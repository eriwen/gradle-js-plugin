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

import org.ajoberstar.gradle.git.tasks.GitClone
import org.gradle.api.tasks.StopExecutionException
import java.nio.file.Files

class GetClosure extends GitClone{

	public GetClosure() {
		uri 'http://code.google.com/p/closure-library/'
		credentials = null
		onlyIf { !Files.exists(destinationDir.toPath()) }
		project.afterEvaluate {
			destinationPath project.closure.library.location
			outputs.file project.file("${destinationDir}/.git/config")
			if (Files.exists(destinationDir.toPath())) {
				enabled = false
				//throw new StopExecutionException("Closure Library location already exists")
			}
		}
		doFirst {
			if (Files.exists(destinationDir.toPath())) {
				throw new StopExecutionException("Closure Library location already exists")
			}
		}
	}
}
