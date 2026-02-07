plugins {
    `java-library`
    `java-test-fixtures`
}

dependencies {
    implementation(project(":src:common"))
    implementation(project(":src:compiler-frontend"))
    implementation(project(":src:virtual-machine"))
    implementation(project(":src:compiler-backend"))

    implementation(libs.guava)
    implementation(libs.jakarta.inject)
    implementation(libs.okio)
    implementation(libs.picocli)

    testFixturesApi(project(":src:common"))
    testFixturesApi(testFixtures(project(":src:compiler-frontend")))
    testFixturesApi(testFixtures(project(":src:compiler-backend")))
    testFixturesApi(testFixtures(project(":src:virtual-machine")))
    testFixturesApi(libs.guava)
    testFixturesApi(libs.okio)
    testFixturesApi(libs.bundles.testingFrameworks)
    testFixturesImplementation(libs.dagger)
    testFixturesAnnotationProcessor(libs.dagger.compiler)
    testFixturesImplementation(libs.jakarta.inject)

    testImplementation(project(":src:testing"))
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.bundles.testingFrameworks)
}
