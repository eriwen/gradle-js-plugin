/**
 * Copyright 2011 Eric Wendelin
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

import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskAction

class JsHintTask extends Exec {
    String input
    String outputFilePath
    def options = []

    @TaskAction
    def run() {
        // FIXME: writes too late
        new File(outputFilePath).write('')
        // TODO: package jslint.js with the plugin
        commandLine = ["jslint"] + options + input.split(' ')
        standardOutput = new BufferedOutputStream(new FileOutputStream(new File("${outputFilePath}/jslint.xml")))
    }
}
