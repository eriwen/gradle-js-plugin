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

import com.eriwen.gradle.js.JsMinifier
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

class MinifyJsTask extends SourceTask {
    private static final JsMinifier MINIFIER = new JsMinifier()

    @OutputFile def dest
    @Optional @OutputFile def sourceMap

    File getDest() {
        project.file(dest)
    }

    @TaskAction
    def run() {
        def buildSettings
        if (this.closure) {
            buildSettings = this.closure
        } else {
            buildSettings = project.closure
        }
        Set<File> externsFiles = buildSettings.externs ? buildSettings.externs.files : [] as Set<File>
        MINIFIER.minifyJsFile(source.files, externsFiles, dest as File, sourceMap as File,
                buildSettings.compilerOptions, buildSettings.warningLevel, buildSettings.compilationLevel)
    }
}
