package org.biocode.gradle.web.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * @author rjewing
 */
class HotDeployRemoteJettyTask extends DefaultTask {
    @Internal String description = "Restart jetty instance"

    @Input String remoteWarDir
    @Input def remote

    @TaskAction
    def restart() {
        project.ssh.run {
            session(remote) {
                def xml = project.war.archiveName.take(project.war.archiveName.lastIndexOf('.')) + ".xml"
                executeSudo "touch ${remoteWarDir}${xml}", pty: true
            }
        }
    }
}
