package com.eriwen.gradle.js

import com.google.javascript.jscomp.CommandLineRunner
import com.google.javascript.jscomp.CompilationLevel
import com.google.javascript.jscomp.Compiler
import com.google.javascript.jscomp.CompilerOptions
import com.google.javascript.jscomp.JSSourceFile
import com.google.javascript.jscomp.Result
import com.google.javascript.jscomp.WarningLevel

/**
 * Util to minify JS files with Google Closure Compiler.
 *
 * @author Eric Wendelin
 * @date 10/30/11
 */
class JsMinifier {


    void minifyJsFile(final File inputFile, final File outputFile, final CompilerOptions options,
            final String warningLevel, final String compilationLevel) {
		Compiler compiler = new Compiler()
		CompilationLevel.valueOf(compilationLevel).setOptionsForCompilationLevel(options)
		WarningLevel level = WarningLevel.valueOf(warningLevel)
		level.setOptionsForWarningLevel(options)
		List<JSSourceFile> externs = CommandLineRunner.getDefaultExterns()
        List<JSSourceFile> inputs = new ArrayList<JSSourceFile>()
        inputs.add(JSSourceFile.fromFile(inputFile))
		Result result = compiler.compile(externs, inputs, options)
		if (result.success) {
			outputFile.write(compiler.toSource())
		} else {
			result.errors.each {
				println "${it.sourceName}:${it.lineNumber} - ${it.description}"
			}
		}
    }
}
