package com.eriwen.gradle.js

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.After
import org.junit.Test

import static org.junit.Assert.*
import org.gradle.api.tasks.TaskExecutionException

class JsPluginTest {
    private static final String TEST_SOURCE_PATH = new File('.', 'src/test/resources').absolutePath

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
        assertEquals 1, project.getTasksByName('js', false).size()
        assertEquals 0, project.getTasksByName('bogus', false).size()
    }

    @Test(expected=TaskExecutionException.class)
    void shouldFailOnDownstreamTaskFailure() {
        plugin.apply(project)

        JsPluginConvention pluginConvention = plugin.jsPluginConvention
        pluginConvention.with {
            // combine task will fail with no input or output
            input = null
            output = null
        }
        project.getTasksByName('combineJs', false).iterator().next().execute()

        fail 'Should have gotten a TaskExecutionException'
    }
}