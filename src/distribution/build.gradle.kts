plugins {
    base
}

val zip by tasks.register<Zip>("zip") {
    archiveBaseName.set("smooth")
    dependsOn(project(":src:cli").tasks.named("fatJar"))
    dependsOn(project(":src:standard-library").tasks.named("zip"))

    from(project(":src:cli").tasks.named("fatJar")) {
        into("bin")
    }
    from(layout.projectDirectory.dir("src/main/shell")) {
        into("bin")
        filePermissions {
            unix("755")   // applies to all files from this copySpec
        }
    }
    from(project(":src:standard-library").tasks.named("zip").map { task -> task.outputs.files.map { zipTree(it) } }) {
        into("lib")
    }
    into("smooth")
}

tasks.assemble { dependsOn(zip) }
