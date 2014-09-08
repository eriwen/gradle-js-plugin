please take a look at the original code in [Eric Wendelins](https://github.com/eriwen) repository [here](https://github.com/eriwen/gradle-js-plugin).

# differences to the original code:

configure encoding to be used by the combineJs task:

```groovy
// Configure the built-in task
combineJs {
    encoding = "UTF-8"
    source = javascript.source.dev.js.files
    dest = file("${buildDir}/all.js")
}
```

leave out encoding to use your systems default.
