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
import org.gradle.api.GradleException
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.OutputFile

class Props2JsTask extends SourceTask {
    private static final String PROPS2JS_JAR = 'props2js-0.1.0.jar'
    private static final String TMP_DIR = "tmp${File.separator}js"
    private static final ResourceUtil RESOURCE_UTIL = new ResourceUtil()
    private static final Set<String> AVAILABLE_TYPES = ['js', 'json', 'jsonp']

    @OutputFile def dest

    File getDest() {
        project.file(dest)
    }

    @TaskAction
    def run() {
        final String functionName = project.props.functionName
        final String type = project.props.type

        // Prevent arguments that don't make sense
        if (!AVAILABLE_TYPES.contains(type)) {
            throw new IllegalArgumentException("Invalid type specified. Must be one of: ${AVAILABLE_TYPES.join(',')}")
        } else if (type == 'json' && functionName) {
            throw new IllegalArgumentException("Cannot specify a 'functionName' when type is 'json'")
        } else if (type != 'json' && !functionName) {
            throw new IllegalArgumentException("Must specify a 'functionName' when type is 'jsonp' or 'js'")
        }

        if (source.files.size() != 1) {
            throw new GradleException("Only 1 file can be processed with Props2Js. Please run Props2Js for each file.")
        }

        final File props2JsJar = RESOURCE_UTIL.extractFileToDirectory(new File(project.buildDir, TMP_DIR), PROPS2JS_JAR)
        final List<String> props2JsArgs = [props2JsJar.canonicalPath, (source.files.toArray() as File[])[0].canonicalPath, '-t', type]
        if (functionName) {
            props2JsArgs.addAll(['--name', functionName])
        }
        props2JsArgs.addAll(['-o', (dest as File).canonicalPath])
        project.javaexec {
            main = '-jar'
            args = props2JsArgs
        }
    }
}
