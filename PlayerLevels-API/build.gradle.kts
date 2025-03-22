plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
}

dependencies {
    implementation("net.chaossquad:mclib:master-fe32adc2d67aa46d5f1a3897f9d3f0a9ae0d7898")
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
