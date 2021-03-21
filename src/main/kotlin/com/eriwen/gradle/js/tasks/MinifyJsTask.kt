package com.eriwen.gradle.js.tasks

import com.google.javascript.jscomp.*
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import java.io.File
import javax.inject.Inject

open class MinifyJsTask @Inject constructor(private val workerExecutor: WorkerExecutor) : SourceTask() {
    /**
     * Returns the output directory for the generated JS file.
     *
     * @return The output directory.
     */
    @get:OutputDirectory
    val dest: DirectoryProperty = project.objects.directoryProperty()

    @TaskAction
    fun run() {

    }
}

class ClosureCompilerRunnable @Inject constructor(private val outputDir: File) : Runnable {
    override fun run() {

    }

    fun minifyJsFile(
        inputFiles: Set<File>,
        externsFiles: Set<File>,
        outputFile: File,
        sourceMapFile: File?,
        compilerOptions: CompilerOptions?,
        warningLevel: String,
        compilationLevel: String
    ) {
        val compiler = Compiler()
        // TODO: use passed compiler options if given
        val compilerOptions = CompilerOptions()
        CompilationLevel.valueOf(compilationLevel).setOptionsForCompilationLevel(compilerOptions)
        compilerOptions.setSourceMapOutputPath(sourceMapFile?.path)
        val warningLevel = WarningLevel.valueOf(warningLevel)
        warningLevel.setOptionsForWarningLevel(compilerOptions)
        val externs = CommandLineRunner.getBuiltinExterns(compilerOptions.environment)
        if (externsFiles.isNotEmpty()) {
            externs.addAll(externsFiles.map { SourceFile.fromFile(it.name) })
        }

        val inputs = inputFiles.map { SourceFile.fromFile(it.name) }
        val result = compiler.compile(externs, inputs, compilerOptions)
        if (result.success) {
            outputFile.writeText(compiler.toSource())
            if (sourceMapFile != null) {
                val sourceMapContent = StringBuffer()
                result.sourceMap.appendTo(sourceMapContent, outputFile.name)
                sourceMapFile.writeText(sourceMapContent.toString())
            }
        } else {
            throw GradleException(result.errors.joinToString { "${it.sourceName}:${it.lineNumber} - ${it.description}\n" })
        }
    }
}
