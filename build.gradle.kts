import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.eriwen"
version = "6.0.0"
description = "Combine (w/requireJS), lint, document, minify and gzip your JavaScript!"
val webUrl by extra { "https://github.com/eriwen/gradle-js-plugin" }
val githubUrl by extra { "https://github.com/eriwen/gradle-js-plugin" }

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.13.0"
    id("org.jetbrains.dokka") version "1.4.20"
    id("gradle.site") version "0.6"
}

repositories {
    mavenCentral()
}

val kotlinVersion = "1.4.20"
val junitPlatformVersion = "1.1.0"
val spekVersion = "2.0.15"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.google.javascript:closure-compiler:v20210302")
//    implementation("io.jdev.html2js:html2js:0.1")

    testImplementation(kotlin("test"))
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion") {
        exclude(group = "org.jetbrains.kotlin")
    }

    testRuntimeOnly(kotlin("reflect"))
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion") {
        exclude(group = "org.junit.platform")
        exclude(group = "org.jetbrains.kotlin")
    }

    testImplementation("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"

    publishAlways()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform {
        includeEngines("spek2")
    }

    testLogging {
        events("PASSED", "FAILED", "SKIPPED")
    }
}

val dokkaHtml by tasks.getting(DokkaTask::class) {
    outputDirectory.set(buildDir.resolve("javadoc"))
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(dokkaHtml)
}
artifacts.add("archives", dokkaJar)

val sourcesJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles sources JAR"
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}
artifacts.add("archives", sourcesJar)

val jar by tasks.getting(Jar::class) {
    manifest.attributes.apply {
        put("Implementation-Title", "Gradle Digest Plugin")
        put("Implementation-Version", project.version)
        put("Built-By", System.getProperty("user.name"))
        put("Built-JDK", System.getProperty("java.version"))
        put("Built-Gradle", project.gradle.gradleVersion)
    }
}

gradlePlugin {
    plugins {
        create("js") {
            id = "com.eriwen.gradle.js"
            implementationClass = "com.eriwen.gradle.JsPlugin"
        }
    }
}

pluginBundle {
    website = webUrl
    vcsUrl = githubUrl
    description = project.description
    tags = listOf("js")

    (plugins) {
        "js" {
            displayName = "Gradle JS Plugin"
        }
    }
}

site {
    outputDir.set(file("$rootDir/docs"))
    websiteUrl.set(webUrl)
    vcsUrl.set(githubUrl)
}

// Configure maven-publish plugin
publishing {
    publications.withType<MavenPublication> {
        artifact(dokkaJar)
        artifact(sourcesJar)

        pom {
            name.set(project.name)
            description.set(project.description)
            url.set(webUrl)

            scm {
                url.set(githubUrl)
                connection.set("scm:https://eriwen@github.com/eriwen/gradle-js-plugin.git")
                developerConnection.set("scm:git://github.com/eriwen/gradle-js-plugin.git")
            }

            licenses {
                license {
                    name.set("The Apache Software License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.set("repo")
                }
            }

            developers {
                developer {
                    id.set("eriwen")
                    name.set("Eric Wendelin")
                    email.set("me@eriwen.com")
                }
            }
        }
    }
}

val install by tasks.creating {
    description = "Installs plugin to local repo"
    dependsOn("publishToMavenLocal")
}
