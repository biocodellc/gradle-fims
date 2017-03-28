package org.biocode.gradle

import org.gradle.api.tasks.Input

/**
 * @author rjewing
 */
class SwaggerConfig {
    @Input String[] apiVersions
    @Input String apiBasePath
    @Input String apiInfo
}
