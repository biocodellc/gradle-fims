package org.biocode.gradle.web.tasks

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author rjewing
 */
class FimsMinifyJsTask extends DefaultTask {
    String group = "Fims"
    String description = "Minifies and aggregates all js files"

    FimsMinifyJsTask() {
        dependsOn "addProdJsLibs"
        dependsOn "jsExternalLibs"
        dependsOn "jsApp"

        project.tasks.getByName("war").dependsOn this
    }

    @TaskAction
    def run() {
        writeSourceMap(project.tasks.getByName("jsExternalLibs"))
        writeSourceMap(project.tasks.getByName("jsApp"))
    }

    def writeSourceMap(minifyJsTask) {
        project.file(minifyJsTask.dest) << "\n//# sourceMappingURL=" + minifyJsTask.sourceMap.name

        def sources = []

        minifyJsTask.source.files.each { file ->
            sources << ".." + file.canonicalPath.minus(project.webAppDir)
        }

        Map srcMap = new JsonSlurper().parseText(minifyJsTask.sourceMap.text)
        srcMap.sources = sources
        minifyJsTask.sourceMap.withWriter("UTF-8") { it << new JsonBuilder(srcMap).toPrettyString() }

    }
}
