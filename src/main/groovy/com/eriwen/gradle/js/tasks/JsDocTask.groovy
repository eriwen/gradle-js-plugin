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
import com.eriwen.gradle.js.RhinoExec
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class JsDocTask extends SourceTask {
    private static final String JSDOC_PATH = 'com/github/jsdoc3/jsdoc.zip'
    private static final String TMP_DIR = "tmp${File.separator}js"
    private static final ResourceUtil RESOURCE_UTIL = new ResourceUtil()
    private final RhinoExec rhino = new RhinoExec(project)

    Iterable<String> modulePaths = ['node_modules', 'rhino', 'lib', '']
    Boolean debug = false

    @OutputDirectory def destinationDir

    File getDestinationDir() {
        project.file(destinationDir)
    }

    @TaskAction
    def run() {
        def targetDirectory = new File(project.buildDir, TMP_DIR)
        if (targetDirectory.exists() && !targetDirectory.isDirectory()) {
            throw new IllegalArgumentException("Target directory is a file!")
        } else if (!targetDirectory.exists()) {
            targetDirectory.mkdirs()
        }

        final File zipFile = new File(targetDirectory, 'jsdoc.zip')
        if (!zipFile.exists()) {
            final InputStream inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(JSDOC_PATH)
            zipFile << inputStream
            inputStream.close()
        }

        final File jsdocDir = RESOURCE_UTIL.extractZipFile(zipFile)

        final List<String> args = []
        if (debug) {
            args << '-debug'
        }
        modulePaths.each {
            args.addAll(['-modules', new File(jsdocDir, it).absolutePath])
        }
        args.add("${jsdocDir.absolutePath}${File.separator}jsdoc.js")
        args.add("--dirname=${jsdocDir.toString().replace("\\", "/")}")
        args.addAll(source.files.collect { it.canonicalPath })
        args.addAll(['-d', (destinationDir as File).absolutePath])
        args.addAll(project.jsdoc.options.collect { it })

        rhino.execute(args, [workingDir: targetDirectory.absolutePath, configuration: project.configurations.jsdoc])
    }
}
