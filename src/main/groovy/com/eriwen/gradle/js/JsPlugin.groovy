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
import com.eriwen.gradle.js.source.internal.DefaultJavaScriptSourceDirectorySet
import com.eriwen.gradle.js.source.JavaScriptSourceSet
import com.eriwen.gradle.js.tasks.closure.*
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.Task

class JsPlugin implements Plugin<Project> {

    void apply(final Project project) {
        project.extensions.create(ClosureExtension.NAME, ClosureExtension)
        project.extensions.create(JsDocExtension.NAME, JsDocExtension)
        project.extensions.create(JsHintExtension.NAME, JsHintExtension)
        project.extensions.create(RequireJsExtension.NAME, RequireJsExtension)
        project.extensions.create(Props2JsExtension.NAME, Props2JsExtension)
        project.extensions.create(JavaScriptExtension.NAME, JavaScriptExtension, project)

        configureDependencies(project)
        applyTasks(project)
    }

    void applyTasks(final Project project) {
        project.task('combineJs', type: CombineJsTask, group: 'Build', description: 'Combine many JavaScript files into one') {}
        def getClosureTask = project.task('getClosure', 
        	type: com.eriwen.gradle.js.tasks.closure.GetClosure, group: 'Build', 
        	description: 'Download the closure library if it does not already exist') {}
        project.task('depsJs', type: com.eriwen.gradle.js.tasks.closure.WriteDepsTask, 
        	group: 'Build', description: 
        	'Write out JavaScript dependencies using Closure DepsWriter') {}
        project.task('buildJs', type: com.eriwen.gradle.js.tasks.closure.BuildJsTask, 
        	group: 'Build', description: 
        	'Build JavaScript using Closure Build Tool and the Closure Compiler') {}
        project.task('minifyJs', type: MinifyJsTask, group: 'Build', description: 'Minify JavaScript using Closure Compiler') {}
        project.task('gzipJs', type: GzipJsTask, group: 'Build', description: 'GZip a given JavaScript file') {}
        project.task('jshint', type: JsHintTask, group: 'Verification', description: 'Analyze JavaScript sources with JSHint') {}
        project.task('jsdoc', type: JsDocTask, group: 'Documentation', description: 'Produce HTML documentation with JSDoc 3') {}
        project.task('props2js', type: Props2JsTask, group: 'Build', description: 'Convert Java properties files for use with JavaScript') {}
        project.task('requireJs', type: RequireJsTask, group: 'Build', description: 'Run the r.js Optimizer to produce Require.js output') {}

        project.tasks.withType(com.eriwen.gradle.js.tasks.closure.BuildJsTask) { Task task ->
          conventionMapping.map("source") { project.javascript.source.main.js }
          conventionMapping.map("dest") { project.file("${project.buildDir}/${task.namespace}.js") }
        }
        
        project.tasks.withType(com.eriwen.gradle.js.tasks.closure.WriteDepsTask) { Task task ->
        /*task.conventionMapping.map("source") { 
          	def srcSet = new DefaultJavaScriptSourceDirectorySet(
          			task.name, project)
          	project.javascript.source.each { JavaScriptSourceSet src ->
          		srcSet.source(src.js)
          	}
          	return srcSet
		   }*/
          conventionMapping.map("source") { project.javascript.source.main.js }
          conventionMapping.map("dest") { "${project.buildDir}/deps.js" }
         }
    }

    void configureDependencies(final Project project) {
        project.configurations {
            rhino
        }
        project.repositories {
            mavenCentral()
        }
        project.dependencies {
            rhino 'org.mozilla:rhino:1.7R4'
        }
        // TODO: have 'check' depend on jshint
    }
}
