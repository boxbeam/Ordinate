plugins {
    id("ordinate.common-conventions")
}

dependencies {
    implementation(project(":Ordinate-base"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}