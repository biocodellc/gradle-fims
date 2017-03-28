package org.biocode.gradle.app.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.execution.commandline.TaskConfigurationException

/**
 * @author rjewing
 */
class SelectEnvironmentTask extends DefaultTask {
    String description = "Internal task used to set the environment"

    @TaskAction
    def selectEnvironment() {

        if (!project.hasProperty("environment")) {
            project.ext.environment = project.environments.defaultEnv
        }

        if (!project.file("${project.environments.environmentDir}/${project.environment}").exists()) {
            throw new TaskConfigurationException(this.path, "Environment was set to ${project.environmen} however " +
                    "the directory ${project.environments.environmentDir}/${project.environment} doesn't exist")
        }

        logger.info("Environment is set to ${project.environment}")
    }
}
