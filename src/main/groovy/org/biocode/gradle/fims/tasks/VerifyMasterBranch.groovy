package org.biocode.gradle.fims.tasks

import org.ajoberstar.grgit.Grgit
import org.gradle.api.DefaultTask
import org.gradle.api.GradleScriptException
import org.gradle.api.tasks.TaskAction

/**
 * @author rjewing
 */
class VerifyMasterBranch extends DefaultTask {

    @TaskAction
    def verify() {
        def repo = Grgit.open(dir: project.file('.'))
        if (repo.branch.current.name != "master")

            if (!project.hasProperty("ignore")) {
                throw new GradleScriptException(project.name + ' is not on the master branch. To ignore, call the task with the arg -Pignore', null)
            }
    }
}
