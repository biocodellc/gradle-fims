package org.biocode.gradle.web

import org.gradle.api.Project
import org.gradle.external.javadoc.StandardJavadocDocletOptions

/**
 * @author rjewing
 */
class SwaggerJavadocOptions extends StandardJavadocDocletOptions {

    SwaggerJavadocOptions(Project project, SwaggerConfig config) {
        super()

        this.setDoclet("com.tenxerconsulting.swagger.doclet.ServiceDoclet")
        this.addStringOption("host", "")
        this.addBooleanOption("skipUiFiles", true)

        project.gradle.projectsEvaluated {
            // fo some reason, we can't detect if classpath or docletpath have been set in the build.gradle file
            // if we need to set these in the build.gradle, we need to move this to GenerateRestApiDocsTask
            // where we can detect if they have been overridden in build.gradle
            this.setClasspath(project.configurations.doclet.files.asType(List))
            this.setDocletpath(project.configurations.doclet.files.asType(List))

            if (config.apiVersions) {
                this.addStringOption("apiVersion", config.apiVersions[-1])
                this.addStringOption("apiBasePath", config.apiBasePath + config.apiVersions[-1])
            }

            this.addStringOption("apiInfoFile", config.apiInfo)
        }
    }

}
