package org.biocode.gradle.web.tasks

import com.eriwen.gradle.js.tasks.MinifyJsTask

/**
 * @author rjewing
 */
class MinifyExternalJsTask extends MinifyJsTask {

    MinifyExternalJsTask() {
        dest = project.file("${project.buildDir}/js/allExternalLibs.min.js")
        sourceMap = project.file("${project.buildDir}/js/allExternalLibs.min.map")
    }
}
