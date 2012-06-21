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

class ClosureCompilerExtension {
    public static final NAME = "closure"
    @Input CompilerOptions compilerOptions = new CompilerOptions()
    @Input String compilationLevel = 'SIMPLE_OPTIMIZATIONS'
    @Input String warningLevel = 'DEFAULT'
    @Input FileCollection externs = null
}
