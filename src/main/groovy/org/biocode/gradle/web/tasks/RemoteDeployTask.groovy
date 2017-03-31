package org.biocode.gradle.web.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * @author rjewing
 */
abstract class RemoteDeployTask extends DefaultTask {
    @Internal String group = "Fims"

    @Input String workingDir
    @Input String remoteLibsDir
    @Input String remoteWarDir
    @Input def remote

    RemoteDeployTask() {
        dependsOn "war"
        dependsOn "copyEnvironmentFiles"
    }

    @TaskAction
    def deploy() {
        project.ssh.run {
            session(remote) {
                // Cleanup to begin
                execute "/bin/rm -rf " + workingDir
                execute "/bin/mkdir " + workingDir
                // Copy war file to server
                put from: project.war.archivePath.path, into: workingDir
                // Extract libs and copy from server
                execute "/usr/bin/unzip -d " + workingDir + " " + workingDir + project.war.archiveName
                execute "/bin/cp " + remoteLibsDir + "* " + workingDir + "WEB-INF/lib/"
                // Remove the old war file
                execute "/bin/rm " + workingDir + project.war.archiveName
                // Need to remove the war file itself-- we instead just copy it into /tmp for safekeeping in case next step fails
                execute "/bin/rm -fr " + remoteWarDir + "biscicol.war"
                // Need to execute zip command from the working Directory in order to get relative paths correct
                execute "cd " + workingDir + ";/usr/bin/zip -r " + remoteWarDir + "biscicol.war " + " ."
                // Cleanup
                execute "/bin/rm -rf " + workingDir
            }
        }
    }
}
