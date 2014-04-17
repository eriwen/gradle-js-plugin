package com.eriwen.gradle.js

import org.gradle.api.Project
import org.gradle.api.internal.file.FileResolver
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.ConfigureUtil
import com.eriwen.gradle.js.source.JavaScriptSourceSetContainer
import com.eriwen.gradle.js.source.internal.DefaultJavaScriptSourceSetContainer

class JavaScriptExtension {
    public static final NAME = "javascript"

    final JavaScriptSourceSetContainer source

    JavaScriptExtension(Project project, Instantiator instantiator, FileResolver fileResolver) {
        source = instantiator.newInstance(DefaultJavaScriptSourceSetContainer, project, instantiator, fileResolver)
    }

    void source(Closure closure) {
        ConfigureUtil.configure(closure, source)
    }
}
