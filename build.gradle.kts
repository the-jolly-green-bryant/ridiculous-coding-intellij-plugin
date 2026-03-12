import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.api.plugins.quality.PmdExtension

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.2.1"
    id("checkstyle")
    id("pmd")
}

group = "com.nmeylan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.1")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

configure<CheckstyleExtension> {
    toolVersion = "12.1.0"
}

configure<PmdExtension> {
    toolVersion = "7.16.0"
    isConsoleOutput = true
    ruleSets = listOf(
        "category/java/errorprone.xml",
        "category/java/bestpractices.xml",
        "category/java/design.xml"
    )
}