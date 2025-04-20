allprojects {
    group = "net.jandie1505"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            name = "papermc"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        maven {
            name = "chaossquad-releases"
            url = uri("https://maven.chaossquad.net/releases")
        }
        maven {
            name = "chaossquad-snapshots"
            url = uri("https://maven.chaossquad.net/snapshots")
        }
        maven {
            name = "placeholder-api"
            url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        }
        maven {
            name = "rosewood-repo"
            url = uri("https://repo.rosewooddev.io/repository/public/")
        }
    }
}

subprojects {}
