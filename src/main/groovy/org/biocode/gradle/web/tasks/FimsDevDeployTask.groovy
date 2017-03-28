package org.biocode.gradle.web.tasks

/**
 * @author rjewing
 */
class FimsDevDeployTask extends RemoteDeployTask {
    FimsDevDeployTask() {
        super()

        workingDir = "/tmp/${project.name}-dev"
        project.ext.environment = "development"
    }
}
