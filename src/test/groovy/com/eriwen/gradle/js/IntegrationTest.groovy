package com.eriwen.gradle.js

import org.gradle.GradleLauncher
import org.gradle.StartParameter
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class IntegrationTest extends Specification {
    
    @Rule final TemporaryFolder dir = new TemporaryFolder()

    static class ExecutedTask {
        Task task
        TaskState state
    }
    
    List<ExecutedTask> executedTasks = []

    GradleLauncher launcher(String... args) {
        StartParameter startParameter = GradleLauncher.createStartParameter(args)
        startParameter.setProjectDir(dir.root)
        GradleLauncher launcher = GradleLauncher.newInstance(startParameter)
        executedTasks.clear()
        launcher.addListener(new TaskExecutionListener() {
            void beforeExecute(Task task) {
                executedTasks << new ExecutedTask(task: task)
            }

            void afterExecute(Task task, TaskState taskState) {
                executedTasks.last().state = taskState
                taskState.metaClass.upToDate = taskState.skipMessage == "UP-TO-DATE"
            }
        })
        launcher
    }
    
    File getBuildFile() {
        file("build.gradle")
    }
    
    File file(String path) {
        def parts = path.split("/")
        if (parts.size() > 1) {
            dir.newFolder(*parts[0..-2])
        }
        dir.newFile(path)
    }

    ExecutedTask task(String name) {
        executedTasks.find { it.task.name == name }
    }

    def setup() {
        buildFile << """
            JsPlugin = project.class.classLoader.loadClass("com.eriwen.gradle.js.JsPlugin")
        """
    }

    def "basic processing chain"() {
        given:        
        buildFile << """
            apply plugin: JsPlugin

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
        file("src/custom/js/stuff.js") << ""

        when:
        launcher("copyProcessed").run()

        then:
        task("customTest").state.executed
        task("secondTask").state.executed

        when:
        launcher("copyProcessed").run()

        then:
        task("customTest").state.upToDate
        task("secondTask").state.upToDate
    }

    def "combine, minify, gzip"() {
        given:
        buildFile << """
            apply plugin: JsPlugin

            javascript {
                source {
                    custom {
                        js {
                            srcDir "src/custom/js"
                        }
                    }
                }
            }

            task combine(type: CombineJsTask) {
                //javascript.source.custom.js.files
                inputs.files fileTree(dir: javascript.source.custom.js.srcDir, includes: ['file2.js'])
                outputs.file file("build/all.js")
            }
        """
        and:
        file("src/custom/js/file1.js") << "function fn1() { console.log('1'); }"
        and:
        file("src/custom/js/file2.js") << "function fn2() { console.log('2'); }"

        when:
        launcher('combine').run()

        then:
        file('build/all.js').exists()
    }
}
