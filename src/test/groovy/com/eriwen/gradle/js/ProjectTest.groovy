package com.eriwen.gradle.js

import org.gradle.api.Project
import org.junit.Rule
import spock.lang.Specification

class ProjectTest extends Specification {

    @Rule @Delegate
    public TestProjectRule projectRule = new TestProjectRule()
    
    Project project
    
    def setup() {
        project = projectRule.project
    }
    
    def methodMissing(String name, args) {
        project."$name"(*args)
    }
    
    def propertyMissing(String name) {
        project."$name"
    }

    def propertyMissing(String name, arg) {
        project."$name" = arg
    }
}
