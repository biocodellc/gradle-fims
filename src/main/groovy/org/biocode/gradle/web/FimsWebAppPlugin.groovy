package org.biocode.gradle.web

import org.biocode.gradle.web.tasks.GenerateRestApiDocsTask
import org.biocode.gradle.web.tasks.UpdateRemoteDependenciesTask
import org.biocode.gradle.web.tasks.WebJsLibTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author rjewing
 */
class FimsWebAppPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        configureDependencies(project)

        addProdJsLibTask(project)
        addDevJsLibTask(project)

        project.task("generateRestApiDocs", type: GenerateRestApiDocsTask)
        project.task("updateDependencies", type: UpdateRemoteDependenciesTask).dependsOn("verifyMasterBranch")
        project.task("updateDependenciesDev", type: UpdateRemoteDependenciesTask)
    }

    void configureDependencies(final Project project) {
        project.plugins.apply("org.hidetake.ssh")
        project.plugins.apply("org.biocode.fims")

        project.sourceSets.create("doclet")

        project.configurations {
            additionalSources
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
