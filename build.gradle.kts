plugins {
    kotlin("jvm") version "2.3.0"
    application
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("org.jlleitschuh.gradle.ktlint") version "12.3.0"
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("main.MainKt")
}

sourceSets {
    main {
        kotlin.srcDirs("src")
        resources.srcDirs("src")
    }
    test {
        kotlin.srcDirs("test")
        resources.srcDirs("test")
    }
}

kotlin {
    jvmToolchain(17)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

detekt {
    config.setFrom(files("detekt.yml"))
    buildUponDefaultConfig = true
}

ktlint {
    version.set("1.5.0")
}
