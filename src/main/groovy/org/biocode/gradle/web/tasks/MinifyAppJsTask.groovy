package org.biocode.gradle.web.tasks

import com.eriwen.gradle.js.tasks.MinifyJsTask

/**
 * @author rjewing
 */
class MinifyAppJsTask extends MinifyJsTask {
    MinifyAppJsTask() {
        dest = "${project.buildDir}/js/all.min.js"
        sourceMap = project.file("${project.buildDir}/js/all.min.map")
    }

}
