package com.eriwen.gradle.js.source.internal

import com.eriwen.gradle.js.source.JavaScriptSourceSet

class DefaultJavaScriptSourceSet implements JavaScriptSourceSet {

    private final String name

    DefaultJavaScriptSourceSet(String name) {
        this.name = name
    }

    String getName() {
        return name
    }

}
