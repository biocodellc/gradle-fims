package org.biocode.gradle.fims.tasks

import org.gradle.jvm.tasks.Jar

/**
 * @author rjewing
 */
class IntegrationTestJarTask extends Jar {
    IntegrationTestJarTask() {
        from project.sourceSets.integrationTest.output
        classifier = 'integration-test'

        dependsOn "integrationTestClasses"
    }
}
