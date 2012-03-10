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

class JsTask extends DefaultTask {
    CompilerOptions compilerOptions = new CompilerOptions()
    String compilationLevel = 'SIMPLE_OPTIMIZATIONS'
    String warningLevel = 'DEFAULT'

    private static final String COMBINED_JS_FILE = 'combined.ja'
    private static final String TMP_DIR = 'tmp/js'
    private static final JsMinifier MINIFIER = new JsMinifier()

    @TaskAction
    def run() {
        final File tempDir = makeTempDir()
        final def outputFiles = getOutputs().files.files.toArray()
        final String outputPath = (outputFiles[0] as File).canonicalPath
        final String tempPath = "${tempDir.canonicalPath}/${COMBINED_JS_FILE}"

        def deprecationMessage = """
        The js is deprecated and will be removed in the next version of the Gradle JS plugin.

        It is recommended to use Gradle 1.0m9+ ability to reference other tasks' outputs like so:

        combineJs {
            source = ["foo.js", "bar.js"]
            dest = "all.js"
        }

        task minify(type: MinifyJsTask) {
            source = combineJs.outputs
            dest = "all-min.js"
        }

        """

        logger.warn(deprecationMessage)

        if (outputFiles.size() != 1) {
            throw new IllegalArgumentException('Output must be exactly 1 File object. Example: outputs.file = file("myFile")')
        }

        ant.concat(destfile: tempPath, fixlastline: 'yes') {
            getInputs().files.each {
                getInputs().files.each {
                    logger.info("Adding to fileset: ${it.canonicalPath}")
                    fileset(file: it.canonicalPath)
                }
            }
        }

        MINIFIER.minifyJsFile(new File(tempPath), outputFiles[0] as File, compilerOptions, warningLevel, compilationLevel)

        ant.gzip(src: outputPath, destfile: "${outputPath}.gz")
        ant.move(file: "${outputPath}.gz", tofile: outputPath)
    }

    File makeTempDir() {
        File tempDir = new File(project.buildDir, TMP_DIR)
        tempDir.mkdirs()
        return tempDir
    }
}
