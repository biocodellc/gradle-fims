package org.biocode.gradle.web.tasks

/**
 * @author rjewing
 */
class FimsDeployTask extends RemoteDeployTask {
    FimsDeployTask() {
        super()

        workingDir = "/tmp/${project.name}-prod"
        project.ext.environment = "production"

        dependsOn "verifyMasterBranch"
    }
}
