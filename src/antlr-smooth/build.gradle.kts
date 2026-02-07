import org.gradle.api.plugins.antlr.AntlrTask

plugins {
    id("antlr")
}

tasks.withType<AntlrTask>().configureEach {
    arguments.add("-visitor")
}

dependencies {
    antlr(libs.antlr4)
}
