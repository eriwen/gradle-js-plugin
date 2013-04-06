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
    def dest

    def setup() {
        project.apply(plugin: JsPlugin)
        task = project.tasks.jshint
        src = dir.newFolder()
        dest = dir.newFile()
        task.source = src
        task.dest = dest
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

    def "build writes to stdout and accepts options"() {
        given:
        task.ignoreExitCode = false
        task.outputToStdOut = true
        project.jshint.options = [scripturl: "true", laxcomma: "true"]

        addValidFile()

        when:
        task.run()

        then:
        notThrown ExecException
    }

    def "jshint processes many files"() {
        given:
        task.ignoreExitCode = false
        addFile("valid.js", "var a = 5;")
        addFile("valid2.js", "var b = 5;")
        addFile("valid3.js", "var c = 5;")
        addFile("valid4.js", "var d = 5;")

        when:
        task.run()

        then:
        notThrown ExecException
    }

    def "does not generate checkstyle report when disabled"() {
        given:
        task.checkstyle = false
        addFile("invalid.js", "var b = 5")

        when:
        task.run()

        then:
        def contents = new File(dest as String).text
        assert ! (contents =~ "<checkstyle")
    }

    def "generates checkstyle report when enabled"() {
        given:
        task.checkstyle = true
        addFile("invalid.js", "var b = 5")

        when:
        task.run()

        then:
        def contents = new File(dest as String).text
        assert contents =~ "<checkstyle"
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


