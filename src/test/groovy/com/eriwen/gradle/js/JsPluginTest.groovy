package com.eriwen.gradle.js

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.After
import org.junit.Test

import static org.junit.Assert.*

class JsPluginTest {
    private Project project
    private JsPlugin plugin

    @Before
    void setUp() {
        project = ProjectBuilder.builder().build()
        plugin = new JsPlugin()
    }

    @After
    void tearDown() {
        project = null
        plugin = null
    }

    @Test
    void shouldApplyJsTasks() {
        plugin.apply(project)

        assertEquals 1, project.getTasksByName('combineJs', false).size()
        assertEquals 1, project.getTasksByName('minifyJs', false).size()
        assertEquals 1, project.getTasksByName('gzipJs', false).size()
        assertEquals 1, project.getTasksByName('jshint', false).size()
        assertEquals 1, project.getTasksByName('jsdoc', false).size()
        assertEquals 0, project.getTasksByName('bogus', false).size()
    }
}
