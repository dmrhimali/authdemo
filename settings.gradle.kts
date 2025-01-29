pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/snapshot") }
        gradlePluginPortal()
    }
}
rootProject.name = "authdemo"

include("basicAuthDemo")
include("noAuthDemo")
include("jwtAuthDemo")
