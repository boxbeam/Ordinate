import com.hierynomus.gradle.license.tasks.LicenseCheck
import org.gradle.kotlin.dsl.`java-library`

plugins {
    `java-library`
    id("com.github.hierynomus.license")
    `maven-publish`
}
repositories {
    mavenCentral()
}


tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = "1.9"
        targetCompatibility = "1.8"
    }
    withType<LicenseCheck> {
        this.header = rootProject.file("LICENSE")
        encoding = "UTF-8"
        mapping("java", "JAVADOC_STYLE")

        include("**/*.java")
    }
}