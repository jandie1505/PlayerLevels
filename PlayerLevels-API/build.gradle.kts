plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
}

dependencies {
    implementation("net.chaossquad:mclib:master-3fdcf972520b59608457e3fe51f5224ec6d2045d")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.shadowJar {
    relocate("net.chaossquad.mclib", "net.jandie1505.playerlevels.dependencies.mclib")
}
