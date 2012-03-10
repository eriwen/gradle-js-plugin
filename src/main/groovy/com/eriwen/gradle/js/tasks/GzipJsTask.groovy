/**
 * Copyright 2011-2012 Eric Wendelin
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

class GzipJsTask extends DefaultTask {
    File source
    File dest

    @TaskAction
    def run() {
        if (!source) {
            logger.warn('The syntax "inputs.files ..." is deprecated! Please use `source = file("path1")`')
            logger.warn('This will be removed in the next version of the JS plugin')
            source = getInputs().files.files.toArray()[0] as File
        }

        if (!dest) {
            logger.warn('The syntax "outputs.files ..." is deprecated! Please use `dest = file("dest/file.js")`')
            dest = getOutputs().files.files.toArray()[0] as File
        }

        ant.gzip(src: source.canonicalPath, destfile: "${source.canonicalPath}.gz")
        ant.move(file: "${source.canonicalPath}.gz", tofile: dest.canonicalPath)
    }
}
