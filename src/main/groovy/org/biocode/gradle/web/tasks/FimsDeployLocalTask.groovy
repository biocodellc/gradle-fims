package org.biocode.gradle.web.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * @author rjewing
 */
class FimsDeployLocalTask extends DefaultTask {
    @Input String deployDir

    FimsDeployLocalTask() {
        dependsOn "fatWar"
        dependsOn "copyEnvironmentFiles"

        if (this.name in project.gradle.startParameter.taskNames) {
            project.ext.environment = "local"
        }
    }

    @TaskAction
    def run() {
        project.copy {
            from project.fatWar.destinationDir
            into deployDir
            include project.fatWar.archiveName
            rename project.fatWar.archiveName, "${project.name}.war"
        }
    }
}
