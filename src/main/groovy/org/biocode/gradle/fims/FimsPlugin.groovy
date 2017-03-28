package org.biocode.gradle.fims

import org.ajoberstar.grgit.Grgit
import org.biocode.gradle.fims.tasks.IntegrationTestJarTask
import org.biocode.gradle.fims.tasks.IntegrationTestTask
import org.biocode.gradle.fims.tasks.VerifyMasterBranch
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

/**
 * @author rjewing
 */
class FimsPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        configureDefaults(project)
        configureTests(project)
        configureRelease(project)

        project.task("verifyMasterBranch", type: VerifyMasterBranch)
    }

    def configureDefaults(Project project) {
        project.plugins.apply("java")
        project.plugins.apply("idea")

        project.targetCompatibility = 1.8
        project.sourceCompatibility = 1.8

        project.compileJava {
            options.fork = true
            options.forkOptions.jvmArgs += ["-parameters"]
            options.forkOptions.executable = 'javac'
        }

        project.configurations {
            provided
            server
        }

        project.sourceSets {
            main {
                compileClasspath += project.configurations.server
                compileClasspath += project.configurations.provided
            }
        }

        project.idea {
            module {
                scopes.PROVIDED.plus += [project.configurations.provided]
                scopes.COMPILE.plus += [project.configurations.server]
            }
        }
    }

    def configureTests(Project project) {
        project.configurations {
            testCompile.extendsFrom provided
            testCompile.extendsFrom server
            integrationTestCompile.extendsFrom testCompile
            integrationTestRuntime.extendsFrom testRuntime
            integrationTestOutput.extendsFrom integrationTestCompile
        }

        project.sourceSets {
            test {
                compileClasspath += main.output
                runtimeClasspath += main.output
            }
            integrationTest {
                compileClasspath += main.output + test.output
                runtimeClasspath += main.output + test.output
                java.srcDir project.file('src/integration-test/java')
                resources.srcDir project.file('src/integration-test/resources')
            }
        }

        project.task("integrationTest", type: IntegrationTestTask)
        def jarIntegrationTest = project.task("jarIntegrationTest", type: IntegrationTestJarTask)

        project.tasks.withType(Test) {
            reports.html.destination = project.file("${project.reporting.baseDir}/${project.name}")
        }

        project.artifacts {
            integrationTestOutput jarIntegrationTest
        }
    }

    def configureRelease(Project project) {
        project.plugins.apply("maven-publish")
        project.plugins.apply("org.ajoberstar.release-opinion")

        project.group = "org.biocode"

        project.release {
            grgit = Grgit.open(currentDir: project.file('.'))
        }

        project.tasks.release.dependsOn "build", "publish"
    }


}
