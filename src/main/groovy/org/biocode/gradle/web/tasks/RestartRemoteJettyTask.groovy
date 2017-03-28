package org.biocode.gradle.web.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * @author rjewing
 */
class RestartRemoteJettyTask extends DefaultTask {
    String description = "Restart jetty instance"

    @Input def remote
    @Input String jettyPath

    @TaskAction
    def restart() {
        project.ssh.run {
            project.session(remote) {
                executeSudo jettyPath + ' restart'
            }
        }
    }
}
