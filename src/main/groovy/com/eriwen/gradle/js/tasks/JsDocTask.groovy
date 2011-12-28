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

import org.gradle.api.tasks.TaskAction
import org.gradle.api.DefaultTask

class JsDocTask extends DefaultTask {
    private static final String JSHINT_PATH = 'jshint-rhino.js'
    private static final String TMP_DIR = 'tmp/js'

    @TaskAction
    def run() {
        def outputFiles = getOutputs().files
        if (outputFiles.files.size() == 1) {
            final File jshintJsFile = loadJsHintJs()
            final String outputPath = (outputFiles.files.toArray()[0] as File).canonicalPath
            ant.java(jar: project.configurations.rhino.asPath, fork: true, output: outputPath) {
                arg(value: jshintJsFile.canonicalPath)
                getInputs().files.files.each {
                    arg(value: it.canonicalPath)
                }
            }
        } else {
            throw new IllegalArgumentException('Output must be exactly 1 File object. Example: outputs.file = file("myFile")')
        }
    }

    File loadJsHintJs() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(JSHINT_PATH)
        File tempDir = new File(project.buildDir, TMP_DIR)
        tempDir.mkdirs()
        File jshintJsFile = new File(tempDir, JSHINT_PATH)
        if (!jshintJsFile.exists()) {
            jshintJsFile << inputStream
        }
        inputStream.close()
        return jshintJsFile
    }
}
