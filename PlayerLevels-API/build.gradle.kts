plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
}

dependencies {
    implementation("net.chaossquad:mclib:coreexecutable-managedlistener-managedentity-4538aad9567f5a34cbee51ef65d6a57d22b781e3")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.shadowJar {
    relocate("net.chaossquad.mclib", "net.jandie1505.playerlevels.dependencies.mclib")
}
