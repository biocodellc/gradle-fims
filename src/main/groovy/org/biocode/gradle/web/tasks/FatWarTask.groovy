package org.biocode.gradle.web.tasks

import org.gradle.api.tasks.bundling.War

/**
 * @author rjewing
 */
class FatWarTask extends War {
    String group = "Fims"
    String description = "Creates a .war file with all required dependencies bundled"

    FatWarTask() {
        dependsOn "minifyJs"
        archiveName = project.name + "-fat.war"

        classpath project.configurations.server
        classpath project.configurations.compile

        webInf {
            from "${project.buildDir}/js"
            into "/js/"
        }
    }
}
