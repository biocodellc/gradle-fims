package org.biocode.gradle.fims.tasks

import org.gradle.api.tasks.testing.Test

/**
 * @author rjewing
 */
class IntegrationTestTask extends Test {
    IntegrationTestTask() {
        testClassesDir = project.sourceSets.integrationTest.output.classesDir
        classpath = project.sourceSets.integrationTest.runtimeClasspath
        outputs.upToDateWhen { false }

        project.tasks.getByName("check").dependsOn this
        mustRunAfter "test"
    }
}
