package org.biocode.gradle.web

import org.biocode.gradle.app.ForceJarsResolver
import org.biocode.gradle.web.tasks.FatWarTask
import org.biocode.gradle.web.tasks.FimsDeployLocalTask
import org.biocode.gradle.web.tasks.FimsDeployTask
import org.biocode.gradle.web.tasks.FimsDevDeployTask
import org.biocode.gradle.web.tasks.FimsMinifyJsTask
import org.biocode.gradle.web.tasks.GenerateRestApiDocsTask
import org.biocode.gradle.web.tasks.MinifyAppJsTask
import org.biocode.gradle.web.tasks.MinifyExternalJsTask
import org.biocode.gradle.web.tasks.RestartRemoteJettyTask
import org.biocode.gradle.web.tasks.SetupAppConstantsTask
import org.biocode.gradle.web.tasks.UpdateRemoteDependenciesTask
import org.biocode.gradle.web.tasks.WebJsLibTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * @author rjewing
 */
class FimsWebAppPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        configureDependencies(project)
        configureDefaults(project)

        addUpdateDependenciesTask(project)

        if (project.file('src/main/web/js').exists()) {
            addProdJsLibTask(project)
            addDevJsLibTask(project)
            project.task("jsExternalLibs", type: MinifyExternalJsTask)
            project.task("jsApp", type: MinifyAppJsTask)
            project.task("populateJsProps", type: SetupAppConstantsTask)
            project.task("minifyJs", type: FimsMinifyJsTask, overwrite: true)
        }

        project.task("generateRestApiDocs", type: GenerateRestApiDocsTask)
        project.task("updateDependenciesDev", type: UpdateRemoteDependenciesTask)
        project.task("fatWar", type: FatWarTask)
        project.task("deployFims", type: FimsDeployTask)
        project.task("deployFimsDev", type: FimsDevDeployTask)
        project.task("deployFimsLocal", type: FimsDeployLocalTask)
    }

    void configureDependencies(final Project project) {
        project.plugins.apply("war")
        project.plugins.apply("org.hidetake.ssh")
        project.plugins.apply("org.biocode.fims-app")

        if (project.file('src/main/web/js').exists()) {
            project.plugins.apply("com.eriwen.gradle.js")
        }


        project.configurations {
            additionalSources
            doclet.extendsFrom server
        }

        project.dependencies {
            doclet group: 'com.tenxerconsulting', name: 'swagger-doclet', version: '2.0.0-beta.1'
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

    void addProdJsLibTask(project) {
        Task t = project.task('addProdJsLibs', type: WebJsLibTask, group: 'Fims', description: 'Modify index.html to include the combined, minified, and compressed js files') {
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


    private void addUpdateDependenciesTask(Project project) {
        Task upDeps = project.task("updateDependencies", type: UpdateRemoteDependenciesTask)
        upDeps.dependsOn("verifyMasterBranch")
        project.afterEvaluate {
            ForceJarsResolver.forceJars(project, upDeps.name)
        }
    }

}
