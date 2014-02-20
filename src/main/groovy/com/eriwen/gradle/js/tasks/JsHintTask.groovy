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

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import com.eriwen.gradle.js.ResourceUtil
import com.eriwen.gradle.js.RhinoExec
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.OutputFile

class JsHintTask extends SourceTask {
    private static final String JSHINT_PATH = 'jshint-rhino-2.4.3.js'
    private static final String TMP_DIR = "tmp${File.separator}js"
    private static final ResourceUtil RESOURCE_UTIL = new ResourceUtil()
    private final RhinoExec rhino = new RhinoExec(project)

    @OutputFile def dest = new File(project.buildDir, "jshint.log")
    @Input def ignoreExitCode = true
    @Input def outputToStdOut = false
    @Input def reporter = ''

    File getDest() {
        project.file(dest)
    }

    @TaskAction
    def run() {
        final File jshintJsFile = RESOURCE_UTIL.extractFileToDirectory(
                new File(project.buildDir, TMP_DIR), JSHINT_PATH)
        final List<String> args = [jshintJsFile.canonicalPath]
        args.addAll(source.files.collect { it.canonicalPath })

        // Allow variable reporter
        if (reporter) {
          logger.debug("reporter=${reporter}")
          args.add("reporter=${reporter}")
        }

        def optionsArg = makeOptionsArg(project.jshint.options)
        if (optionsArg != "") {
          args.add(optionsArg)
        }
        def predefArg = makeOptionsArg(project.jshint.predef)
        if (predefArg != "") {
          args.add(predefArg)
        }

        if (outputToStdOut) {
            rhino.execute(args, [ignoreExitCode: ignoreExitCode])
        } else {
            rhino.execute(args, [ignoreExitCode: ignoreExitCode, out: new FileOutputStream(dest as File)])
        }
    }

    private def makeOptionsArg(LinkedHashMap<String, Object> options) {
        def optionsArg = ""
        if (options != null && options.size() > 0) {
            options.each() { key, value ->
                logger.debug("${key} == ${value}")
                optionsArg = (optionsArg == "") ? "${key}=${value}" : "${optionsArg},${key}=${value}"
            }
        }
        return optionsArg
    }
}
