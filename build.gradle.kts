plugins {
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.errorprone)
}

subprojects {
    apply(plugin = "com.diffplug.spotless")
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        format("misc") {
            target("*.gradle", "*.gradle.kts", ".gitattributes", ".gitignore")
            trimTrailingWhitespace()
            leadingTabsToSpaces()
            endWithNewline()
        }
        antlr4 {
            target("**/*.g4")
            antlr4Formatter()
        }
    }

    tasks.withType<Test> {
        maxParallelForks = Runtime.getRuntime().availableProcessors()
    }

    plugins.withType<JavaPlugin> {
        tasks.withType<Test>().configureEach {
            jvmArgs("-Xshare:off")
        }
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(25))
            }
        }
        configure<com.diffplug.gradle.spotless.SpotlessExtension> {
            java {
                targetExclude("**/build/generated-src/**/*.java", "**/build/generated/**/*.java")
                removeUnusedImports()
                palantirJavaFormat(libs.versions.palantir.get()).style("GOOGLE")
            }
        }

        apply(plugin = "net.ltgt.errorprone")
        dependencies {
            "errorprone"(libs.errorprone.core)
            "errorprone"(libs.nullaway)
        }
        plugins.withId("java-library") {
            dependencies {
                "api"(libs.jspecify)
            }
        }
        plugins.withId("java") {
            if (!project.plugins.hasPlugin("java-library")) {
                dependencies {
                    "implementation"(libs.jspecify)
                }
            }
        }
        tasks.withType<JavaCompile>().configureEach {
            (options as ExtensionAware).extensions.configure<net.ltgt.gradle.errorprone.ErrorProneOptions>("errorprone") {
                error("NullAway")
                excludedPaths.set(".*/build/generated/.*")
                option("NullAway:AnnotatedPackages", "org.smoothbuild")
                // Avoid errors when field is injected by picocli.
                // In some cases, when picocli does not always inject some field
                // (for example user is not required to provide some option in commandline),
                // such field is annotated in code with @Nullable.
                option("NullAway:ExternalInitAnnotations", "picocli.CommandLine.Command")
            }
        }

        @Suppress("UnstableApiUsage")
        configure<TestingExtension> {
            suites {
                val test by getting(JvmTestSuite::class) {
                    useJUnitJupiter()
                }
            }
        }
    }
}
