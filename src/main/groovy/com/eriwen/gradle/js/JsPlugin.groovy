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
package com.eriwen.gradle.js

import org.gradle.api.Project
import org.gradle.api.Plugin

import com.eriwen.gradle.js.tasks.*
import com.eriwen.gradle.js.tasks.Props2JsTask

class JsPlugin implements Plugin<Project> {
    private Project project
    protected JsPluginConvention jsPluginConvention

    void apply(final Project project) {
        this.project = project
        this.jsPluginConvention = new JsPluginConvention()

        project.convention.plugins.js = jsPluginConvention
        configureDependencies()
        applyTasks(project)
    }

    void applyTasks(final Project project) {
        project.task('minifyJs', type: MinifyJsTask) {
            compilerOptions = project.convention.plugins.js.compilerOptions
            compilationLevel = project.convention.plugins.js.compilationLevel
            warningLevel = project.convention.plugins.js.warningLevel
        }

        project.task('combineJs', type: CombineJsTask) {}

        project.task('gzipJs', type: GzipJsTask) {}

        project.task('jshint', type: JsHintTask) {}

        project.task('jsdoc', type: JsDocTask) {
            options = project.convention.plugins.js.options
        }

        project.task('js', type: JsTask) {
            compilerOptions = project.convention.plugins.js.compilerOptions
            compilationLevel = project.convention.plugins.js.compilationLevel
            warningLevel = project.convention.plugins.js.warningLevel
        }

        project.task('props2js', type: Props2JsTask) {
            type = project.convention.plugins.js.type
            functionName = project.convention.plugins.js.functionName
        }
    }

    void configureDependencies() {
        project.configurations {
            rhino
        }
        project.repositories {
            mavenCentral()
        }
        project.dependencies {
            rhino 'org.mozilla:rhino:1.7R3'
        }
    }
}