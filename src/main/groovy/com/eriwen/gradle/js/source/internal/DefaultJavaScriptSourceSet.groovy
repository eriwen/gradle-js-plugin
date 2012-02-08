package com.eriwen.gradle.js.source.internal

import com.eriwen.gradle.js.source.JavaScriptSourceSet
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.util.ConfigureUtil
import org.gradle.util.GUtil

class DefaultJavaScriptSourceSet implements JavaScriptSourceSet {

    private final String name
    private final String displayName
    private final DefaultSourceDirectorySet js
    
    DefaultJavaScriptSourceSet(String name, Project project) {
        this.name = name
        this.displayName = GUtil.toWords(name)
        this.js = new DefaultSourceDirectorySet(name, String.format("%s JavaScript source", displayName), InternalGradle.toFileResolver(project))
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


}
