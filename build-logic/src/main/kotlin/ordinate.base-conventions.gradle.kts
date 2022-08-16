plugins {
    id("ordinate.common-conventions")
}

dependencies {
    implementation("com.github.Redempt:RedLex:1.3.4")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    setWorkingDir("test")
}
