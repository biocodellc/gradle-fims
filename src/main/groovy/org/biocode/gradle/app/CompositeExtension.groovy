package org.biocode.gradle.app

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency

/**
 * Composite plugin API definition. Defines entry point for calling Composite methods in Gradle files (e.g., composite.resolveDependency()).
 */
class CompositeExtension {
    private Project project
    boolean resolvedProject = false

    public void setProject(Project project) {
        this.project = project
    }

    /**
     * Define a Method for resolving dependencies as projects if possible. Otherwise the passed in dependency notation
     * (group/name/version map) is used to resolve the dependency.
     * @param dependencyNotation The map of the dependency in question, separated properly
     * @param projectPath The path of the project. Usually set in gradle.properties
     */
    Dependency resolveDependency(Object dependencyNotation, String projectPath) {
        return DependencyResolver.resolveDependency(dependencyNotation, project, projectPath)
    }
}
