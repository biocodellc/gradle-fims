package org.biocode.gradle

import org.biocode.gradle.tasks.WebJsLibTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author rjewing
 */
class FimsPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        addProdJsLibTask(project)
        addDevJsLibTask(project)
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
