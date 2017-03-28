package org.biocode.gradle.fims

import org.gradle.api.tasks.Input

/**
 * @author rjewing
 */
class EnvironmentExtension {
    @Input String environmentDir = "src/main/environment"
    @Input String defaultEnv = "local"
}
