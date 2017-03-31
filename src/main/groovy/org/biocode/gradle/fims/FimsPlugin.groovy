package org.biocode.gradle.fims

import org.ajoberstar.grgit.Grgit
import org.biocode.gradle.app.CompositeExtension
import org.biocode.gradle.fims.tasks.IntegrationTestJarTask
import org.biocode.gradle.fims.tasks.IntegrationTestTask
import org.biocode.gradle.fims.tasks.VerifyMasterBranch
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.credentials.Credentials
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.testing.Test

/**
 * @author rjewing
 */
class FimsPlugin implements Plugin<Project> {
    static final Logger log = Logging.getLogger(FimsPlugin)
    static final String DEFAULT_GROUP = "org.biocode"

    @Override
    void apply(Project project) {
        project.extensions.create("fims", FimsExtension)
        CompositeExtension compositeExtension = project.extensions.create("composite", CompositeExtension)
        compositeExtension.setProject(project)

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

        project.repositories {
            maven {
                url "http://www.repo.biocodellc.com/repository/maven-private/"
                credentials {
                    username project.fims.mavenUser
                    password project.fims.mavenPass
                }
            }
        }
    }

    def configureRelease(Project project) {
        project.plugins.apply("maven-publish")
        project.plugins.apply("org.ajoberstar.release-opinion")

        if (!project.group) {
            log.debug "Setting group from: '${project.group}' to: '${DEFAULT_GROUP}'."
            project.group = DEFAULT_GROUP
        }

        project.publishing {
            repositories {
                maven {
                    url "http://www.repo.biocodellc.com/repository/maven-${project.version.toString().contains('dev') ? 'dev-releases' : 'releases'}"
                    credentials {
                        username project.fims.mavenUser
                        password project.fims.mavenPass
                    }
                }
            }
        }

        project.release {
            grgit = Grgit.open(currentDir: project.file('.'))
        }

        project.tasks.release.dependsOn "build", "publish"
    }
}
