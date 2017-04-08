package org.biocode.gradle.web.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction

import java.util.regex.Pattern

/**
 * @author rjewing
 */
class SetupAppConstantsTask extends DefaultTask {

    @Input
    @SkipWhenEmpty
    Map propsMap
    @InputFile
    @SkipWhenEmpty
    File templateFile
    @InputFile
    @SkipWhenEmpty
    File propsFile
    @OutputFile
    File constantsFile

    @TaskAction
    def setup() {
        if (templateFile.exists() && propsFile.exists()) {
            
            def props = new Properties()
            propsFile.withInputStream { props.load(it) }

            def constantsReplacement = templateFile.getText()

            propsMap.each { k, v ->
                constantsReplacement = constantsReplacement.replaceAll('\\{' + k + '}', props.getProperty(v))
            }

            constantsFile.setText(constantsReplacement, 'UTF-8')
        }

    }
}
