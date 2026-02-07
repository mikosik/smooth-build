plugins {
    `java-library`
    `java-test-fixtures`
}

dependencies {
    implementation(project(":src:antlr-smooth"))
    implementation(project(":src:common"))
    api(libs.jspecify)
    implementation(libs.guava)
    implementation(libs.jakarta.inject)
    implementation(libs.okio)

    testFixturesImplementation(libs.dagger)
    testFixturesAnnotationProcessor(libs.dagger.compiler)
    testFixturesImplementation(libs.jakarta.inject)
    testFixturesApi(project(":src:common"))
    testFixturesApi(project(":src:compiler-frontend"))
    testFixturesApi(testFixtures(project(":src:common")))
    testFixturesApi(testFixtures(project(":src:virtual-machine")))
    testFixturesApi(libs.guava)
    testFixturesApi(libs.okio)
    testFixturesApi(libs.bundles.testingFrameworks)

    testImplementation(project(":src:testing"))
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.bundles.testingFrameworks)
}
