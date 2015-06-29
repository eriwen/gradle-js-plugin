package com.eriwen.gradle.js.util

import spock.lang.Specification
import org.junit.Rule

import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.GradleConnector

abstract class FunctionalSpec extends Specification {

    @Rule TemporaryFolder dir = new TemporaryFolder()

    List<ExecutedTask> executedTasks = []

    def output = new ByteArrayOutputStream()

    def launcher(String... args) {
        def connection = GradleConnector.newConnector()
            .forProjectDirectory(dir.dir).connect()
        BuildLauncher buildLauncher = connection.newBuild();

        buildLauncher.forTasks(args)
        buildLauncher.setStandardOutput(output)
        buildLauncher
    }

    def run(String... args) {
        def result = launcher(args).run()
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

    boolean wasExecuted(String taskName) {
        println '3'*40
        println output.toString()
        println '3'*40
        return output.toString().contains(taskName + "\n")
    }

    boolean wasUpToDate(String taskName) {
        println '4'*40
        println output.toString()
        println '4'*40
        return output.toString().contains(taskName + "\n")
        return output.toString().contains(taskName + " UP-TO-DATE\n")
    }

    ExecutedTask task(String name) {
        executedTasks.find { it.task.name == name }
    }

    File unzip(File zip, File destination) {
        ProjectBuilder.builder().build().ant.unzip(src: zip, dest: destination)
        destination
    }

    String applyPlugin(Class pluginClass) {
        """
            buildscript {
                repositories {
                    mavenLocal()
                    mavenCentral()
                    flatDir(dirs: "../../../../build/lbs")
                }
                dependencies {
                    classpath "com.eriwen:gradle-js-plugin:1.12.1"
                }
            }
            apply plugin: com.eriwen.gradle.js.JsPlugin
        """
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
