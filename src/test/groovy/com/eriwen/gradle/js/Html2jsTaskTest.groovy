package com.eriwen.gradle.js

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class Html2jsTaskTest extends Specification {
    @Rule TemporaryFolder dir = new TemporaryFolder()

    Project project = ProjectBuilder.builder().build()
    def task
    def src
    def dest

    def setup() {
        project.apply(plugin: JsPlugin)
        project.repositories.mavenCentral()
        task = project.tasks.html2js
        src = dir.newFolder()
        dest = dir.newFile()
        task.source = src
        task.dest = dest
        addFile('src/foo.html', '<div id="foo">foo\n</div>')
        addFile('src/bar.html', "<div id='bar'>bar\n</div>")
    }

    def "no module does not create a root angular bundle"() {
        when:
        task.run()

        then:
        String result = dest.text
        assertExpectedResult(result, null, '')

        and: 'first angular module is a template, not a bundle'
        def firstTemplatePlace = Math.min(result.indexOf('angular.module("bar.html"'), result.indexOf('angular.module("foo.html"'))
        result.indexOf("angular.module") == firstTemplatePlace
    }

    def "specified module name is used"() {
        given:
        task.moduleName = 'blah'

        when:
        task.run()

        then:
        assertExpectedResult(dest.text, 'blah', '')
    }

    def "template module has relative path when base is specified"() {
        given:
        task.base = src
        task.moduleName = 'templates'

        when:
        task.run()

        then:
        assertExpectedResult(dest.text, 'templates', 'src/')
    }

    def "other options are passed through"() {
        given:
        task.quoteChar = "\'";
        task.indentString = "\t";
        task.useStrict = true;
        task.fileHeader = "alert('hi there!');"

        when:
        task.run()

        then:
        String result = dest.text
        assert result.indexOf("alert('hi there!');") == 0
        assert result.indexOf("'use strict';") != -1
        assert result.indexOf("'<div id=\"foo\">foo\\n' +" + System.lineSeparator() + "\t\t'</div>'") > -1
        assert result.indexOf("'<div id=\\'bar\\'>bar\\n' +" + System.lineSeparator() + "\t\t'</div>'") > -1
    }

    private static boolean assertExpectedResult(String result, String expectedModule, String expectedPathPrefix) {
        if(expectedModule) {
            def m = result =~ /angular.module\('${expectedModule}'.*\['(.*)', '(.*)'.*/
            def modules = [m[0][1], m[0][2]]
            assert modules.sort() == ["${expectedPathPrefix}bar.html", "${expectedPathPrefix}foo.html"]
        }
        assert result.indexOf('"<div id=\\"foo\\">foo\\n" +' + System.lineSeparator() + '    "</div>"') > -1
        assert result.indexOf('"<div id=\'bar\'>bar\\n" +' + System.lineSeparator() + '    "</div>"') > -1
        true
    }

    def addFile(name,contents) {
        def file = new File(src as String, name)
        file.parentFile.mkdirs()
        file << contents
    }
}


