plugins {
    `java-library`
}

dependencies {
    api(libs.jspecify)
    implementation(libs.truth)

    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.bundles.testingFrameworks)
}
