import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.api.plugins.quality.PmdExtension

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.2.1"
    id("checkstyle")
    id("pmd")
}

group = "com.bryantjames"
val gitSha = providers.environmentVariable("GITHUB_SHA").orNull
val dateVersion = providers.environmentVariable("BUILD_SEMVER_DATE").orNull

version = when {
    !dateVersion.isNullOrBlank() && !gitSha.isNullOrBlank() ->
        "$dateVersion+${gitSha.take(7)}"

    !dateVersion.isNullOrBlank() ->
        "$dateVersion+no-sha"

    else ->
        "0.0.0-local"
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

intellijPlatform {
    pluginVerification {
        ides {
            recommended()
        }
    }

    publishing {
        token = providers.environmentVariable("JETBRAINS_MARKETPLACE_TOKEN")
    }

    signing {
        certificateChain = providers.environmentVariable("JETBRAINS_CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("JETBRAINS_PRIVATE_KEY")
        password = providers.environmentVariable("JETBRAINS_PRIVATE_KEY_PASSWORD")
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