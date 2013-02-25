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

import com.google.javascript.jscomp.CompilerOptions
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.util.ConfigureUtil


class ClosureExtension {
    public static final NAME = "closure"
    ClosureCompilerExtension compiler
    ClosureLibraryExtension library
	
	public ClosureExtension() {
//		this.compiler = this.extensions.create("compiler",ClosureCompilerExtension)
//		this.library = this.extensions.create("library",ClosureLibraryExtension)
		compiler = new ClosureCompilerExtension()
    	library = new ClosureLibraryExtension()
	}
	
	public ClosureCompilerExtension getCompiler() {
		return compiler
	}
	
	public setCompiler(ClosureCompilerExtension o){
		this.compiler = o
	}
	
	public compiler(Closure closure){
		ConfigureUtil.configure(closure, getCompiler());
	}
	
		
	public ClosureLibraryExtension getLibrary() {
		return library
	}
	
	public setLibrary(ClosureLibraryExtension o){
		this.library = o
	}
	
	public library(Closure closure){
		ConfigureUtil.configure(closure, getLibrary());
	}
	
	public CompilerOptions getCompilerOptions() {
		this.compiler.getCompilerOptions()
	}
	
	public setCompilerOptions(CompilerOptions o) {
		this.compiler.setCompilerOptions(o)
	}
	
	public compilerOptions(Closure closure) {
		ConfigureUtil.configure(closure, getCompiler().getCompilerOptions());
	}

	public String getCompilationLevel() {
		this.compiler.getCompilationLevel()
	}
	
	public setCompilationLevel(String o) {
		this.compiler.setCompilationLevel(o)
	}

	public String getWarningLevel() {
		this.compiler.getWarnings()
	}
	
	public setWarningLevel(String o) {
		this.compiler.setWarnings(o)
	}

	public FileCollection getExterns() {
		this.compiler.getExterns()
	}
	
	public setExterns(FileCollection o) {
		this.compiler.setExterns(o)
	}
	
public class ClosureCompilerExtension {
    public static final NAME = "closure"
    @Input CompilerOptions compilerOptions = new CompilerOptions()
    @Input String compilationLevel = 'SIMPLE_OPTIMIZATIONS'
    @Input String warnings = 'DEFAULT'
    @Input FileCollection externs = null
    public ClosureCompilerExtension() {
    	compilerOptions.closurePass = true
    }
    public void options(Closure closure) {
    	ConfigureUtil.configure(closure, compilerOptions);
    }
    	
}

public class ClosureLibraryExtension {
	String location = 'lib/closure-library/'
    String getLocation(){
    	return location
    }
    void setLocation(String o) {
    	this.location = o
    }
}

	
}
