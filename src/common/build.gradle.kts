plugins {
    `java-library`
    `java-test-fixtures`
}

dependencies {
    api(libs.jspecify)
    implementation(libs.guava)
    implementation(libs.dagger)
    annotationProcessor(libs.dagger.compiler)
    implementation(libs.jakarta.inject)
    implementation(libs.okio)
    implementation(libs.zip4j)
    implementation(libs.antlr4.runtime)

    testFixturesApi(libs.okio)
    testFixturesApi(libs.bundles.testingFrameworks)
    testFixturesImplementation(libs.guava)
    testFixturesImplementation(libs.dagger)
    testFixturesAnnotationProcessor(libs.dagger.compiler)
    testFixturesImplementation(libs.jakarta.inject)

    testImplementation(project(":src:testing"))
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.bundles.testingFrameworks)
}
