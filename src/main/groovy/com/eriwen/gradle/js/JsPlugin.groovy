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

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.eriwen.gradle.js.tasks.*

class JsPlugin implements Plugin<Project> {

    void apply(final Project project) {
        project.extensions.create(ClosureCompilerExtension.NAME, ClosureCompilerExtension)
        project.extensions.create(JsDocExtension.NAME, JsDocExtension)
        project.extensions.create(Props2JsExtension.NAME, Props2JsExtension)
        project.extensions.create(JavaScriptExtension.NAME, JavaScriptExtension, project)

        configureDependencies(project)
        applyTasks(project)
    }

    void applyTasks(final Project project) {
        project.task('minifyJs', type: MinifyJsTask) {}
        project.task('combineJs', type: CombineJsTask) {}
        project.task('gzipJs', type: GzipJsTask) {}
        project.task('jshint', type: JsHintTask) {}
        project.task('jsdoc', type: JsDocTask) {}
        project.task('props2js', type: Props2JsTask) {}
    }

    void configureDependencies(final Project project) {
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
