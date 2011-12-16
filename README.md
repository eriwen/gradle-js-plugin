# Gradle Javascript Plugin! #
Aiming to be the *simplest* way to manage your Javascript in a build.

# Quick Start #
Wrangling your JS in a [Gradle](http://gradle.org) build is easy! Just add this to your *build.gradle* file:

```groovy
    // Pull the plugin from a Maven Repo
    buildscript {
        repositories {
            mavenCentral()
        }
        dependencies {
            classpath 'com.eriwen:gradle-js-plugin:0.2'
        }
    }
    // Invoke the plugin
    apply plugin: 'js'

    // Specify a collection of files to be combined, then minified and finally GZip compressed.
    js {
        inputs.files fileTree(dir: "${projectDir}/js", include: "**/*.js")
        outputs.file file("${buildDir}/combinedMinifiedAndGzipped.js")
    }
```

**Need more than 1 set of files generated? Just add another *js* block:**

```groovy
    js {
        inputs.files fileTree(dir: "${projectDir}/otherdir", includes: ["file1.js", "file2.js"])
        outputs.file file("${buildDir}/teenytiny.js")
    }
```

**Want more fine-grained control or just want to combine, minify or zip your files?**

```groovy
    // Combine JS files
    combineJs {
        inputs.files fileTree(dir: "${projectDir}/js", include: "**/*.js")
        outputs.file file("${buildDir}/all.js")
    }
    
    // Minify with Google Closure Compiler
    minifyJs {
        inputs.file file("${buildDir}/all.js")
        outputs.file file("${buildDir}/all-min.js")
        warningLevel = 'QUIET'
    }
    
    // GZip it!
    gzipJs {
        inputs.file file("${buildDir}/all-min.js")
        outputs.file input
    }
```

**[JSHint](http://jshint.com) support**

```groovy
    jshint {
	    inputs.files files('js/main.js')
	    outputs.file file("${buildDir}/jshint.out")
    }
```

# Available Tasks and Options #
### combineJs ###
 - input.files [FileCollection](http://gradle.org/current/docs/javadoc/org/gradle/api/file/FileCollection.html) of files to merge
 - output.file File for combined output

### minifyJs (Uses the [Google Closure Compiler](http://code.google.com/closure/compiler/)) ###
 - input.file File to minify
 - output.file File for minified output
 - *(Optional)* compilationLevel = 'WHITESPACE_ONLY', 'SIMPLE_OPTIMIZATIONS' (default), or 'ADVANCED_OPTIMIZATIONS' (are you *hardcore*?)
 - *(Optional)* warningLevel = 'QUIET', 'DEFAULT' (default), or 'VERBOSE'
 - *(Optional)* options = [CompilerOptions](http://code.google.com/p/closure-compiler/source/browse/trunk/src/com/google/javascript/jscomp/CompilerOptions.java?r=1187) object

### gzipJs ###
 - input.file File to compress
 - output.file File for compressed output

### js ###
 - input.files Files to combine, minify and gzip
 - output.file File for tiny output :)
 - *(Optional)* compilationLevel = 'WHITESPACE_ONLY', 'SIMPLE_OPTIMIZATIONS' (default), or 'ADVANCED_OPTIMIZATIONS' (are you *hardcore*?)
 - *(Optional)* warningLevel = 'QUIET', 'DEFAULT' (default), or 'VERBOSE'
 - *(Optional)* options = [CompilerOptions](http://code.google.com/p/closure-compiler/source/browse/trunk/src/com/google/javascript/jscomp/CompilerOptions.java?r=1187) object

### jshint ###
- input.files Files to assess with JSHint
- output.file File for report output

What, you want more? [Tell me then!](https://github.com/eriwen/gradle-js-plugin/issues)

# See Also #
The [Gradle CSS Plugin](https://github.com/eriwen/gradle-css-plugin)!