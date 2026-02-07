plugins {
    `java-library`
    `java-test-fixtures`
}

dependencies {
    implementation(project(":src:common"))
    implementation(libs.guava)
    implementation(libs.jakarta.inject)
    implementation(libs.okio)
    api(libs.jspecify)

    testFixturesApi(project(":src:common"))
    testFixturesApi(project(":src:virtual-machine"))
    testFixturesApi(testFixtures(project(":src:common")))
    testFixturesApi(libs.guava)
    implementation(libs.dagger)
    annotationProcessor(libs.dagger.compiler)
    testFixturesImplementation(libs.dagger)
    testFixturesAnnotationProcessor(libs.dagger.compiler)
    testFixturesImplementation(libs.jakarta.inject)
    testFixturesApi(libs.okio)
    testFixturesApi(libs.bundles.testingFrameworks)

    testImplementation(project(":src:testing"))
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.bundles.testingFrameworks)
}
