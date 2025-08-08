plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    implementation(project(":PlayerLevels-API"))
    implementation("net.chaossquad:mclib:main-e95d70f19fde050fe80a799f4f289c032bc3d07c")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("org.json:json:20250107")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.4.1")
    implementation("org.postgresql:postgresql:42.7.7")
    implementation("net.objecthunter:exp4j:0.4.8")
    compileOnly("eu.cloudnetservice.cloudnet:wrapper-jvm-api:4.0.0-RC13")
    compileOnly("eu.cloudnetservice.cloudnet:platform-inject-api:4.0.0-RC13")
}

tasks.jar {
    archiveClassifier.set("original")
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
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