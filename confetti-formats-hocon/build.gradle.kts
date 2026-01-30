plugins {
    `java-library`
}

dependencies {
    api(project(":confetti-core"))
    implementation("com.typesafe:config:1.4.5")

    // Tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
