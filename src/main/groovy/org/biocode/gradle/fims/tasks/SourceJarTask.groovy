package org.biocode.gradle.fims.tasks

import org.gradle.jvm.tasks.Jar

/**
 * @author rjewing
 */
class SourceJarTask extends Jar {
    SourceJarTask() {
        from project.sourceSets.main.allJava
        classifier = 'sources'
    }
}
