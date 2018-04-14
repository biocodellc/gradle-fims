package org.biocode.gradle.fims.tasks

import org.biocode.gradle.app.ForceJarsResolver
import org.gradle.api.DefaultTask

/**
 * https://github.com/ajoberstar/reckon#execute-gradle
 *
 * ex. ./gradlew release -Preckon.scope=minor -Preckon.stage=final
 *
 * @author rjewing
 */
class Release extends DefaultTask {

    Release() {
        // since release plugin is only applied to the root project, we only want to build the root project
        dependsOn ":build", "reckonTagCreate", "reckonTagPush", "publish"

        project.afterEvaluate {
            ForceJarsResolver.forceJars(project, this.name)
        }
    }
}
