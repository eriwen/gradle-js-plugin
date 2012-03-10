/**
 * Copyright 2012 Eric Wendelin
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
    def source
    def dest

    @TaskAction
    def run() {
        if (!getInputs().files.files.empty) {
            logger.warn('The syntax "inputs.files ..." is deprecated! Please use `source = ["path1", "path2"]`')
            logger.warn('This will be removed in the next version of the JS plugin')
            source = getInputs().files.files.collect { it.canonicalPath }
        }

        if (!getOutputs().files.files.empty) {
            logger.warn('The syntax "outputs.files ..." is deprecated! Please use `dest = "dest/filename.js"`')
            def outputFiles = getOutputs().files.files
            if (outputFiles.size() == 1) {
                dest = (outputFiles.toArray()[0] as File).canonicalPath
            } else if (!dest) {
                throw new IllegalArgumentException('Output must be exactly 1 File object. Example: dest = "myFile"')
            }
        }

        ant.concat(destfile: dest, fixlastline: 'yes') {
            source.each {
                logger.info("Adding to fileset: ${it}")
                fileset(file: it)
            }
        }
    }
}
