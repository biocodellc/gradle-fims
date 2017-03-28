package org.biocode.gradle.fims

import org.biocode.gradle.fims.tasks.VerifyMasterBranch
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author rjewing
 */
class FimsPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        configureDependencies(project)

        project.task("verifyMasterBranch", type: VerifyMasterBranch)
    }

    void configureDependencies(final Project project) {
        project.plugins.apply("java")

        project.configurations {
            server
        }

        project.targetCompatibility = 1.8
        project.sourceCompatibility = 1.8
    }
}
