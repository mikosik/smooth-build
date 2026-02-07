plugins {
    java
}

val installation by tasks.register<Copy>("installation") {
    group = "build"
    description = "Extracts smooth distribution to build/installation"
    dependsOn(project(":src:distribution").tasks.named("zip"))
    from(project(":src:distribution").tasks.named("zip").map { it.outputs.files.map { f -> zipTree(f) } })
    into(layout.buildDirectory.dir("installation"))
}

tasks.test { dependsOn(installation) }

tasks.test {
    inputs.dir("../../doc")
}

dependencies {
    implementation(project(":src:cli"))
    implementation(libs.guava)
    implementation(libs.okio)
    implementation(libs.zip4j)

    testImplementation(project(":src:common"))
    testImplementation(project(":src:testing"))
    testImplementation(testFixtures(project(":src:common")))
    testImplementation(testFixtures(project(":src:virtual-machine")))
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.bundles.testingFrameworks)
}
