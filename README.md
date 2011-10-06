# Gradle Javascript Plugin! #
Aiming to be the *simplest* way to manage your Javascript in a build.

# Quick Start #
Wrangling your JS in a [Gradle](http://gradle.org) build is easy! Just add this to your *build.gradle* file:

    // Pull the plugin from a Maven Repo
    buildscript {
        repositories {
            mavenCentral()
        }
        dependencies {
            classpath 'com.eriwen:gradle-js-plugin:0.1'
        }
    }
    // Invoke the plugin
    apply plugin: 'js'

    // Specify a collection of files to be combined, then minified and finally GZip compressed.
    processJs {
        input = fileTree(dir: "${projectDir}/js", include: "**/*.js")
        output = file("${buildDir}/combinedMinifiedAndGzipped.js")
    }

**Need more than 1 set of files generated? Just add another *processJs* block:**
    processJs {
        input = fileTree(dir: "${projectDir}/otherdir", includes: ["file1.js", "file2.js"])
        output = file("${buildDir}/teenytiny.js")
    }

**Want more fine-grained control or just want to combine, minify or zip your files?**
    // Combine JS files
    combineJs {
        input = fileTree(dir: "${projectDir}/js", include: "**/*.js")
        output = file("${buildDir}/all.js")
    }
    
    // Minify with Google Closure Compiler
    minifyJs {
        input = file("${buildDir}/all.js")
        output = file("${buildDir}/all-min.js")
        warningLevel = 'QUIET'
    }
    
    // GZip it!
    gzipJs {
        input = file("${buildDir}/all-min.js")
        output = input
    }

# Available Tasks and Options #
 - combineJs
 -- input = [FileCollection](http://gradle.org/current/docs/javadoc/org/gradle/api/file/FileCollection.html) of files to merge
 -- output = File for combined output
 - minifyJs Uses the [Google Closure Compiler](http://code.google.com/closure/compiler/)
 -- input = File to minify
 -- output = File for minified output
 -- *(Optional)* compilationLevel = 'WHITESPACE_ONLY', 'SIMPLE_OPTIMIZATIONS' (default), or 'ADVANCED_OPTIMIZATIONS' (are you *hardcore*?)
 -- *(Optional)* warningLevel = 'QUIET', 'DEFAULT' (default), or 'VERBOSE'
 -- *(Optional)* options = [CompilerOptions](http://code.google.com/p/closure-compiler/source/browse/trunk/src/com/google/javascript/jscomp/CompilerOptions.java?r=1187) object
 - gzipJs
 -- input = File to compress
 -- output = File for compressed output
 - processJs
 -- input = File to minify
 -- output = File for minified output
 -- *(Optional)* compilationLevel = 'WHITESPACE_ONLY', 'SIMPLE_OPTIMIZATIONS' (default), or 'ADVANCED_OPTIMIZATIONS' (are you *hardcore*?)
 -- *(Optional)* warningLevel = 'QUIET', 'DEFAULT' (default), or 'VERBOSE'
 -- *(Optional)* options = [CompilerOptions](http://code.google.com/p/closure-compiler/source/browse/trunk/src/com/google/javascript/jscomp/CompilerOptions.java?r=1187) object
 - What, you want more? [Tell me then!](https://github.com/eriwen/gradle-js-plugin/issues)
