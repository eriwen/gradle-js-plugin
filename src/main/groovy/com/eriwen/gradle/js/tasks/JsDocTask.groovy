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

import org.gradle.api.tasks.TaskAction
import org.gradle.api.DefaultTask
import com.eriwen.gradle.js.ResourceUtil

import com.eriwen.gradle.js.RhinoExec

class JsDocTask extends DefaultTask {
    private static final String JSDOC_PATH = 'jsdoc.zip'
    private static final String TMP_DIR = "tmp${File.separator}js"
    private static final ResourceUtil RESOURCE_UTIL = new ResourceUtil()
    private final RhinoExec rhino = new RhinoExec(project)

    Iterable<String> modulePaths = ['node_modules', 'rhino_modules', '.']
    Iterable<String> options = []
    Boolean debug = false
    def source
    File destinationDir

    @TaskAction
    def run() {
        if (!source) {
            logger.warn('The syntax "inputs.files ..." is deprecated! Please use `source = ["path1", "path2"]`')
            logger.warn('This will be removed in the next version of the JS plugin')
            source = getInputs().files.files.collect { it.canonicalPath }
        }

        if (!destinationDir) {
            logger.warn('The syntax "outputs.file file(..)" is deprecated! Please use `destinationDir = file(buildDir)`')
            destinationDir = getOutputs().files.files.toArray()[0] as File
        }

        final File zipFile = RESOURCE_UTIL.extractFileToDirectory(new File(project.buildDir, TMP_DIR), JSDOC_PATH)
        final File jsdocDir = RESOURCE_UTIL.extractZipFile(zipFile)
        final String workingDir = "${jsdocDir.absolutePath}${File.separator}jsdoc"

        final List<String> args = []
        if (debug) {
            args << '-debug'
        }
        modulePaths.each {
            args.addAll(['-modules', it])
        }
        args.add("${workingDir}${File.separator}jsdoc.js")
        args.addAll(source)
        args.addAll(['-d', destinationDir.absolutePath])
        args.addAll(options.collect { it })

        rhino.execute(args, workingDir)
    }
}
