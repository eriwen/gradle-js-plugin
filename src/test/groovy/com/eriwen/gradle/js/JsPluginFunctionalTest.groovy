package com.eriwen.gradle.js

import org.gradle.testkit.runner.BuildResult
import com.eriwen.gradle.js.util.FunctionalSpec
import org.gradle.testkit.runner.TaskOutcome

class JsPluginFunctionalTest extends FunctionalSpec {
    def "basic processing chain"() {
        given:
        buildFile << """
            class TestTask extends SourceTask {
                @OutputDirectory
                File destination

                @TaskAction
                void doit() {
                    project.copy {
                        from getSource()
                        into destination
                    }
                }
            }

            javascript {
                source {
                    custom {
                        js {
                            srcDir "src/custom/js"
                        }
                        processing {
                            task(TestTask) {
                                destination project.file("build/\$name")
                            }
                            task("secondTask", TestTask) {
                                destination project.file("build/\$name")
                            }
                        }
                    }
                }
            }

            task copyProcessed(type: Copy) {
                from javascript.source.custom.processed
                into "build/out"
            }
        """
        and:
        tempProjectDir.newFolder('src', 'custom', 'js')
        tempProjectDir.newFile('src/custom/js/stuff.js')

        when:
        BuildResult result = build('copyProcessed')

        then:
        result.task(":customTest").outcome == TaskOutcome.SUCCESS
        result.task(":secondTask").outcome == TaskOutcome.SUCCESS

        when:
        BuildResult secondResult = build('copyProcessed')

        then:
        secondResult.task(":customTest").outcome == TaskOutcome.UP_TO_DATE
        secondResult.task(":secondTask").outcome == TaskOutcome.UP_TO_DATE
    }

    def "tasks operation"() {
        given:
        buildFile << """
            javascript.source {
                custom {
                    js {
                        srcDir "src/custom/js"
                    }
                }
            }

            combineJs {
                source = javascript.source.custom.js.files
                dest = file("\$buildDir/all.js")
            }

            minifyJs {
                source = combineJs
                dest = "\$buildDir/all-min.js" //Test flexible outputs
            }
        """
        and:
        tempProjectDir.newFolder('src', 'custom', 'js')
        tempProjectDir.newFile("src/custom/js/file1.js") << "function fn1() { console.log('1'); }"
        tempProjectDir.newFile("src/custom/js/file2.js") << "function fn2() { console.log('2'); }"

        when:
        BuildResult result = build('minifyJs')

        then:
        File minifiedJs = new File(projectDir, 'build/all-min.js')
        minifiedJs.exists()
        minifiedJs.text.indexOf('function fn1(){console.log("1")}') > -1
        minifiedJs.text.indexOf('function fn2(){console.log("2")}') > -1

        and:
        result.task(':combineJs').outcome == TaskOutcome.SUCCESS
        result.task(':minifyJs').outcome == TaskOutcome.SUCCESS

        when:
        BuildResult secondResult = build('minifyJs')

        then:
        secondResult.task(':combineJs').outcome == TaskOutcome.UP_TO_DATE
        secondResult.task(':minifyJs').outcome == TaskOutcome.UP_TO_DATE

        when:
        tempProjectDir.newFile("src/custom/js/file3.js") << "function fn3() { console.log('3'); }"

        and:
        BuildResult thirdResult = build('minifyJs')

        then:
        thirdResult.task(':minifyJs').outcome == TaskOutcome.SUCCESS
    }

    def "html2js combine with combineJs"() {
        given:
        buildFile << """
            javascript.source {
                templates {
                    js {
                        srcDir "src/app"
                        include "*.html"
                    }
                }
                app {
                    js {
                        srcDir "src/app"
                        include "*.js"
                    }
                }
            }

            html2js {
                source = javascript.source.templates.js.files
                moduleName = 'templates'
                base = "src/app"
                dest = file("\$buildDir/templates.js")
            }

            combineJs {
                source = javascript.source.app.js.files + html2js
                dest = file("\$buildDir/all.js")
            }
        """

        and:
        tempProjectDir.newFolder('src', 'app')
        tempProjectDir.newFile("src/app/app.js") << "function fn1() { console.log('1'); }"
        tempProjectDir.newFile('src/app/app.html') << '<div>foo</div>'

        when:
        build(':combineJs')

        then:
        File combinedJs = new File(projectDir, 'build/all.js')
        combinedJs.exists()
        combinedJs.text.indexOf("function fn1() { console.log('1'); }") > -1
        combinedJs.text.indexOf('<div>foo</div>') > -1
    }

    def "per-task Closure Compiler settings"() {
        given:
        buildFile << """
            javascript.source {
                custom {
                    js {
                        srcDir "src/custom/js"
                    }
                }
            }

            task compileDev (type: com.eriwen.gradle.js.tasks.MinifyJsTask) {
                source = javascript.source.custom.js.files
                dest = "\$buildDir/out-dev.js" //Test flexible outputs
                closure {
                    warningLevel = 'VERBOSE'
                    compilerOptions.defineReplacements = ['DEBUG': true]
                }
            }

            task compileProd (type: com.eriwen.gradle.js.tasks.MinifyJsTask) {
                source = javascript.source.custom.js.files
                dest = "\$buildDir/out-prod.js" //Test flexible outputs
                closure {
                    warningLevel = 'VERBOSE'
                    compilerOptions.defineReplacements = ['DEBUG': false]
                }
            }
        """
        and:
        tempProjectDir.newFolder('src', 'custom', 'js')
        tempProjectDir.newFile("src/custom/js/file1.js") << "/** @define {boolean} */var DEBUG = true; DEBUG && console.log('hello, world!');"

        when:
        BuildResult result = build('compileProd')

        then:
        File minifiedJs = new File(projectDir, 'build/out-prod.js')
        minifiedJs.exists()
        minifiedJs.text.indexOf("console.log") == -1

        and:
        result.task(':compileProd').outcome == TaskOutcome.SUCCESS

        when:
        BuildResult secondResult = build('compileDev')

        then:
        secondResult.task(":compileDev").outcome == TaskOutcome.SUCCESS
        File minifiedJsDev = new File(projectDir, 'build/out-dev.js')
        minifiedJsDev.exists()
        minifiedJsDev.text.length() > 0
        minifiedJsDev.text.indexOf("console.log") >= 0
    }

}
