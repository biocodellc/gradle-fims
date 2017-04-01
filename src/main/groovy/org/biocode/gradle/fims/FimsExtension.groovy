package org.biocode.gradle.fims

import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested

/**
 * @author rjewing
 */
class FimsExtension {
    @Internal Project project
    @Nested FimsMavenCredentials maven = new FimsMavenCredentials()

    FimsExtension(Project project) {
        this.project = project
    }

    void maven(Closure block) {
        this.project.configure(this.maven, block)
    }

    MavenArtifactRepository mavenPublish() {
        credentialsSet()
        MavenArtifactRepository repo = project.repositories.maven {
            url "http://repo.biocodellc.com/repository/maven-${this.project.version.toString().contains('dev') ? 'dev-releases' : 'releases'}"
            credentials {
                username this.maven.username
                password this.maven.password
            }
        }
        return repo
    }

    MavenArtifactRepository mavenFims() {
        credentialsSet()
        MavenArtifactRepository repo = project.repositories.maven {
            url "http://repo.biocodellc.com/repository/maven-private"
            credentials {
                username this.maven.username
                password this.maven.password
            }
        }
        return repo
    }

    private boolean credentialsSet() {
        if (!maven.username || !maven.password) {
            throw new InvalidUserDataException("fims.maven.username and fims.maven.password must be configured before you can use this method.")
        }
    }
}
