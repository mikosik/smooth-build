rootProject.name = "smooth-build"

include("src:testing")
include("src:common")
include("src:antlr-smooth")
include("src:compiler-frontend")
include("src:virtual-machine")
include("src:compiler-backend")
include("src:evaluator")
include("src:antlr-report-matcher")
include("src:cli")
include("src:standard-library")
include("src:distribution")
include("src:system-test")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
