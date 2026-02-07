import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar

plugins {
    application
    `java-test-fixtures`
}

application {
    mainClass.set("org.smoothbuild.cli.Main")
}

dependencies {
    implementation(project(":src:common"))
    implementation(project(":src:antlr-report-matcher"))
    implementation(project(":src:compiler-frontend"))
    implementation(project(":src:compiler-backend"))
    implementation(project(":src:virtual-machine"))
    implementation(project(":src:evaluator"))

    implementation(libs.guava)
    implementation(libs.dagger)
    annotationProcessor(libs.dagger.compiler)
    implementation(libs.jakarta.inject)
    implementation(libs.okio)
    implementation(libs.picocli)

    testFixturesApi(project(":src:common"))
    testFixturesApi(project(":src:cli"))
    testFixturesApi(testFixtures(project(":src:common")))
    testFixturesApi(testFixtures(project(":src:virtual-machine")))
    testFixturesApi(testFixtures(project(":src:compiler-frontend")))
    testFixturesApi(libs.guava)
    testFixturesImplementation(libs.dagger)
    testFixturesAnnotationProcessor(libs.dagger.compiler)
    testFixturesImplementation(libs.jakarta.inject)
    testFixturesApi(libs.okio)
    testFixturesApi(libs.bundles.testingFrameworks)

    testImplementation(project(":src:testing"))
    testImplementation(project(":src:virtual-machine"))
    testImplementation(testFixtures(project(":src:common")))
    testImplementation(testFixtures(project(":src:virtual-machine")))
    testImplementation(testFixtures(project(":src:evaluator")))
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.bundles.testingFrameworks)
}

val sourceSets = the<SourceSetContainer>()

val fatJar by tasks.register<Jar>("fatJar") {
    manifest { attributes(mapOf<String, String>()) }
    archiveBaseName.set("smooth")
    dependsOn(configurations.runtimeClasspath)
    dependsOn(sourceSets.named("main").get().output)
    from(sourceSets.named("main").get().output)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    }) {
        exclude("META-INF/**", "LICENSE", "module-info.class")
    }
}

tasks.assemble { dependsOn(fatJar) }
