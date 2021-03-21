plugins {
    id("com.gradle.enterprise") version("3.6")
}

rootProject.name = "gradle-js-plugin"

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}
