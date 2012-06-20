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
        classpath 'com.eriwen:gradle-js-plugin:1.0.1'
    }
}
// Invoke the plugin
apply plugin: 'js'

// Declare your sources
javascript.source {
    dev {
        js {
            srcDir jsSrcDir
            include "*.js"
            exclude "*.min.js"
        }
    }
    prod {
        js {
            srcDir jsSrcDir
            include "*.min.js"
        }
    }
}

// Specify a collection of files to be combined, then minified and finally GZip compressed.
task combinejs(type: com.eriwen.gradle.js.tasks.CombineJsTask) {
    if (env == 'prod') {
        source = javascript.source.dev.js.files
    } else {
        source = javascript.source.prod.js.files
    }
    dest = file("${buildDir}/all.js")
}

task minifyjs(type: com.eriwen.gradle.js.tasks.MinifyJsTask, dependsOn: 'combinejs') {
    source = file("${buildDir}/all.js")
    dest = file("${buildDir}/all-min.js")
    closure {
        warningLevel = 'QUIET'
    }
}

task gzipjs(type: com.eriwen.gradle.js.tasks.GzipJsTask, dependsOn: 'minifyjs') {
    source = file("${buildDir}/all-min.js")
    dest = file("${buildDir}/all-min.js")
}
```

**Need more than 1 set of files generated? We'll have to declare our tasks a bit differently:**

```groovy
task jsDev(type: com.eriwen.gradle.js.tasks.CombineJsTask) {
    source = ["${projectDir}/js/file1.js", "${projectDir}/js/file2.js"]
    dest = file("${buildDir}/all-debug.js")
}

task jsProd(type: com.eriwen.gradle.js.tasks.CombineJsTask) {
    source = ["${projectDir}/js/file1.js", "${projectDir}/js/file2.js"]
    dest = file("${buildDir}/all.js")
}
```

**[JSHint](http://jshint.com) support**
```groovy
task jshintjs(type: com.eriwen.gradle.js.tasks.JsHintTask) {
    source = javascript.source.dev.js.files
}
```

**[JSDoc 3](https://github.com/micmath/jsdoc) support**
```groovy
task jsdocjs(type: com.eriwen.gradle.js.tasks.JsDocTask) {
    source = ["${projectDir}/js/file1.js", "${projectDir}/js/file2.js"]
    destinationDir = file("${buildDir}/jsdoc")
}
```

**[props2Js](https://github.com/nzakas/props2js) support**
```groovy
task processProps(type: com.eriwen.gradle.js.tasks.Props2JsTask) {
    source = file("${projectDir}/src/test/resources/test.properties")
    dest = file("${buildDir}/props.jsonp")
    props {
        type = 'jsonp'
        functionName = 'fn'
    }
}
```

# Available Tasks and Options #
### combineJs ###
- source = Collection of file paths of files to merge
- dest = File for combined output

### minifyJs (Uses the [Google Closure Compiler](http://code.google.com/closure/compiler/)) ###
- source = File to minify
- dest = File for minified output
- *(Optional)* closure.compilationLevel = 'WHITESPACE_ONLY', 'SIMPLE_OPTIMIZATIONS' (default), or 'ADVANCED_OPTIMIZATIONS' (are you *hardcore*?)
- *(Optional)* closure.warningLevel = 'QUIET', 'DEFAULT' (default), or 'VERBOSE'
- *(Optional)* closure.compilerOptions = [CompilerOptions](http://code.google.com/p/closure-compiler/source/browse/trunk/src/com/google/javascript/jscomp/CompilerOptions.java?r=1918) object
- *(Optional)* closure.externs = [FileCollection](http://gradle.org/docs/current/javadoc/org/gradle/api/file/FileCollection.html) object

### gzipJs ###
- source = File to compress
- dest = File for compressed output

### jshint ###
- source = Files to assess with JSHint

### jsdoc ###
- source = Files to generate documentation for
- destinationDir = Directory path to put JSDoc output
- *(Optional)* options.options = []

```
JSDoc 3 options:
-t or --template <value> The name of the template to use. Default: the "default" template
-c or --configure <value> The path to the configuration file. Default: jsdoc __dirname + /conf.json
-e or --encoding <value> Assume this encoding when reading all source files. Default: utf-8
-T or --test Run all tests and quit.
-d or --destination <value> The path to the output folder. Use "console" to dump data to the console. Default: console
-p or --private Display symbols marked with the @private tag. Default: false.
-r or --recurse Recurse into subdirectories when scanning for source code files.
-h or --help Print this message and quit.
-X or --explain Dump all found doclet internals to console and quit.
-q or --query <value> Provide a querystring to define custom variable names/values to add to the options hash.
-u or --tutorials <value> Directory in which JSDoc should search for tutorials.
```

### props2js ###
- source = Properties file to process
- dest = Destination file for output
- props.type = One of: 'js', 'json', or 'jsonp'
- *(Optional)* props.functionName = Function name to wrap JSONP

What, you want more? [Tell me then!](https://github.com/eriwen/gradle-js-plugin/issues)

# See Also #
The [Gradle CSS Plugin](https://github.com/eriwen/gradle-css-plugin)!
