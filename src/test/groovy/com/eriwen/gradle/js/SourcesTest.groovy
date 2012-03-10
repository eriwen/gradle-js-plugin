package com.eriwen.gradle.js

class SourcesTest extends ProjectTest {
    
    def setup() {
        apply plugin: JsPlugin
    }

    def createSource(String name = "main") {
        def srcRoot = newFolder("src", name, "js")
        (new File(srcRoot, "source.js") << "").canonicalFile
    }

    def "can add to the source set container"() {
        given:
        def src = createSource("custom")

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

