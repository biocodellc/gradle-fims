package org.biocode.gradle.fims

import org.biocode.gradle.app.CompositeExtension
import org.biocode.gradle.fims.tasks.IntegrationTestJarTask
import org.biocode.gradle.fims.tasks.IntegrationTestTask
import org.biocode.gradle.fims.tasks.Release
import org.biocode.gradle.fims.tasks.VerifyMasterBranch
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.api.publish.maven.tasks.GenerateMavenPom
import org.gradle.api.publish.plugins.PublishingPlugin
import org.gradle.api.tasks.testing.Test
import org.gradle.model.Finalize
import org.gradle.model.ModelMap
import org.gradle.model.RuleSource

/**
 * @author rjewing
 */
class FimsPlugin implements Plugin<Project> {
    static final Logger log = Logging.getLogger(FimsPlugin)
    static final String DEFAULT_GROUP = "org.biocode"

    @Override
    void apply(Project project) {
        project.extensions.create("fims", FimsExtension, project)
        project.extensions.create("composite", CompositeExtension, project)

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
            serverRuntime
        }

        project.sourceSets {
            main {
                compileClasspath += project.configurations.server
                compileClasspath += project.configurations.provided
                runtimeClasspath += project.configurations.serverRuntime
            }
        }

        project.jar {
            archiveName "${project.name}.jar"
        }

        project.idea {
            module {
                scopes.PROVIDED.plus += [project.configurations.provided]
                scopes.COMPILE.plus += [project.configurations.server]
                scopes.RUNTIME.plus += [project.configurations.serverRuntime]
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

        if (project.rootProject == project) {
            project.plugins.apply("org.ajoberstar.grgit")
            project.plugins.apply("org.ajoberstar.reckon")

            if (!project.group) {
                log.debug "Setting group from: '${project.group}' to: '${DEFAULT_GROUP}'."
                project.group = DEFAULT_GROUP
            }

            project.publishing {
                repositories {
                    add(project.fims.mavenPublish())
                }
                publications {
                    java(MavenPublication) {
                        from project.components.java
                        pom.withXml {
                            def dependenciesNode = asNode().dependencies[0] ?: asNode().appendNode("dependencies")

                            // gradle puts all dependencies as runtime. we need to replace the scope with compile if
                            // the dependency is in the compile configuration
                            dependenciesNode.findAll() {
                                it.scope.text() == 'runtime' && project.configurations.compile.allDependencies.find { dep ->
                                    dep.name == it.artifactId.text()
                                }
                            }.each { it.scope*.value = 'compile'}

                            // add all server configuration deps to the compile scope
                            project.configurations.server.allDependencies.each {
                                def dependencyNode = dependenciesNode.appendNode('dependency')
                                dependencyNode.appendNode('groupId', it.group)
                                dependencyNode.appendNode('artifactId', it.name)
                                dependencyNode.appendNode('version', it.version)
                                dependencyNode.appendNode('scope', "compile")
                            }

                            // add all provided configuration deps to the provided scope
                            project.configurations.provided.allDependencies.each {
                                def dependencyNode = dependenciesNode.appendNode('dependency')
                                dependencyNode.appendNode('groupId', it.group)
                                dependencyNode.appendNode('artifactId', it.name)
                                dependencyNode.appendNode('version', it.version)
                                dependencyNode.appendNode('scope', "provided")
                            }

                        }
                    }
                }
            }

            // ex. ./gradlew reckonTagCreate -Preckon.scope=minor -Preckon.stage=final
            project.reckon {
                normal = scopeFromProp()
                preRelease = stageFromProp('beta', 'rc', 'final')
            }
            
            project.task("release", type: Release)
        } else {
            log.quiet("Skipping default fims release configuration for non rootProject: ${project.name}")
        }
    }

    static class Rules extends RuleSource {
        @Finalize
        public void removeChildProjectPublishingTasks(ModelMap<Task> tasks) {
            removeChildProjectTask(tasks.get(PublishingPlugin.PUBLISH_LIFECYCLE_TASK_NAME))
            removeChildProjectTask(tasks.get(MavenPublishPlugin.PUBLISH_LOCAL_LIFECYCLE_TASK_NAME))
            for (Task t : tasks) {
                switch (t.class) {
                    case AbstractPublishToMaven.class:
                    case GenerateMavenPom.class:
                        removeChildProjectTask(t)
                }
            }
        }

        private void removeChildProjectTask(Task t) {
            if (t.project != t.project.rootProject) {
                log.info("Removing childProject task [${t.name}]. To run this task, change to the project's directory: ${t.project.projectDir}")
                t.project.tasks.remove(t)
            }
        }
    }
}
