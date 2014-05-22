/**
 * Copyright 2012 Tom Dunstan
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

import io.jdev.html2js.TemplateBundler
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Optional

class Html2jsTask extends SourceTask {
    @OutputFile def dest
    @Optional @Input def moduleName
    @Optional @Input def base
    @Optional @Input def quoteChar
    @Optional @Input def indentString
    @Optional @Input Boolean useStrict
    @Optional @Input def fileHeader

    File getDest() {
        project.file(dest)
    }

    @TaskAction
    def run() {
        logger.debug("Combining ${source.files.collect { it.canonicalPath }} into ${getDest()}")

        TemplateBundler bundler = new TemplateBundler()
        bundler.module = moduleName
        if(base) bundler.base = project.file(base).canonicalFile
        if(quoteChar) bundler.quoteChar = quoteChar as String
        if(indentString) bundler.indentString = indentString as String
        if(useStrict != null) bundler.useStrict = useStrict
        if(fileHeader) bundler.fileHeader = fileHeader as String

        bundler.bundleTemplates(getDest().canonicalFile, source.files.canonicalFile)
    }
}
