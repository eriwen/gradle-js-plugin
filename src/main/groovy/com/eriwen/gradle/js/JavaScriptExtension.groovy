package com.eriwen.gradle.js

import com.eriwen.gradle.js.source.JavaScriptSourceSet
import com.eriwen.gradle.js.source.internal.DefaultJavaScriptSourceSet
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import org.gradle.api.internal.file.FileResolver
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.ConfigureUtil

class JavaScriptExtension {
    public static final NAME = "javascript"

    final NamedDomainObjectContainer<JavaScriptSourceSet> source

    JavaScriptExtension(Project project, Instantiator instantiator, FileResolver fileResolver) {
        source = project.container(JavaScriptSourceSet.class, new NamedDomainObjectFactory<JavaScriptSourceSet>() {
            @Override
            JavaScriptSourceSet create(String name) {
                return instantiator.newInstance(DefaultJavaScriptSourceSet.class, name, project, instantiator, fileResolver);
            }
        })
    }

    void source(Closure closure) {
        ConfigureUtil.configure(closure, source)
    }
}
