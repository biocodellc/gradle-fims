package org.biocode.gradle.fims

import org.ajoberstar.gradle.git.release.base.ReleasePluginExtension
import org.ajoberstar.gradle.git.release.base.ReleaseVersion
import org.ajoberstar.gradle.git.release.base.VersionStrategy
import org.ajoberstar.gradle.git.release.semver.SemVerStrategy
import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.Grgit
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Temp class addressing https://issues.gradle.org/browse/GRADLE-2754. Can be removed when
 * https://github.com/ajoberstar/gradle-git/issues/252 is resolved
 */
class RebuildVersionStrategyFix implements VersionStrategy {
    private static final Logger logger = LoggerFactory.getLogger(RebuildVersionStrategyFix)
    public static final RebuildVersionStrategyFix INSTANCE = new RebuildVersionStrategyFix()

    private RebuildVersionStrategyFix() {
        // just hiding the constructor
    }

    @Override
    String getName() {
        return 'rebuild'
    }

    @Override
    boolean selector(Project project, Grgit grgit) {
        def clean = grgit.status().clean
        def props = project.hasProperty(SemVerStrategy.SCOPE_PROP) || project.hasProperty(SemVerStrategy.STAGE_PROP)
        def headVersion = getHeadVersion(project, grgit)

        if (clean && !props && headVersion) {
            logger.info('Using {} strategy because repo is clean, no "release." properties found and head version is {}', name, headVersion)
            return true
        } else {
            logger.info('Skipping {} strategy because clean is {}, "release." properties are {} and head version is {}', name, clean, props, headVersion)
            return false
        }
    }

    /**
     * Infers the version based on the version tag on the current HEAD with the
     * highest precendence.
     */
    @Override
    ReleaseVersion infer(Project project, Grgit grgit) {
        String version = getHeadVersion(project, grgit)
        def releaseVersion = new ReleaseVersion(version, version, false)
        logger.debug('Inferred version {} by strategy {}', releaseVersion, name)
        return releaseVersion
    }

    private String getHeadVersion(Project project, Grgit grgit) {
        def tagStrategy = project.extensions.getByType(ReleasePluginExtension).tagStrategy
        Commit head = grgit.head()
        return grgit.tag.list().findAll {
            it.commit == head
        }.collect {
            tagStrategy.parseTag(it)
        }.findAll {
            it != null
        }.max()?.toString()
    }
}
