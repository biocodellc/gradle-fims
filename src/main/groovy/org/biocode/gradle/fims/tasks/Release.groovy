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
        // : prefix only executes on root project
        dependsOn ":build", ":reckonTagCreate", ":reckonTagPush", ":publish"

        project.afterEvaluate {
            ForceJarsResolver.forceJars(project, this.name)
        }
    }
}
