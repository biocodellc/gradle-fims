package org.biocode.gradle.fims

import org.gradle.api.tasks.Input

/**
 * @author rjewing
 */
class FimsExtension {
    @Input String mavenUser
    @Input String mavenPass
}
