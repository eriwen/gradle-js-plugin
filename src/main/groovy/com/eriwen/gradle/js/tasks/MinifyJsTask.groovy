/**
 * Copyright 2011 Eric Wendelin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.eriwen.gradle.js.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import com.google.javascript.jscomp.CommandLineRunner
import com.google.javascript.jscomp.CompilationLevel
import com.google.javascript.jscomp.Compiler
import com.google.javascript.jscomp.CompilerOptions
import com.google.javascript.jscomp.JSSourceFile
import com.google.javascript.jscomp.Result
import com.google.javascript.jscomp.WarningLevel
import org.gradle.api.file.FileCollection

class MinifyJsTask extends DefaultTask {
    CompilerOptions options = new CompilerOptions()
    String compilationLevel = 'SIMPLE_OPTIMIZATIONS'
    String warningLevel = 'DEFAULT'
	File input
	File output

	@TaskAction
	def run() {
		Compiler compiler = new Compiler()
		CompilerOptions options = new CompilerOptions()
		CompilationLevel.valueOf(compilationLevel).setOptionsForCompilationLevel(options)
		WarningLevel level = WarningLevel.valueOf(warningLevel)
		level.setOptionsForWarningLevel(options)
		List<JSSourceFile> externs = CommandLineRunner.getDefaultExterns()
        List<JSSourceFile> inputs = new ArrayList<JSSourceFile>()
        inputs.add(JSSourceFile.fromFile(input))
		Result result = compiler.compile(externs, inputs, options)
		if (result.success) {
			output.write(compiler.toSource())
		} else {
			result.errors.each {
				println "${it.sourceName}:${it.lineNumber} - ${it.description}"
			}
		}
	}
}
