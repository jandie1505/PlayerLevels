plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
    `maven-publish`
}

dependencies {
    implementation("net.chaossquad:mclib:master-3fdcf972520b59608457e3fe51f5224ec6d2045d")
    implementation("net.objecthunter:exp4j:0.4.8")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("org.black_ixx:playerpoints:3.3.0")
}

java {
    withSourcesJar()
    withJavadocJar()
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

// gradle publish{PUBLICATION_NAME}To{REPOSITORY_NAME}Repository
// in this case: publishMavenToChaosSquadRepository
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "chaossquad"
            url = uri(if (version.toString().endsWith("RELEASE")) {
                "https://maven.chaossquad.net/releases"
            } else {
                "https://maven.chaossquad.net/snapshots"
            })

            credentials {
                username = findProperty("chaossquad-repository.username") as String?
                password = findProperty("chaossquad-repository.password") as String?
            }
        }
    }
}