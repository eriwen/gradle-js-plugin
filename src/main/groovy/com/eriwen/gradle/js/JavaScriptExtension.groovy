package com.eriwen.gradle.js

import com.eriwen.gradle.js.source.JavaScriptSourceSetContainer
import com.eriwen.gradle.js.source.internal.DefaultJavaScriptSourceSetContainer
import org.gradle.api.Project
import org.gradle.api.internal.Instantiator
import org.gradle.util.ConfigureUtil

class JavaScriptExtension {

    public static final NAME = "javascript"
    
    final JavaScriptSourceSetContainer source

    JavaScriptExtension(Project project) {
        Instantiator instantiator = project.services.get(Instantiator)
        source = instantiator.newInstance(DefaultJavaScriptSourceSetContainer, instantiator)
    }

    void source(Closure closure) {
        ConfigureUtil.configure(closure, source)
    }
}
