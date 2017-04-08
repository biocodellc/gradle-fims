package org.biocode.gradle.app

import org.biocode.gradle.app.tasks.CopyEnvironmentConfigurationTask
import org.biocode.gradle.app.tasks.SelectEnvironmentTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author rjewing
 */
class FimsAppPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("environments", EnvironmentExtension)

        project.plugins.apply("org.biocode.fims")

        project.task("selectEnvironment", type: SelectEnvironmentTask)
        project.task("copyEnvironmentFiles", type: CopyEnvironmentConfigurationTask)
    }
}
