package com.eriwen.gradle.js

import org.gradle.api.Project
import org.gradle.util.ConfigureUtil
import com.eriwen.gradle.js.source.JavaScriptSourceSetContainer
import com.eriwen.gradle.js.source.internal.DefaultJavaScriptSourceSetContainer
import com.eriwen.gradle.js.source.internal.InternalGradle

class JavaScriptExtension {
    public static final NAME = "javascript"

    final JavaScriptSourceSetContainer source

    JavaScriptExtension(Project project) {
        source = InternalGradle.toInstantiator(project).newInstance(DefaultJavaScriptSourceSetContainer, project)
    }

    void source(Closure closure) {
        ConfigureUtil.configure(closure, source)
    }
}
