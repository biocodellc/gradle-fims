package org.biocode.gradle

import org.biocode.gradle.tasks.GenerateRestApiDocs
import org.biocode.gradle.tasks.WebJsLibTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author rjewing
 */
class FimsPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        configureDependencies(project)

        addProdJsLibTask(project)
        addDevJsLibTask(project)

        project.task('generateRestApiDocs', type: GenerateRestApiDocs, group: 'Fims')
    }

    void configureDependencies(final Project project) {
        project.configurations {
            additionalSources
            server
            doclet.extendsFrom server
        }

        project.dependencies {
            // TODO add custom swagger-doclet once we setup maven repo
            doclet group: 'javax.ws.rs', name: 'javax.ws.rs-api', version: '2.0.1'
            doclet group: 'io.swagger', name: 'swagger-models', version: '1.5.12'
        }
    }

    void addProdJsLibTask(project) {
        project.task('addProdJsLibs', type: WebJsLibTask, group: 'Fims', description: 'Modify index.html to include the combined, minified, and compressed js files') {
            html = project.file('src/main/web/index.html')
            environment = 'production'
        }
    }

    void addDevJsLibTask(project) {
        project.task('addDevJsLibs', type: WebJsLibTask, group: 'Fims', description: 'Modify index.html to include all js files') {
            html = project.file('src/main/web/index.html')
            environment = 'dev'
        }
    }

}
