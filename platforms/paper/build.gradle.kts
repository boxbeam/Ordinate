import java.net.URI

plugins {
    id("ordinate.platform-conventions")
}
repositories {
    maven { url = URI("https://libraries.minecraft.net") }
    maven { url = URI("https://repo.papermc.io/repository/maven-public/") }
    mavenCentral()
}

java {
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":Ordinate-brigadier"))
    implementation(project(":Ordinate-spigot"))
    compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-mojangapi:1.19.2-R0.1-SNAPSHOT")
}

