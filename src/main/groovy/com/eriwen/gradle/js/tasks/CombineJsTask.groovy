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

class CombineJsTask extends DefaultTask {
    @TaskAction
    def run() {
        def outputFiles = getOutputs().files
        if (outputFiles.files.size() == 1) {
            ant.concat(destfile: outputFiles.asPath, fixlastline: 'yes') {
                getInputs().files.each {
                    if (it.exists()) {
                        fileset(file: it.canonicalPath)
                    } else {
                        // FIXME: does not print anything, explore Gradle logging
                        logger.warn("Tried to process file that does not exist: ${it.canonicalPath}")
                    }
                }
            }
        } else {
            throw new IllegalArgumentException('Output must be exactly 1 File object. Example: outputs.file = file("myFile")')
        }
    }
}
