plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
}

dependencies {
    implementation(project(":api"))
    implementation("net.chaossquad:mclib:coreexecutable-managedlistener-managedentity-4538aad9567f5a34cbee51ef65d6a57d22b781e3")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("org.json:json:20250107")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}

tasks.shadowJar {
    from(project(":api").tasks.jar)
    relocate("net.chaossquad.mclib", "net.jandie1505.playerlevels.dependencies.net.chaossquad.mclib")
    relocate("com.zaxxer.hikari", "net.jandie1505.playerlevels.dependencies.com.zaxxer.hikari")
    relocate("org.json", "net.jandie1505.playerlevels.dependencies.org.json")
}