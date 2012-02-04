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
import java.util.zip.ZipFile

class JsDocTask extends DefaultTask {
    private static final String JSDOC_PATH = 'jsdoc.zip'
    private static final String TMP_DIR = "tmp${File.separator}js"
    private static final ResourceUtil RESOURCE_UTIL = new ResourceUtil()
    private static final String RHINO_MAIN_CLASS = 'org.mozilla.javascript.tools.shell.Main'
    Iterable<String> modulePaths = ['node_modules', 'rhino_modules', '.']
    Iterable<String> options = []

    @TaskAction
    def run() {
        def outputFiles = getOutputs().files
        if (outputFiles.files.size() == 1) {
            RESOURCE_UTIL.extractZipFile(RESOURCE_UTIL.extractFileToDirectory(new File(project.buildDir, TMP_DIR), JSDOC_PATH))
            final String outputPath = (outputFiles.files.toArray()[0] as File).canonicalPath
            //java -cp ../../idealib/js.jar org.mozilla.javascript.tools.shell.Main -modules ./node_modules -modules ./rhino_modules -modules . jsdoc.js ../../src/test/resources/file2.js

            ant.java(classpath: project.configurations.rhino.asPath, classname: RHINO_MAIN_CLASS, fork: true, output: outputPath) {
                modulePaths.each { String modulePath ->
                    arg(value: '-modules')
                    arg(value: modulePath)
                }
                arg(value: 'jsdoc.js')
                getInputs().files.files.each {
                    arg(value: it.canonicalPath)
                }
                options.each {
                    arg(value: it)
                }
            }
        } else {
            throw new IllegalArgumentException('Output must be exactly 1 File object. Example: outputs.file = file("myFile")')
        }
    }
}
