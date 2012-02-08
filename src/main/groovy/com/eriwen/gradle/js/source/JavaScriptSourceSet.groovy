package com.eriwen.gradle.js.source

import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.file.FileCollection
import org.gradle.api.file.SourceDirectorySet
import org.gradle.util.Configurable

interface JavaScriptSourceSet extends Named, Configurable<JavaScriptSourceSet> {

    SourceDirectorySet getJs()

    SourceDirectorySet js(Action<SourceDirectorySet> action)

    JavaScriptProcessingChain getProcessing()

    JavaScriptProcessingChain processing(Action<JavaScriptProcessingChain> action)
    
    FileCollection getProcessed()
}
