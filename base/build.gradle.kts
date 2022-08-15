plugins {
    id("ordinate.base-conventions")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "redempt.ordinate"

publishing {
    publications {
        create<MavenPublication>("mavenPublish") {
            groupId = "com.github.Redempt"
            artifactId = rootProject.name
            version = System.getenv("BUILD_VERSION") ?: "1.0"
        }
    }
}