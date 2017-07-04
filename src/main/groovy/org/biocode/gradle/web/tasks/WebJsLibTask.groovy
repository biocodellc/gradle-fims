package org.biocode.gradle.web.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*

/**
 * @author rjewing
 */
class WebJsLibTask extends DefaultTask {
    private final def tag = "<!-- LIBRARY LOADING -->"
    private final def endTag = "<!-- END: LIBRARY LOADING -->"

    @InputFile
    File html

    @Input
    String environment

    @OutputFile
    @Optional
    File outputHtml

    final static def PROD_INCLUDE = """
    <!-- use minified and combined js file FOR PROD -->
    <script src="js/allExternalLibs.min.js"></script>
    <script src="js/all.min.js"></script>"""

    @TaskAction
    void applyScriptAndStylesheetTags() {
        def htmlReplacement = html.getText().replaceAll(
                "$tag\\p{all}*$endTag",
                "$tag${getHtmlReplacement(environment)}$endTag")
        if (outputHtml == null) outputHtml = html
        outputHtml.setText(htmlReplacement, 'UTF-8')
    }

    private String getHtmlReplacement(String environment) {
        switch (environment) {
            case 'dev':
                return getDevHtml()
            default:
                return PROD_INCLUDE
        }
    }

    private String getDevHtml() {
        String html = "\n"

        project.jsExternalLibs.source.files.each { file ->
            html += "<script src=\"" + file.canonicalPath.minus(project.projectDir.canonicalPath + "/" + project.webAppDirName + "/") + "\"></script>\n";
        }
        project.jsApp.source.files.each { file ->
            html += "<script src=\"" + file.canonicalPath.minus(project.projectDir.canonicalPath + "/" + project.webAppDirName + "/") + "\"></script>\n";
        }

        return html
    }
}
