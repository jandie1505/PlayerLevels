plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
    `maven-publish`
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