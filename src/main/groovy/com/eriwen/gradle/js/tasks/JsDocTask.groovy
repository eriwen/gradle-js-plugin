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
    private static final String JSDOC_NAME = 'jsdoc-releases-3.3'
    private static final String JSDOC_PATH = "${JSDOC_NAME}.zip"
    private static final String TMP_DIR = "tmp${File.separator}js"
    private static final ResourceUtil RESOURCE_UTIL = new ResourceUtil()
    private final RhinoExec rhino = new RhinoExec(project)

    Iterable<String> modulePaths = ['lib', 'node_modules', 'rhino', '.']
    Boolean debug = false

    @OutputDirectory def destinationDir

    File getDestinationDir() {
        project.file(destinationDir)
    }

    @TaskAction
    def run() {
        final File zipFile = extractJsDoc(new File(project.buildDir, TMP_DIR), JSDOC_PATH)
        final File jsdocDir = RESOURCE_UTIL.extractZipFile(zipFile)
        final String workingDir = "${jsdocDir.absolutePath}${File.separator}${JSDOC_NAME}"

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

        rhino.execute(args, [workingDir: workingDir, classpath: project.files("${workingDir}${File.separator}rhino${File.separator}js.jar")])
    }

    // Cannot for the life of me figure out why Thread.currentThread().contextClassLoader no longer works for loading jsdoc.zip
    File extractJsDoc(final File targetDirectory, final String resourcePath) {
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs()
        }

        final File file = new File(targetDirectory, resourcePath)
        if (!file.exists()) {
            final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)
            file << inputStream
            inputStream.close()
        }
        return file
    }
}
