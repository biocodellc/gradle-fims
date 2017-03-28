package org.biocode.gradle.web.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * @author rjewing
 */
class UpdateRemoteDependenciesTask extends DefaultTask {
    String group = "Fims"
    String description = "Update the dependency jars located at the provided location and shh Remote"

    @Input def remote
    @Input String location

    @TaskAction
    def update() {
        ssh.run {
            session(remote) {
                execute "/bin/rm -f " + location + "*"
                put from: project.configurations.server.incoming.getFiles(), into: location
            }
        }
    }

}