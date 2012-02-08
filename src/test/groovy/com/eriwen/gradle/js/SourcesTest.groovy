package com.eriwen.gradle.js

class SourcesTest extends ProjectTest {
    
    def setup() {
        apply plugin: JsPlugin
    }
    
    def "can add to the source set container"() {
        given:
        def srcRoot = newFolder("src", "custom", "js")
        def src = (new File(srcRoot, "source.js") << "").canonicalFile

        when:
        javascript {
            source {
                custom {
                    js {
                        srcDir "src/custom/js"
                    }
                }
            }
        }

        then:
        javascript.source.custom.name == "custom"
        javascript.source.custom.js.files.toList() == [src]

    }
}
