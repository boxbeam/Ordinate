import java.net.URI
import org.gradle.api.JavaVersion

plugins {
    id("ordinate.platform-conventions")
}
repositories {
    maven { url = URI("https://libraries.minecraft.net") }
}

java {
    targetCompatibility = JavaVersion.VERSION_16
}

dependencies {
    compileOnly("com.mojang:brigadier:1.0.18")
}