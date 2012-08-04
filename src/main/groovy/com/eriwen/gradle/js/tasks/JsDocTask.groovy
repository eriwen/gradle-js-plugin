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

import com.eriwen.gradle.js.ResourceUtil
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.plugins.javascript.rhino.RhinoShellExec

class JsDocTask extends SourceTask {
    private static final String JSDOC_PATH = 'jsdoc.zip'
    private static final String TMP_DIR = "tmp${File.separator}js"
    private static final ResourceUtil RESOURCE_UTIL = new ResourceUtil()
    private final RhinoShellExec rhino = new RhinoShellExec()

    Iterable<String> modulePaths = ['node_modules', 'rhino_modules', '.']
    Boolean debug = false

    @OutputDirectory def destinationDir

    File getDestinationDir() {
        project.file(destinationDir)
    }

    @TaskAction
    def run() {
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
        args.addAll(source.files.collect { it.canonicalPath })
        args.addAll(['-d', (destinationDir as File).absolutePath])
        args.addAll(project.jsdoc.options.collect { it })

        rhino.scriptArgs = ["workingDir=${workingDir}"]
        rhino.args(args)
        rhino.execute()
    }
}
