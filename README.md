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
        classpath 'com.eriwen:gradle-js-plugin:0.3'
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

**Need more than 1 set of files generated? We'll have to declare our tasks a bit differently:**

```groovy
task jsDev(type: com.eriwen.gradle.js.tasks.JsTask) {
    file2 = fileTree(dir: "${projectDir}/src/test/resources", includes: ['file2.js'])
    file1 = fileTree(dir: "${projectDir}/src/test/resources", includes: ['file1.js'])
    inputs.files file2 + file1
    outputs.file file("${buildDir}/all-debug.js")
    compilationLevel = 'WHITESPACE_ONLY'
}

task jsProd(type: com.eriwen.gradle.js.tasks.JsTask) {
    file2 = fileTree(dir: "${projectDir}/src/test/resources", includes: ['file2.js'])
    file1 = fileTree(dir: "${projectDir}/src/test/resources", includes: ['file1.js'])
    inputs.files file2 + file1
    outputs.file file("${buildDir}/all.js")
}
```

**What if I want my JS files combined in a certain order?**
This is how you do it (kludgy) for now, but we're working on a more elegant solution (see progress on [Issue #4](https://github.com/eriwen/gradle-js-plugin/issues/4)):

```groovy
js {
    file2 = fileTree(dir: "${projectDir}/js", includes: ['file2.js'])
    file1 = fileTree(dir: "${projectDir}/js", includes: ['file1.js'])
    inputs.files file2 + file1
    outputs.file file("${buildDir}/all.js")
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

**[JSDoc 3](https://github.com/micmath/jsdoc) support**
```groovy
jsdoc {
    inputs.files fileTree(dir: "${projectDir}/js", include: "**/*.js")
    outputs.dir "${buildDir}/jsdoc"
    options = []
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
- *(Optional)* compilerOptions = [CompilerOptions](http://code.google.com/p/closure-compiler/source/browse/trunk/src/com/google/javascript/jscomp/CompilerOptions.java?r=1187) object

### gzipJs ###
- input.file File to compress
- output.file File for compressed output

### js ###
- input.files Files to combine, minify and gzip
- output.file File for tiny output :)
- *(Optional)* compilationLevel = 'WHITESPACE_ONLY', 'SIMPLE_OPTIMIZATIONS' (default), or 'ADVANCED_OPTIMIZATIONS' (are you *hardcore*?)
- *(Optional)* warningLevel = 'QUIET', 'DEFAULT' (default), or 'VERBOSE'
- *(Optional)* compilerOptions = [CompilerOptions](http://code.google.com/p/closure-compiler/source/browse/trunk/src/com/google/javascript/jscomp/CompilerOptions.java?r=1187) object

### jshint ###
- input.files Files to assess with JSHint
- output.file File for report output

### jsdoc ###
- input.files Files to generate documentation for
- output.dir Directory path to put JSDoc output
- *(Optional)* options = []

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

What, you want more? [Tell me then!](https://github.com/eriwen/gradle-js-plugin/issues)

# See Also #
The [Gradle CSS Plugin](https://github.com/eriwen/gradle-css-plugin)!
