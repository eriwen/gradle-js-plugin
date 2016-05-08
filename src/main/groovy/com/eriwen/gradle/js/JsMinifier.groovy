package com.eriwen.gradle.js

import com.google.javascript.jscomp.CommandLineRunner
import com.google.javascript.jscomp.CompilationLevel
import com.google.javascript.jscomp.Compiler
import com.google.javascript.jscomp.CompilerOptions
import com.google.javascript.jscomp.SourceFile
import com.google.javascript.jscomp.Result
import com.google.javascript.jscomp.WarningLevel
import org.gradle.api.GradleException

/**
 * Util to minify JS files with Google Closure Compiler.
 *
 * @author Eric Wendelin
 * @date 10/30/11
 */
class JsMinifier {

    void minifyJsFile(final Set<File> inputFiles, final Set<File> externsFiles, final File outputFile, final File sourceMap, CompilerOptions options,
            final String warningLevel, final String compilationLevel) {
        options = options ?: new CompilerOptions()
        options.setSourceMapOutputPath(sourceMap?.path)
        Compiler compiler = new Compiler()
        CompilationLevel.valueOf(compilationLevel).setOptionsForCompilationLevel(options)
        WarningLevel level = WarningLevel.valueOf(warningLevel)
        level.setOptionsForWarningLevel(options)
        List<SourceFile> externs = CommandLineRunner.getBuiltinExterns(new CompilerOptions());
        if (externsFiles.size()) {
            externs.addAll(externsFiles.collect() { SourceFile.fromFile(it) })
        }
        List<SourceFile> inputs = new ArrayList<SourceFile>()
        inputFiles.each { inputFile -> 
          inputs.add(SourceFile.fromFile(inputFile))
        }
        Result result = compiler.compile(externs, inputs, options)
        if (result.success) {
            outputFile.write(compiler.toSource())
            if(sourceMap) {
              def sourceMapContent = new StringBuffer()
              result.sourceMap.appendTo(sourceMapContent, outputFile.name)
              sourceMap.write(sourceMapContent.toString())
            }
        } else {
        	String error = ""
            result.errors.each {
                error += "${it.sourceName}:${it.lineNumber} - ${it.description}\n"
            }
            throw new GradleException(error)
        }
    }
}
