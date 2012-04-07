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
        classpath 'com.eriwen:gradle-js-plugin:0.4'
    }
}
// Invoke the plugin
apply plugin: 'js'

// Specify a collection of files to be combined, then minified and finally GZip compressed.
task combinejs(type: com.eriwen.gradle.js.tasks.CombineJsTask) {
	source = ["${projectDir}/js/file1.js", "${projectDir}/js/file2.js"]
	dest = file("${buildDir}/all.js")
}

task minifyjs(type: com.eriwen.gradle.js.tasks.MinifyJsTask, dependsOn: 'combinejs') {
	source = file("${buildDir}/all.js")
	dest = file("${buildDir}/all-min.js")
	warningLevel = 'QUIET'
}

task gzipjs(type: com.eriwen.gradle.js.tasks.GzipJsTask, dependsOn: 'minifyjs') {
	source = file("${buildDir}/all-min.js")
	dest = file("${buildDir}/all-min.js")
}
```

**Need more than 1 set of files generated? We'll have to declare our tasks a bit differently:**

```groovy
task jsDev(type: com.eriwen.gradle.js.tasks.JsTask) {
    source = ["${projectDir}/js/file1.js", "${projectDir}/js/file2.js"]
    dest = file("${buildDir}/all-debug.js")
    compilationLevel = 'WHITESPACE_ONLY'
}

task jsProd(type: com.eriwen.gradle.js.tasks.JsTask) {
    source = ["${projectDir}/js/file1.js", "${projectDir}/js/file2.js"]
    dest = file("${buildDir}/all.js")
}
```

**[JSHint](http://jshint.com) support**

```groovy
task jshintjs(type: com.eriwen.gradle.js.tasks.JsHintTask) {
    source = ['js/main.js']
}
```

**[JSDoc 3](https://github.com/micmath/jsdoc) support**
```groovy
task jsdocjs(type: com.eriwen.gradle.js.tasks.JsDocTask) {
    source = ["${projectDir}/js/file1.js", "${projectDir}/js/file2.js"]
    destinationDir = file("${buildDir}/jsdoc")
    options = []
}
```

**[props2Js](https://github.com/nzakas/props2js) support**
```groovy
task props(type: com.eriwen.gradle.js.tasks.Props2JsTask) {
    propertiesFile = file("${projectDir}/src/test/resources/test.properties")
    dest = file("${buildDir}/props.jsonp")
    type = 'jsonp'
    functionName = 'fn'
}
```

# Available Tasks and Options #
### combineJs ###
- source = [FileCollection](http://gradle.org/current/docs/javadoc/org/gradle/api/file/FileCollection.html) of files to merge
- dest = File for combined output

### minifyJs (Uses the [Google Closure Compiler](http://code.google.com/closure/compiler/)) ###
- source = File to minify
- dest = File for minified output
- *(Optional)* compilationLevel = 'WHITESPACE_ONLY', 'SIMPLE_OPTIMIZATIONS' (default), or 'ADVANCED_OPTIMIZATIONS' (are you *hardcore*?)
- *(Optional)* warningLevel = 'QUIET', 'DEFAULT' (default), or 'VERBOSE'
- *(Optional)* compilerOptions = [CompilerOptions](http://code.google.com/p/closure-compiler/source/browse/trunk/src/com/google/javascript/jscomp/CompilerOptions.java?r=1187) object

### gzipJs ###
- source = File to compress
- dest = File for compressed output

### js (DEPRECATED, will be removed in v0.5) ###
- inputs.files Files to combine, minify and gzip
- optputs.files File for tiny output :)
- *(Optional)* compilationLevel = 'WHITESPACE_ONLY', 'SIMPLE_OPTIMIZATIONS' (default), or 'ADVANCED_OPTIMIZATIONS' (are you *hardcore*?)
- *(Optional)* warningLevel = 'QUIET', 'DEFAULT' (default), or 'VERBOSE'
- *(Optional)* compilerOptions = [CompilerOptions](http://code.google.com/p/closure-compiler/source/browse/trunk/src/com/google/javascript/jscomp/CompilerOptions.java?r=1187) object

### jshint ###
- source = Files to assess with JSHint

### jsdoc ###
- source = Files to generate documentation for
- destinationDir = Directory path to put JSDoc output
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

### props2js ###
- propertiesFile = Properties file to process
- dest = Destination file for output
- type = One of: 'js', 'json', or 'jsonp'
- *(Optional)* functionName = Function name to wrap JSONP

What, you want more? [Tell me then!](https://github.com/eriwen/gradle-js-plugin/issues)

# See Also #
The [Gradle CSS Plugin](https://github.com/eriwen/gradle-css-plugin)!
