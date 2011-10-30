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
package com.eriwen.gradle.js

import org.gradle.api.logging.Logger
import org.gradle.api.Project
import org.gradle.api.Plugin

import com.eriwen.gradle.js.tasks.*

class JsPlugin implements Plugin<Project> {
    private Project project
    private Logger logger
    protected JsPluginConvention jsPluginConvention

    void apply(final Project project) {
        this.project = project
        this.logger = logger
        this.jsPluginConvention = new JsPluginConvention()

        project.convention.plugins.js = jsPluginConvention
        configureDependencies()
        applyTasks(project)
    }

    void applyTasks(final Project project) {
        project.task('minifyJs', type: MinifyJsTask) {
            input = project.convention.plugins.js.input
            output = project.convention.plugins.js.output
            options = project.convention.plugins.js.options
            compilationLevel = project.convention.plugins.js.compilationLevel
            warningLevel = project.convention.plugins.js.warningLevel
        }

        project.task('combineJs', type: CombineJsTask) {
            input = project.convention.plugins.js.input
            output = project.convention.plugins.js.output
        }

        project.task('gzipJs', type: GzipJsTask) {
            input = project.convention.plugins.js.input
            output = project.convention.plugins.js.output
        }

        project.task('jshint', type: JsHintTask) {}

        project.task('js', type: JsTask) {
            input = project.convention.plugins.js.input
            output = project.convention.plugins.js.output
            options = project.convention.plugins.js.options
            compilationLevel = project.convention.plugins.js.compilationLevel
            warningLevel = project.convention.plugins.js.warningLevel
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