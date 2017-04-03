package org.biocode.gradle.web.tasks

import org.biocode.gradle.app.ForceJarsResolver

/**
 * @author rjewing
 */
class FimsDeployTask extends RemoteDeployTask {
    FimsDeployTask() {
        super()

        workingDir = "/tmp/${project.name}-prod/"
        setEnvironment("production")

        dependsOn "verifyMasterBranch"

        project.afterEvaluate {
            ForceJarsResolver.forceJars(project, this.name)
        }
    }
}
