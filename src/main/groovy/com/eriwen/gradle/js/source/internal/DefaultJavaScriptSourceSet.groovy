package com.eriwen.gradle.js.source.internal

import com.eriwen.gradle.js.source.JavaScriptProcessingChain
import com.eriwen.gradle.js.source.JavaScriptSourceSet
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.util.ConfigureUtil
import org.gradle.util.GUtil

class DefaultJavaScriptSourceSet implements JavaScriptSourceSet {

    private final String name
    private final String displayName
    private final DefaultSourceDirectorySet js
    private final JavaScriptProcessingChain processing
    private final FileCollection processed
    
    DefaultJavaScriptSourceSet(String name, Project project) {
        this.name = name
        this.displayName = GUtil.toWords(name)
        this.js = new DefaultSourceDirectorySet(name, String.format("%s JavaScript source", displayName), InternalGradle.toFileResolver(project))
        this.processing = InternalGradle.toInstantiator(project).newInstance(DefaultJavaScriptProcessingChain, project, this)
        this.processed = project.files({ processing.empty ? js : processing.last().outputs.files })
    }

    String getName() {
        name
    }
    
    SourceDirectorySet getJs() {
        js
    }

    SourceDirectorySet js(Action<SourceDirectorySet> action) {
        action.execute(js)
        js
    }
    
    JavaScriptSourceSet configure(Closure closure) {
        ConfigureUtil.configure(closure, this, false)
    }

    JavaScriptProcessingChain getProcessing() {
        processing
    }

    JavaScriptProcessingChain processing(Action<JavaScriptProcessingChain> action) {
        action.execute(processing)
        processing
    }

    FileCollection getProcessed() {
        processed
    }
}
