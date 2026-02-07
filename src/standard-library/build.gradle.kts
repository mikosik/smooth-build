plugins {
    `java-library`
}

tasks.jar {
    archiveBaseName.set("std_lib")
}

dependencies {
    implementation(project(":src:common"))
    implementation(project(":src:virtual-machine"))
    implementation(libs.guava)
    implementation(libs.okio)
    implementation(libs.zip4j)

    testImplementation(testFixtures(project(":src:virtual-machine")))
    testImplementation(testFixtures(project(":src:evaluator")))
    testImplementation(project(":src:testing"))
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.bundles.testingFrameworks)
}

val junit4 by configurations.creating

dependencies {
    junit4(libs.junit4)
}

val junit4files by tasks.register<Copy>("junit4files") {
    from(configurations.named("junit4"))
    into(layout.buildDirectory.dir("junit4files"))
}

tasks.test { dependsOn(junit4files) }

val zip by tasks.register<Zip>("zip") {
    archiveBaseName.set("std_lib-all")
    dependsOn(tasks.jar)
    from(tasks.jar)
    from(layout.projectDirectory.dir("src/main/smooth"))
}

tasks.assemble { dependsOn(zip) }
