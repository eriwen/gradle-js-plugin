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
import com.eriwen.gradle.js.ResourceUtil
import org.gradle.api.GradleException

class Props2JsTask extends DefaultTask {
    private static final String PROPS2JS_JAR = 'props2js-0.1.0.jar'
    private static final String TMP_DIR = 'tmp/js'
    private static final ResourceUtil RESOURCE_UTIL = new ResourceUtil()
    private static final Set<String> AVAILABLE_TYPES = ['js', 'json', 'jsonp']

    File propertiesFile
    File dest
    // FIXME: populate defaults using plugin convention (issue #14)
    String functionName = ''
    String type = 'json'

    @TaskAction
    def run() {
        if (!propertiesFile) {
            logger.warn 'The syntax "inputs.file file(..)" is deprecated! Please use `propertiesFile = file("path/file.props")`'
            logger.warn 'This will be removed in the next version of the JS plugin'
            propertiesFile = getInputs().files.files.toArray()[0] as File
        }

        if (!dest) {
            logger.warn 'The syntax "outputs.file file(..)" is deprecated! Please use `dest = file("dest/file.js")`'
            dest = getOutputs().files.files.toArray()[0] as File
        }

        if (!propertiesFile.exists()) {
            throw new GradleException("${propertiesFile} does not exist!")
        }

        // Prevent arguments that don't make sense
        if (!AVAILABLE_TYPES.contains(type)) {
            throw new IllegalArgumentException("Invalid type specified. Must be one of: ${AVAILABLE_TYPES.join(',')}")
        } else if (type == 'json' && functionName) {
            throw new IllegalArgumentException("Cannot specify a 'functionName' when type is 'json'")
        } else if (type != 'json' && !functionName) {
            throw new IllegalArgumentException("Must specify a 'functionName' when type is 'jsonp' or 'js'")
        }

        final File props2JsJar = RESOURCE_UTIL.extractFileToDirectory(new File(project.buildDir, TMP_DIR), PROPS2JS_JAR)
        final List<String> props2JsArgs = [props2JsJar.canonicalPath, propertiesFile.canonicalPath, '-t', type]
        if (functionName) {
            props2JsArgs.addAll(['--name', functionName])
        }
        props2JsArgs.addAll(['-o', dest.canonicalPath])
        project.javaexec {
            main = '-jar'
            args = props2JsArgs
        }
    }
}
