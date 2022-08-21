dependencyResolutionManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
    }

}

rootProject.name = "Ordinate"

listOf(
    "base",
).forEach(::includeProject)

listOf(
    "spigot",
    "sponge",
).forEach {
    includeProject(it, "platforms")
}

fun includeProject(name: String, folder: String? = null) {
    include(name) {
        this.name = "${rootProject.name}-$name"
        if (folder != null) {
            this.projectDir = file("$folder/$name")
        }
    }
}

fun include(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}