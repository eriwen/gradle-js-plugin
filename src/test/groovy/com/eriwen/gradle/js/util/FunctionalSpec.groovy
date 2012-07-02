package com.eriwen.gradle.js.util

import spock.lang.Specification
import org.junit.Rule
import org.gradle.GradleLauncher
import org.gradle.StartParameter
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.Task
import org.gradle.api.tasks.TaskState
import org.gradle.BuildResult

abstract class FunctionalSpec extends Specification {

    @Rule TemporaryFolder dir = new TemporaryFolder()

    List<ExecutedTask> executedTasks = []

    GradleLauncher launcher(String... args) {
        StartParameter startParameter = GradleLauncher.createStartParameter(args)
        startParameter.setProjectDir(dir.dir)

        if (settingsFile.exists()) {
            startParameter.settingsFile = settingsFile
        } else {
            startParameter.useEmptySettings()
        }

        GradleLauncher launcher = GradleLauncher.newInstance(startParameter)
        executedTasks.clear()
        launcher.addListener(new TaskExecutionListener() {
            void beforeExecute(Task task) {}

            void afterExecute(Task task, TaskState taskState) {
                getExecutedTasks() << new ExecutedTask(task, new IncrementalTaskState(taskState))
            }
        })
        launcher
    }

    BuildResult run(String... args) {
        def result = launcher(args).run()
        result.rethrowFailure()
        result
    }

    File file(String path) {
        dir.newFile(path)
    }

    File createFile(String path) {
        File file = file(path)
        if (!file.exists()) {
            assert file.parentFile.mkdirs() || file.parentFile.exists()
            file.createNewFile()
        }
        file
    }

    File getBuildFile() {
        file("build.gradle")
    }

    File getSettingsFile() {
        file("settings.gradle")
    }

    ExecutedTask task(String name) {
        executedTasks.find { it.task.name == name }
    }

    boolean wasExecuted(String taskPath) {
        executedTasks.any { it.task.path == taskPath }
    }

    boolean wasUpToDate(String taskPath) {
        executedTasks.find { it.task.path == taskPath }?.state?.upToDate
    }

    File unzip(File zip, File destination) {
        ProjectBuilder.builder().build().ant.unzip(src: zip, dest: destination)
        destination
    }

    String applyPlugin(Class pluginClass) {
        "apply plugin: project.class.classLoader.loadClass('$pluginClass.name')"
    }

    String copyResources(String srcDir, String destination) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(srcDir);
        if (resource == null) {
            throw new RuntimeException("Could not find classpath resource: $srcDir")
        }

        File destinationFile = file(destination)
        File resourceFile = new File(resource.toURI())
        if (resourceFile.file) {
            FileUtils.copyFile(resourceFile, destinationFile)
        } else {
            FileUtils.copyDirectory(resourceFile, destinationFile)
        }
    }

}
