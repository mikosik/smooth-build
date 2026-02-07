plugins {
    `java-library`
    `java-test-fixtures`
}

dependencies {
    implementation(project(":src:common"))
    implementation(project(":src:compiler-frontend"))
    implementation(project(":src:virtual-machine"))
    implementation(libs.guava)
    implementation(libs.dagger)
    annotationProcessor(libs.dagger.compiler)
    implementation(libs.jakarta.inject)
    implementation(libs.okio)

    testFixturesApi(project(":src:common"))
    testFixturesApi(project(":src:virtual-machine"))
    testFixturesApi(testFixtures(project(":src:compiler-frontend")))
    testFixturesApi(testFixtures(project(":src:virtual-machine")))

    testImplementation(project(":src:testing"))
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.bundles.testingFrameworks)
}
