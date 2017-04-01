package org.biocode.gradle.fims

import org.gradle.api.tasks.Input

/**
 * @author rjewing
 */
class FimsMavenCredentials {
    @Input public String username
    @Input public String password

    void username(String username) {
        this.username = username
    }

    void password(String password) {
        this.password = password
    }
}
