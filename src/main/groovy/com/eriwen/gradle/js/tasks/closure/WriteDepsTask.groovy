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

class WriteDepsTask extends DefaultTask{
	@Input @SkipWhenEmpty SourceDirectorySet source = null;
	@OutputFile def destination
	
	public WriteDepsTask() {
		Task superTask = this
		project.afterEvaluate {
			if (superTask.destination == null) {
				superTask.destination = project.file("${project.buildDir}/deps.js")
			}
		}
		def execTask = project.task(this.name+".exec",type:Exec) { Task task ->
			doFirst {
			commandLine = ["python","lib/closure-library/closure/bin/build/depswriter.py"]
			superTask.source.getSrcDirs().each {File dir ->
				def relpath = project.relativePath(dir.canonicalPath)
				task.args("--root_with_prefix=${relpath} ../../../../${relpath}")
			}
			args("--output_file=${project.buildDir}/deps.js")
			workingDir = project.projectDir
			println commandLine.join(" ")
			}
		}
	    dependsOn execTask
		execTask.enabled = true
    }
	
	@TaskAction
	public void run(){
		
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
