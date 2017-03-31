package org.biocode.gradle.web.tasks

/**
 * @author rjewing
 */
class FimsDevDeployTask extends RemoteDeployTask {
    FimsDevDeployTask() {
        super()

        workingDir = "/tmp/${project.name}-dev/"

        if (this.name in project.gradle.startParameter.taskNames) {
            project.ext.environment = "development"
        }
    }
}
