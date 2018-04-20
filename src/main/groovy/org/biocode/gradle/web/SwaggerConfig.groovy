package org.biocode.gradle.web

import org.gradle.api.tasks.Input

/**
 * @author rjewing
 */
class SwaggerConfig {
    @Input String[] apiVersions
    @Input List<String> apiSchemes
    @Input String apiBasePath
    @Input String apiInfo
}
