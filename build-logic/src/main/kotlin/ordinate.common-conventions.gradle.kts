import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.`java-library`
import java.net.URI

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow")
}

group = "redempt.ordinate"

repositories {
    mavenCentral()
    maven {
        url = URI("https://redempt.dev/")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
}

sourceSets {
    main {
        java {
            srcDir("src")
        }
        resources {
            srcDir("res")
        }
    }
    test {
        java {
            srcDir("test/src")
        }
        resources {
            srcDir("test/res")
        }
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    withType<ShadowJar> {
        archiveClassifier.set("")
    }
    withType<Jar> {
        archiveBaseName.set(project.name)
    }
    register<Jar>("javadocJar") {
        from(tasks["javadoc"])
    }
    register("buildDocs") {
        dependsOn(tasks["build"])
        dependsOn(tasks["javadocJar"])
    }
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            groupId = "com.github.Redempt"
            artifactId = project.name
            version = System.getenv("BUILD_VERSION") ?: "1.0"
            from(components["java"])
        }
    }
}