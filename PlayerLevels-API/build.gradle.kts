plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
}

dependencies {
    implementation("net.chaossquad:mclib:master-d7d92b1765d275ae2aa8669a7e1b36fd809de861")
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
