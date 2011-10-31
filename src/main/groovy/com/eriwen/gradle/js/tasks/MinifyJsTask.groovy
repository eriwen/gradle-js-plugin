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

import com.google.javascript.jscomp.CompilerOptions
import com.eriwen.gradle.js.JsMinifier

class MinifyJsTask extends DefaultTask {
    private static final JsMinifier MINIFIER = new JsMinifier()

    CompilerOptions options = new CompilerOptions()
    String compilationLevel = 'SIMPLE_OPTIMIZATIONS'
    String warningLevel = 'DEFAULT'

	@TaskAction
	def run() {
        def inputFiles = getInputs().files.files.toArray()
        def outputFiles = getOutputs().files.files.toArray()
        if (outputFiles.size() == inputFiles.size()) {
            for (int i = 0; i < inputFiles.size(); i++) {
                MINIFIER.minifyJsFile(inputFiles[i] as File, outputFiles[i] as File, warningLevel, compilationLevel)
            }
        } else {
            throw new IllegalArgumentException("Could not map input files to output files. Found ${inputFiles.size()} inputs and ${outputFiles.size()} outputs")
        }
	}
}
