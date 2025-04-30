plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly(project(":PlayerLevels-API"))
    //compileOnly("eu.cloudnetservice.cloudnet:bridge:4.0.0-RC11.2")
    compileOnly("eu.cloudnetservice.cloudnet:wrapper-jvm:4.0.0-RC10")
    compileOnly("eu.cloudnetservice.cloudnet:platform-inject-api:4.0.0-RC10")
}

tasks.jar {
    archiveClassifier.set("original")
}

tasks.shadowJar {

    // Set classifier
    archiveClassifier.set("")

    // Relocate everything
    isEnableRelocation = true
    relocationPrefix = "net.jandie1505.playerlevels.libs"

    // Exclude own plugin to prevent the api from getting relocated
    relocate("net.jandie1505.playerlevels", "net.jandie1505.playerlevels")

    // SLF4J special case: keep the references in the class files not-relocated but do not add them because paper already has them
    exclude("org/slf4j/**")
    relocate("org.slf4j", "org.slf4j")
}

tasks.build {
    dependsOn("shadowJar")
}