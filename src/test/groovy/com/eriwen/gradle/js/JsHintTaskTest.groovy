package com.eriwen.gradle.js

import org.gradle.api.Project
import org.gradle.process.internal.ExecException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class JsHintTaskTest extends Specification {
    @Rule TemporaryFolder dir = new TemporaryFolder()

    Project project = ProjectBuilder.builder().build()
    def task
    def src

    def setup() {
        project.apply(plugin: JsPlugin)
        task = project.tasks.jshint
        src = dir.newFolder()
        task.source = src
        task.dest = dir.newFile()
    }

    def "build ignores result by default"() {
        given:
        addValidFile()
        addInvalidFile()

        when:
        task.run()

        then:
        notThrown ExecException
    }

    def "build passes with only valid files"() {
        given:
        task.ignoreExitCode = false
        addValidFile()

        when:
        task.run()

        then:
        notThrown ExecException
    }

    def "build fails with invalid files"() {
        given:
        task.ignoreExitCode = false
        addValidFile()
        addInvalidFile()

        when:
        task.run()

        then:
        ExecException e = thrown()
    }


    def addValidFile() {
        addFile("valid.js", "var a = 5;")
    }

    def addInvalidFile() {
        // no semicolon, jshint should fail
        addFile("invalid.js", "var a = 5")
    }

    def addFile(name,contents) {
        def file = new File(src as String, name)
        file << contents
    }
}


