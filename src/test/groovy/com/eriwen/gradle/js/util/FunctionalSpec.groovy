package com.eriwen.gradle.js.util

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

abstract class FunctionalSpec extends Specification {
    @Rule
    TemporaryFolder tempProjectDir = new TemporaryFolder()

    File projectDir
    File buildFile

    def setup() {
        projectDir = tempProjectDir.root
        buildFile = tempProjectDir.newFile('build.gradle')

        def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
        if (pluginClasspathResource == null) {
            throw new IllegalStateException("Did not find plugin classpath resource, run 'functionalTestClasses' build task.")
        }

        def pluginClasspath = pluginClasspathResource.readLines()
                .collect { it.replace('\\', '\\\\') } // escape backslashes in Windows paths
                .collect { "'$it'" }
                .join(", ")

        // Add the logic under test to the test build
        buildFile << """
    buildscript {
        dependencies {
            classpath files($pluginClasspath)
        }
    }
"""

        buildFile << """
apply plugin: com.eriwen.gradle.js.JsPlugin

repositories {
    mavenCentral()
}
"""
    }

    protected BuildResult build(String... arguments) {
        createAndConfigureGradleRunner(arguments).build()
    }

    protected BuildResult buildAndFail(String... arguments) {
        createAndConfigureGradleRunner(arguments).buildAndFail()
    }

    private GradleRunner createAndConfigureGradleRunner(String... arguments) {
        GradleRunner.create().withProjectDir(projectDir).withArguments(arguments)
    }
}
