package com.eriwen.gradle.js

import org.gradle.api.Project
import org.gradle.process.internal.ExecException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class RequireJsTaskTest extends Specification {
    @Rule TemporaryFolder dir = new TemporaryFolder()

    Project project = ProjectBuilder.builder().build()
    def task
    def src

    def setup() {
        project.apply(plugin: JsPlugin)
        project.repositories.mavenCentral()
        task = project.tasks.requireJs
        src = dir.newFolder()
        task.source = src
        task.dest = dir.newFile()
    }

    def "runWithDefaults"() {
        given:
        project.requirejs.options = [baseUrl: ".", "paths.jquery": "jam/jquery/dist/jquery", name: "main", out: "main-built.js"]
        task.ignoreExitCode = false
        addMainFile()
        addJamDir()

        when:
        task.run()

        then:
        notThrown ExecException
    }

    def "runWithInvalidMainJs"() {
        given:
        project.requirejs.options = [baseUrl: ".", "paths.jquery": "jam/jquery/dist/jquery", name: "main", out: "main-built.js"]
        task.ignoreExitCode = false
        addInvalidMainFile()
        addJamDir()

        when:
        task.run()

        then:
        ExecException e = thrown()
    }

    def "runWithInvalidRequireImplementation"() {
        given:
        project.requirejs.options = [baseUrl: ".", "paths.jquery": "jam/jquery/dist/jquery", name: "main", out: "main-built.js"]
        project.requirejs.impl = new File("bad.r.js");
        task.ignoreExitCode = false
        addMainFile()
        addJamDir()

        when:
        task.run()

        then:
        ExecException e = thrown()
    }

    def "runWithAlternateRequireImplementation"() {
        given:
        project.requirejs.options = [baseUrl: ".", "paths.jquery": "jam/jquery/dist/jquery", name: "main", out: "main-built.js"]
        project.requirejs.impl = new File("src/test/resources/requirejs/r.2.1.4.js");
        task.ignoreExitCode = false
        addMainFile()
        addJamDir()

        when:
        task.run()

        then:
        notThrown ExecException
    }

    def "runWithBuildProfile"() {
        given:
        project.requirejs.buildprofile = new File("${project.projectDir.absolutePath}${File.separator}build.js")
        addBuildFile()
        addMainFile()
        addJamDir()

        when:
        task.run()

        then:
        notThrown ExecException
    }

    def addBuildFile() {
        addFile("build.js", new File("src/test/resources/requirejs/build.js").text)
    }

    def addMainFile() {
        addFile("main.js", new File("src/test/resources/requirejs/main.js").text)
    }

    def addInvalidMainFile() {
        // call to invalidrequire[]
        addFile("main.js", new File("src/test/resources/requirejs/invalidmain.js").text)
    }

    def addJamDir() {
        new AntBuilder().copy(todir: new File("${project.projectDir.absolutePath}${File.separator}jam").canonicalPath) {
            fileset(dir : new File("src/test/resources/requirejs/jam").canonicalPath)
        }
    }

    def addFile(name, contents) {
        def file = new File(project.projectDir as String, name)
        file << contents
    }
}


