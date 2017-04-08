package org.biocode.gradle.web.tasks

import com.eriwen.gradle.js.tasks.MinifyJsTask

/**
 * @author rjewing
 */
class MinifyAppJsTask extends MinifyJsTask {
    MinifyAppJsTask() {
        dependsOn "populateJsProps"
        dest = "${project.buildDir}/js/all.min.js"
        sourceMap = project.file("${project.buildDir}/js/all.min.map")
    }

}
