package org.biocode.gradle.web.tasks

import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.bundling.War

/**
 * @author rjewing
 */
class FatWarTask extends War {
    @Internal String group = "Fims"
    @Internal String description = "Creates a .war file with all required dependencies bundled"

    FatWarTask() {
        dependsOn "minifyJs"
        archiveName = project.name + "-fat.war"

        classpath project.configurations.server

        webInf {
            from "${project.buildDir}/js"
            into "/js/"
        }
    }
}
