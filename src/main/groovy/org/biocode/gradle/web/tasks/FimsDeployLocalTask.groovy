package org.biocode.gradle.web.tasks

import org.biocode.gradle.app.tasks.SelectEnvironmentTask
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * @author rjewing
 */
class FimsDeployLocalTask extends DefaultTask {
    @Input String deployDir

    FimsDeployLocalTask() {
        dependsOn "cleanWar"
        dependsOn "war"
        dependsOn "copyEnvironmentFiles"

        SelectEnvironmentTask.setEnvironment(project, this, "local")
    }

    @TaskAction
    def run() {
        project.copy {
            from project.war.destinationDir
            into deployDir
            include project.war.archiveName
            rename project.war.archiveName, "${project.name}.war"
        }
    }
}
