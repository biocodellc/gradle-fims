package org.biocode.gradle.app

import org.gradle.api.Project
import org.gradle.api.UnknownProjectException
import org.gradle.api.artifacts.Dependency
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

/**
 * @author rjewing
 */
class DependencyResolver {
    static final Logger log = Logging.getLogger(DependencyResolver)

    /**
     * Define a Method for resolving dependencies as projects if possible. Otherwise the passed in dependency notation
     */
    static Dependency resolveDependency(Object dependencyNotation, Project project, String projectPath) {
        def forceJars = project.hasProperty("forceJars")
        log.info("resolveDependency forceJars: ${forceJars}, dependencyNotation: ${dependencyNotation}, projectPath: ${projectPath}")

        def dependency = project.dependencies.create(dependencyNotation)

    if (!forceJars && projectPath) {

        Project childProject
        try {
            childProject = project.project(":${projectPath}")
        } catch (UnknownProjectException e) {
            // No local project, use maven repository for dependency resolution. This is not an error condition but an expect
            childProject = null
        }

        if (childProject) {
            project.configurations.each { cfg ->
                cfg.resolutionStrategy.dependencySubstitution {
                    substitute it.module(dependencyNotation) with it.project(childProject.path)
                }
            }
            if (log.isTraceEnabled()) {
                childProject.tasks.each { log.trace(it) }
            }
            log.quiet("Found non-jar project dependency: " + childProject)
            project.composite.resolvedProject = true
        }
    }

        return dependency
    }
}
