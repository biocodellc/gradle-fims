package org.biocode.gradle.web.tasks

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.biocode.gradle.app.ForceJarsResolver
import org.biocode.gradle.web.SwaggerConfig
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.javadoc.Javadoc

/**
 * @author rjewing
 */
class GenerateRestApiDocsTask extends Javadoc {
    @Internal
    String group = "Fims"
    @Internal
    String description = "Generate Swagger rest documentation json file"

    private File destinationDir = project.file("${project.docsDir}/rest-api-docs")

    @Optional
    List<String> additionalSourcesInclude = []
    @Optional
    List<String> additionalSourcesExclude = []

    @Nested
    SwaggerConfig swagger = new SwaggerConfig()

    GenerateRestApiDocsTask() {
        options {
            doclet "com.tenxerconsulting.swagger.doclet.ServiceDoclet"
            addStringOption("host", "")
            addBooleanOption("skipUiFiles", true)

            if (swagger.apiVersions) {
                addStringOption("apiVersion", swagger.apiVersions[-1])
                addStringOption("apiBasePath", swagger.apiBasePath + swagger.apiVersions[-1])
            }

            addStringOption("apiInfoFile", swagger.apiInfo)
        }

        project.afterEvaluate {
            ForceJarsResolver.forceJars(project, this.name)
        }

        project.gradle.projectsEvaluated {
            if (this.source.isEmpty()) {
                def tree = project.fileTree(dir: "${project.buildDir}/additional-sources")
                this.setSource([project.sourceSets.doclet.allJava, tree])
            }

            if (!options.classpath) {
                options.classpath project.configurations.doclet.files.asType(List)
            }

            if (!options.docletpath) {
                options.docletpath = project.configurations.doclet.files.asType(List)
            }
        }
    }

    @TaskAction
    @Override
    void generate() {
        if (!swagger.apiVersions) {
            throw new InvalidUserDataException("The property swagger.apiVersions must specify at least 1 version")
        }

        def files = []
        project.configurations.additionalSources.files.each {
            files.add(project.zipTree(it))
        }
        project.copy {
            from files
            into project.file("${project.buildDir}/additional-sources")
            exclude 'org/springframework'
            exclude additionalSourcesExclude
            include 'org/springframework/data/domain/*.java', 'biocode/fims/**/*.class'
            include additionalSourcesInclude
        }

        super.setDestinationDir(destinationDir)
        super.generate()

        def swaggerFile = project.file("${destinationDir}/service.json")
        // add the apiVersions to the swagger file
        def swagger = new JsonSlurper().parseText(swaggerFile.text)
        swagger.info.apiVersions = this.swagger.apiVersions
        swaggerFile.write(new JsonBuilder(swagger).toPrettyString(), 'UTF-8')

        def currentDocsDir = "${project.webAppDir}/apidocs/current"
        if (this.swagger.apiVersions.length > 1) {
            def previousDocsDir = "${project.webAppDir}/apidocs/" + this.swagger.apiVersions[-2]

            // move current docs to next to previous apiVersion. If the directory already exists, then that means we are
            // updating the current docs
            if (!project.file(previousDocsDir).exists()) {
                project.copy {
                    from currentDocsDir
                    into previousDocsDir
                }
            }
        }

        project.copy {
            from destinationDir
            into currentDocsDir
        }

    }

    public void swagger(Closure<?> block) {
        this.project.configure(this.swagger, block)
    }

}
