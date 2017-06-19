# Gradle Javascript Plugin! [![Build Status](https://secure.travis-ci.org/eriwen/gradle-js-plugin.png)](http://travis-ci.org/eriwen/gradle-js-plugin)
Aiming to be the *simplest* way to manage your JavaScript in a build.

# Quick Start
Wrangling your JS in a [Gradle](https://gradle.org) build is easy! Just add this to your *build.gradle* file:

### Gradle 2.1+
```groovy
plugins {
  id "com.eriwen.gradle.js" version "2.14.1"
}
```

### Gradle 2.0-
```groovy
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath "com.eriwen:gradle-js-plugin:1.12.1"
  }
}

apply plugin: "com.eriwen.gradle.js"
```


```groovy
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
```

### Combining Files ([options](#combinejs))
```groovy
// Configure the built-in task
combineJs {
    encoding = "UTF-8"
    source = javascript.source.dev.js.files
    dest = file("${buildDir}/all.js")
}

// Create new CombineJsTasks if you have multiple sets of JS files
task jsDev(type: com.eriwen.gradle.js.tasks.CombineJsTask) {
    source = ["${projectDir}/js/file1.js", "${projectDir}/js/file2.js"]
    dest = file("${buildDir}/all-debug.js")
}

task jsProd(type: com.eriwen.gradle.js.tasks.CombineJsTask) {
    source = ["${projectDir}/js/file1.js", "${projectDir}/js/file2.js"]
    dest = file("${buildDir}/all.js")
}
```

### Minifying files with [Google Closure Compiler](https://github.com/google/closure-compiler) ([options](#minifyjs-uses-the-google-closure-compiler))
```groovy
minifyJs {
    source = combineJs
    dest = file("${buildDir}/all-min.js")
    sourceMap = file("${buildDir}/all.sourcemap.json")
    closure {
        warningLevel = 'QUIET'
    }
}
```

### GZip JS ([options](#gzipjs))
```groovy
gzipJs {
    source = minifyjs
    dest = file("${buildDir}/all-min.js")
}
```

### [JSHint](http://jshint.com) support ([options](#jshint))
```groovy
jshint {
    source = javascript.source.dev.js.files
    dest = file("${buildDir}/jshint.out")
    reporter = 'checkstyle'
    jshint.options = [expr: "true", unused: "true"]
}
```

### [JSDoc 3](https://github.com/jsdoc3/jsdoc) support ([options](#jsdoc))
```groovy
jsdoc {
    source = ["${projectDir}/js/file1.js", "${projectDir}/js/file2.js"]
    destinationDir = file("${buildDir}/jsdoc")
}
```

### [props2js](https://github.com/nzakas/props2js) support ([options](#props2js))
```groovy
props2js {
    source = file("${projectDir}/src/test/resources/test.properties")
    dest = file("${buildDir}/props.jsonp")
    props {
        type = 'jsonp'
        functionName = 'fn'
    }
}
```

### [require.js](http://requirejs.org/) via r.js ([options](#requirejs))
```groovy
requireJs {
    source = javascript.source.dev.js.files
    dest = file("${buildDir}/out.js")
    requirejs.buildprofile = new File("src/main/resources/requirejs-config.js")
}
```

# Built-in Tasks and Options
### combineJs
- source = Collection of file paths of files to merge
- dest = File for combined output

### minifyJs (Uses the [Google Closure Compiler](https://github.com/google/closure-compiler))
- source = File to minify
- dest = File for minified output
- *(Optional)* sourcemap = Source map file
- *(Optional)* closure.compilationLevel = 'WHITESPACE_ONLY', 'SIMPLE_OPTIMIZATIONS' (default), or 'ADVANCED_OPTIMIZATIONS' (are you *hardcore*?)
- *(Optional)* closure.warningLevel = 'QUIET', 'DEFAULT' (default), or 'VERBOSE'
- *(Optional)* closure.compilerOptions = [CompilerOptions](https://github.com/google/closure-compiler/blob/master/src/com/google/javascript/jscomp/CompilerOptions.java) object
- *(Optional)* closure.externs = [FileCollection](http://gradle.org/docs/current/javadoc/org/gradle/api/file/FileCollection.html) object

### gzipJs
- source = File to compress
- dest = File for compressed output

### jshint
- source = Files to assess with JSHint
- dest = File for JSHint output
- *(Optional)* reporter = Only 'checkstyle' supported right now. Defaults to plain JSHint output.
- *(Optional)* ignoreExitCode = Fail build if `false` and jshint finds problems. Default is `true`.
- *(Optional)* outputToStdOut = `true` will output to STDOUT instead of file. Default is `false`.
- *(Optional)* jshint.options = Map of options (e.g. `[expr: "true", unused: "true"]`)
- *(Optional)* jshint.predef = Map of predefined globals so JSHint doesn't complain about them


### jsdoc
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

### props2js
- source = Properties file to process
- dest = Destination file for output
- props.type = One of: 'js', 'json', or 'jsonp'
- *(Optional)* props.functionName = Function name to wrap JSONP

### requireJs
- source = Source JS files
- dest = Output JS file
- *(Must declare this or `requirejs.options`)* requirejs.buildprofile = File reference for config [example](https://github.com/eriwen/gradle-js-plugin/blob/master/src/test/resources/requirejs/build.js)
- requirejs.options = Map of options [require.js docs](http://requirejs.org/docs/optimization.html#options)
- *(Optional)* ignoreExitCode = Fail build if `false` and require.js did not run successfully. Default is `false`.
- *(Optional)* requirejs.impl = r.js implementation file.  Version 2.1.8 is provided within this plugin.  Specifying this option allows users to specify a version of the require optimizer of their own choosing

What, you want more? [Tell me!](https://github.com/eriwen/gradle-js-plugin/issues)

## Contributors
This project is made possible due to the efforts of these fine people:

* [Eric Wendelin](http://eriwen.com) - Original author and maintainer
* [Luke Daley](https://github.com/alkemist) - Advice and improved project structure and testing
* [Josh Newman](https://github.com/jnewman) - AMD and CommonJS work
* Martin Ziel - Allowing minifyJs task to accept multiple files as input
* [Joe Fitzgerald](https://github.com/joefitzgerald) - JSHint and RequireJS features
* [levsa](https://github.com/levsa) - JSHint predef and checkstyle reporter
* [Martin Snyder](https://github.com/MartinSnyder) - requireJs impl option
* [Aaron Arnett](https://github.com/a3rnett) - Remove explicit MavenCentral dependency
* [sv99](https://github.com/sv99) - Improve Gradle version compatibility

## See Also
The [Gradle CSS Plugin](https://github.com/eriwen/gradle-css-plugin)!
