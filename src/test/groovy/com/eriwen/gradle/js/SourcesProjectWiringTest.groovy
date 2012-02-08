package com.eriwen.gradle.js

import spock.lang.Specification
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

class SourcesProjectWiringTest extends Specification {
    
    Project project = ProjectBuilder.builder().build()
    
    def setup() {
        project.apply plugin: JsPlugin
    }
    
    def "can add to the source set container"() {
        when:
        project.javascript {
            source {
                custom {

                }
            }
        }

        then:
        project.javascript.source.custom.name == "custom"
    }
}
