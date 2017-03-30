package org.biocode.gradle.app

import org.gradle.api.GradleScriptException
import org.gradle.api.Project
import org.gradle.api.artifacts.component.ProjectComponentSelector

/**
 * @author rjewing
 */
class ForceJarsResolver {

    static forceJars(Project project, String taskName) {
        if (taskName in project.gradle.startParameter.taskNames && project.composite.resolvedProject) {
            println "\n"
            def msg = "The task [${taskName}] can not be run with resolved project dependencies. Would you like to force the use of the jar dependencies?"

            project.ant.input(message: msg, validargs: 'y,n', addproperty: 'forceJars')

            if (project.ant.forceJars == 'n') {
                throw new GradleScriptException("This task cannot be run with project dependencies. Either call the task with \"-PforceJars\", or accept the previous prompt.", null)
            }

            project.configurations.each { cfg ->
                cfg.resolutionStrategy.dependencySubstitution.all {
                    if (it.target instanceof ProjectComponentSelector) {
                        it.useTarget it.oldRequested.toString()
                    }
                }
            }

        }
    }
}
