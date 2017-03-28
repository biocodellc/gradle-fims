package org.biocode.gradle.app.tasks

import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author rjewing
 */
class CopyEnvironmentConfigurationTask extends DefaultTask {
    String group = "Fims"
    String description = "Generate Swagger rest documentation json file"

    CopyEnvironmentConfigurationTask() {
        dependsOn "selectEnvironment"
        project.tasks.getByName("processResources").dependsOn this
    }

    @TaskAction
    def copyFiles() {
        cleanResourceDirectory()
        project.copy {
            from "${project.environments.environmentDir}/${project.environment}"
            into "${project.webAppDirName}/WEB-INF"
            include "web.xml"
        }

        project.copy {
            from "${project.environments.environmentDir}/${project.environment}"
            into "src/main/resources"
            include "**/*"
            exclude "web.xml"
            exclude ".gitkeep"
        }
    }

    /**
     * Delete any resource files in src/main/resources if the file exists in a sub directory
     * of src/main/environment, excluding the current environment setting directory
     * @return
     */
    def cleanResourceDirectory() {
        def allResourceFilesToRemove = []

        def environmentDir = project.file(project.environments.environmentDir)
        environmentDir.eachFileRecurse(FileType.FILES) { file ->
            allResourceFilesToRemove << file.name
        }

        allResourceFilesToRemove.each {
            def resourceFile = new File("src/main/resources/${it}")
            if (resourceFile.exists()) {
                resourceFile.delete()
            }
        }
    }
}
