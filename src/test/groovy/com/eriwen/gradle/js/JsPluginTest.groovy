package com.eriwen.gradle.js

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class JsPluginTest extends Specification {

    Project project = ProjectBuilder.builder().build()

    def setup() {
        project.apply(plugin: JsPlugin)
    }

    def "extensions are installed"() {
        expect:
        project.extensions.getByName("closure") instanceof ClosureCompilerExtension
        project.extensions.getByName("javascript") instanceof JavaScriptExtension
        project.extensions.getByName("jsdoc") instanceof JsDocExtension
        project.extensions.getByName("props") instanceof Props2JsExtension
        project.extensions.getByName("requirejs") instanceof RequireJsExtension
    }
}
