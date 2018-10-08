package org.biocode.gradle.web

import org.biocode.gradle.web.tasks.FimsDeployLocalTask
import org.biocode.gradle.web.tasks.FimsWarTask
import org.biocode.gradle.web.tasks.GenerateRestApiDocsTask
import org.biocode.gradle.web.tasks.SetupAppConstantsTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author rjewing
 */
class FimsWebAppPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        configureDependencies(project)
        configureDefaults(project)

        if (project.file('src/main/web/js').exists()) {
            project.task("populateJsProps", type: SetupAppConstantsTask)
        }

        project.task("generateRestApiDocs", type: GenerateRestApiDocsTask)
        project.task("war", type: FimsWarTask, overwrite: true)
        project.task("deployFimsLocal", type: FimsDeployLocalTask)
    }

    void configureDependencies(final Project project) {
        project.plugins.apply("war")
        project.plugins.apply("org.biocode.fims-app")

        project.configurations {
            additionalSources
            doclet.extendsFrom compile
        }

        project.dependencies {
            doclet group: 'com.tenxerconsulting', name: 'swagger-doclet', version: '2.0.0-beta.3'
            doclet group: 'javax.ws.rs', name: 'javax.ws.rs-api', version: '2.0.1'
            doclet group: 'io.swagger', name: 'swagger-models', version: '1.5.12'
        }
    }

    void configureDefaults(final Project project) {
        project.sourceSets {
            doclet {
                java {
                    srcDir 'src/main/java'
                }
            }
        }

        project.war {
            archiveName "${project.name}.war"
            webInf {
                from "${project.buildDir}/js"
                into "/js/"
            }
        }

        project.webAppDirName = "src/main/web"
        project.libsDirName = "../dist"

        project.clean {
            delete project.libsDir
        }
    }
}
