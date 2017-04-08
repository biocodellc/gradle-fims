package org.biocode.gradle.app.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.execution.commandline.TaskConfigurationException

/**
 * @author rjewing
 */
class SelectEnvironmentTask extends DefaultTask {
    @Internal String description = "Internal task used to set the environment"

    @TaskAction
    def selectEnvironment() {

        if (!project.hasProperty("environment")) {
            project.ext.environment = project.environments.defaultEnv
        }

        logger.quiet("Environment is set to ${project.environment}")
    }

    public static setEnvironment(Project project, Task task, String env) {
        project.gradle.taskGraph.whenReady {
            if (it.hasTask(task)) {
                project.ext.environment = env
            }
        }

    }
}
