package com.eriwen.gradle.js

import com.google.javascript.jscomp.CompilerOptions
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input

class ClosureCompilerExtension {
    val NAME = "closure"
    @Input val compilerOptions = CompilerOptions()
    @Input val compilationLevel = "SIMPLE_OPTIMIZATIONS"
    @Input val warningLevel = "DEFAULT"
    @Input val externs : FileCollection? = null
}
