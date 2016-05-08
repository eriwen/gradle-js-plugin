/**
 * Copyright 2013 Eric Wendelin
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
import org.gradle.api.internal.file.FileResolver
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject

class JsPlugin implements Plugin<Project> {

    private final Instantiator instantiator;
    private final FileResolver fileResolver;

    @Inject
    public JsPlugin(Instantiator instantiator, FileResolver fileResolver) {
        this.instantiator = instantiator;
        this.fileResolver = fileResolver;
    }

    void apply(final Project project) {
        project.extensions.create(ClosureCompilerExtension.NAME, ClosureCompilerExtension)
        project.extensions.create(JsDocExtension.NAME, JsDocExtension)
        project.extensions.create(JsHintExtension.NAME, JsHintExtension)
        project.extensions.create(RequireJsExtension.NAME, RequireJsExtension)
        project.extensions.create(Props2JsExtension.NAME, Props2JsExtension)
        project.extensions.create(JavaScriptExtension.NAME, JavaScriptExtension, project, instantiator, fileResolver)

        configureDependencies(project)
        applyTasks(project)
    }

    void applyTasks(final Project project) {
        project.task('combineJs', type: CombineJsTask, group: 'Build', description: 'Combine many JavaScript files into one') {}
        project.tasks.whenTaskAdded { task ->
            if (task instanceof MinifyJsTask) {
                task.extensions.create(ClosureCompilerExtension.NAME, ClosureCompilerExtension)
            }
        }
        project.task('minifyJs', type: MinifyJsTask, group: 'Build', description: 'Minify JavaScript using Closure Compiler') {}
        project.task('gzipJs', type: GzipJsTask, group: 'Build', description: 'GZip a given JavaScript file') {}
        project.task('jshint', type: JsHintTask, group: 'Verification', description: 'Analyze JavaScript sources with JSHint') {}
        project.task('jsdoc', type: JsDocTask, group: 'Documentation', description: 'Produce HTML documentation with JSDoc 3') {}
        project.task('props2js', type: Props2JsTask, group: 'Build', description: 'Convert Java properties files for use with JavaScript') {}
        project.task('requireJs', type: RequireJsTask, group: 'Build', description: 'Run the r.js Optimizer to produce Require.js output') {}
        project.task('html2js', type: Html2jsTask, group: 'Build', description: 'Bundle HTML templates into AngularJS compatible javascript files') {}
    }

    void configureDependencies(final Project project) {
        project.configurations {
            rhino
        }
        project.dependencies {
            rhino 'org.mozilla:rhino:1.7R3'
        }
        // TODO: have 'check' depend on jshint
    }
}
