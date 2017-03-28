package org.biocode.gradle.fims

import org.biocode.gradle.fims.tasks.CopyEnvironmentConfigurationTask
import org.biocode.gradle.fims.tasks.SelectEnvironmentTask
import org.biocode.gradle.fims.tasks.VerifyMasterBranch
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author rjewing
 */
class FimsPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("environments", EnvironmentExtension)

        configureDependencies(project)

        project.task("verifyMasterBranch", type: VerifyMasterBranch)
        project.task("selectEnvironment", type: SelectEnvironmentTask)
        project.task("copyEnvironmentFiles", type: CopyEnvironmentConfigurationTask)
    }

    void configureDependencies(final Project project) {
        project.configurations {
            server
        }
    }
}
