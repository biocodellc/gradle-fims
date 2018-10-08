package org.biocode.gradle.web.tasks

import org.biocode.gradle.app.ForceJarsResolver
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.bundling.War

/**
 * @author rjewing
 */
class FimsWarTask extends War {
    FimsWarTask() {
        project.afterEvaluate {
            if (project.ext.environment == 'production') {
                ForceJarsResolver.forceJars(project, this.name)
            }
        }
        archiveName = project.name + ".war"
    }
}
