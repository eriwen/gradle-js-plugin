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
package com.eriwen.gradle.js.tasks.closure

import groovy.lang.Closure;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.FileTreeElement;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.util.PatternFilterable;
import org.gradle.api.tasks.util.PatternSet;
import org.gradle.internal.Factory;
import org.gradle.util.DeprecationLogger;
import org.gradle.api.Task
import org.gradle.api.file.SourceDirectorySet

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.eriwen.gradle.js.JsMinifier
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.api.DefaultTask

class BuildJsTask extends DefaultTask {
    @Input @SkipWhenEmpty SourceDirectorySet source = null;    
    
    @Input Boolean compile = true
    @Input String namespace = null
    @OutputFile def destination
    
	
//    output.files = project.file(jsOutput)
	public BuildJsTask() {
		final Task superTask = this
		project.afterEvaluate {
	    	if (superTask.destination == null) {
    			superTask.destination = project.file("${project.buildDir}/${namespace}.js")
    		}
    	}
 
    def buildTask = project.task(superTask.name + '.build',type: Exec) { Task task ->
        doFirst {
        if (namespace == null){
        	throw new TaskExecutionException("You must supply a namespace for a Closure Build task")
        }
        commandLine = ["python",
        	"${project.closure.library.location}/closure/bin/build/closurebuilder.py",
            "--root=${project.closure.library.location}"]
        superTask.source.srcDirs.each {File dir ->
            task.args("--root=${dir.canonicalPath}")
	    }
        Set<File> externs = project.closure.externs ? project.closure.externs.files : [] as Set<File>
        externs.each {File file ->
            task.args("--compiler_flags=--externs=j${file.canonicalPath}")
        }
        args("--namespace=${superTask.namespace}")
        args("--output_mode=script")
        args("--output_file=${superTask.dest}")
        workingDir = project.projectDir
        println commandLine.join(" ")
        }
    }
    def compileTask = project.task(superTask.name + '.compile',
    	type:com.eriwen.gradle.js.tasks.MinifyJsTask){
        dependsOn buildTask
        onlyIf { superTask.compile }
        project.afterEvaluate {
            dest = superTask.dest
            source superTask.dest
        }
    }
    dependsOn compileTask
    dependsOn buildTask
    buildTask.enabled = true
    compileTask.enabled = true
    }

    SourceDirectorySet getSource() {
       return source
    }
    
    public setSource(SourceDirectorySet o) {
    	source = o
    }
    public File getDest() {
       return project.file(this.destination)
    }
    
    public setDest(Object o) {
    	destination = o
    }
}
