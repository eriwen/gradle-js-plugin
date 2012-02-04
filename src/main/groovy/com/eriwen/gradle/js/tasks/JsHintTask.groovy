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
import com.eriwen.gradle.js.ResourceUtil

class JsHintTask extends DefaultTask {
    private static final String JSHINT_PATH = 'jshint-rhino.js'
    private static final String TMP_DIR = 'tmp/js'
    private static final ResourceUtil RESOURCE_UTIL = new ResourceUtil()

    @TaskAction
    def run() {
        def outputFiles = getOutputs().files
        if (outputFiles.files.size() == 1) {
            final File jshintJsFile = RESOURCE_UTIL.extractFileToDirectory(new File(project.buildDir, TMP_DIR), JSHINT_PATH)
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
}
