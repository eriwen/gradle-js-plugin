/**
 * Copyright 2012 Joe Fitzgerald
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

class RequireJsTask extends SourceTask {
    private static final String REQUIREJS_PATH = 'r.js'
    private static final String TMP_DIR = "tmp${File.separator}js"
    private static final ResourceUtil RESOURCE_UTIL = new ResourceUtil()
    private final RhinoExec rhino = new RhinoExec(project)

    @OutputFile def dest
    @Input def ignoreExitCode = false

    File getDest() {
        project.file(dest)
    }

    @TaskAction
    def run() {
        LinkedHashMap<String, Object> options = [] // [optimize: "none", logLevel: 2, skipModuleInsertion: false, out: dest]
        options.putAll(project.requirejs.options)

        final File requireJsFile
        if (project.requirejs.impl != null && project.requirejs.impl.class == File) {
            requireJsFile = new File("${project.requirejs.impl.canonicalPath}")
        } else {
            requireJsFile = RESOURCE_UTIL.extractFileToDirectory(new File(project.buildDir, TMP_DIR), REQUIREJS_PATH)
        }

        final List<String> args = [requireJsFile.canonicalPath]
        args.add("-o")
        if (project.requirejs.buildprofile != null && project.requirejs.buildprofile.class == File && project.requirejs.buildprofile.exists()) {
            args.add("${project.requirejs.buildprofile.canonicalPath}")
        }

        def outAdded = false
        if (!options.containsKey("out")) {
            args.add("out=${getDest().canonicalPath}")
            outAdded = true
        }
        options.each() { key, value ->
            logger.debug("${key} == ${value}")
            if (key.equalsIgnoreCase("out") & !outAdded) {
                args.add("out=${getDest().canonicalPath}")
                outAdded = true
            } else {
                args.add("${key}=${value}")
            }
        }

        rhino.execute(args, [ignoreExitCode: ignoreExitCode, workingDir: project.projectDir.canonicalPath])
    }
}
