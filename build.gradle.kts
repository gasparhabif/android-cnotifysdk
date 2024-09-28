// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.androidLibrary) apply false
    id("com.vanniktech.maven.publish") version "0.29.0" apply false
    id("com.gradleup.nmcp") version "0.0.8" 
    signing

}

buildscript {
    dependencies {
        classpath(libs.android.maven.gradle.plugin)
    }
}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

nmcp {
  publishAllProjectsProbablyBreakingProjectIsolation {
    username = project.properties["SONATYPE_USERNAME"] as String
    password = project.properties["SONATYPE_PASSWORD"] as String

    publicationType = "AUTOMATIC"
  }
}

signing {
    useGpgCmd()
//    sign(publishing.publications["mavenJava"])
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            useVersion("1.7.10")
        }
    }
}