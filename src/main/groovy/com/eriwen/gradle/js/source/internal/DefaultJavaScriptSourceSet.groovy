package com.eriwen.gradle.js.source.internal

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.file.FileResolver
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.ConfigureUtil
import org.gradle.util.GUtil
import org.gradle.util.GradleVersion

import com.eriwen.gradle.js.source.JavaScriptProcessingChain
import com.eriwen.gradle.js.source.JavaScriptSourceSet

class DefaultJavaScriptSourceSet implements JavaScriptSourceSet {

    private final String name
    private final String displayName
    private final SourceDirectorySet js
    private final JavaScriptProcessingChain processing
    private final FileCollection processed

    DefaultJavaScriptSourceSet(String name, Project project, Instantiator instantiator, FileResolver fileResolver) {
        this.name = name
        this.displayName = GUtil.toWords(name)
        if (GradleVersion.current().compareTo(GradleVersion.version("2.12")) >= 0) {
            Class fileTreeFactory = Class.forName("org.gradle.api.internal.file.collections.DefaultDirectoryFileTreeFactory")
            def directoryFileTreeFactory = fileTreeFactory.getConstructor().newInstance()
            this.js = new DefaultSourceDirectorySet(name, String.format("%s JavaScript source", displayName), fileResolver, directoryFileTreeFactory)
        } else {
            this.js = new DefaultSourceDirectorySet(name, String.format("%s JavaScript source", displayName), fileResolver)
        }
        this.processing = instantiator.newInstance(DefaultJavaScriptProcessingChain, project, this, instantiator)
        this.processed = project.files({ processing.empty ? js : processing.last().outputs.files })
    }

    @Override
    String getName() {
        name
    }

    @Override
    SourceDirectorySet getJs() {
        js
    }

    @Override
    SourceDirectorySet js(Action<SourceDirectorySet> action) {
        action.execute(js)
        js
    }

    @Override
    JavaScriptSourceSet configure(Closure closure) {
        if (GradleVersion.current().compareTo(GradleVersion.version("2.14")) >= 0) {
            ConfigureUtil.configureSelf(closure, this)
        } else {
            ConfigureUtil.configure(closure, this, false)
        }
    }

    @Override
    JavaScriptProcessingChain getProcessing() {
        processing
    }

    @Override
    JavaScriptProcessingChain processing(Action<JavaScriptProcessingChain> action) {
        action.execute(processing)
        processing
    }

    @Override
    FileCollection getProcessed() {
        processed
    }
}
