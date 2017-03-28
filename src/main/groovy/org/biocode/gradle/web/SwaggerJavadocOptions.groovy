package org.biocode.gradle.web

import org.gradle.api.Project
import org.gradle.external.javadoc.StandardJavadocDocletOptions

/**
 * @author rjewing
 */
class SwaggerJavadocOptions extends StandardJavadocDocletOptions {

    SwaggerJavadocOptions(Project project, SwaggerConfig config) {
        super()

        this.setDoclet("com.tenxerconsulting.swagger.doclet.ServiceDoclet")
        this.addStringOption("host", "")
        this.addBooleanOption("skipUiFiles", true)

        project.afterEvaluate {
            this.setClasspath(project.configurations.doclet.files.asType(List))
            this.setDocletpath(project.configurations.doclet.files.asType(List))

            if (config.apiVersions) {
                this.addStringOption("apiVersion", config.apiVersions[-1])
                this.addStringOption("apiBasePath", config.apiBasePath + config.apiVersions[-1])
            }

            this.addStringOption("apiInfoFile", config.apiInfo)
        }
    }

}
