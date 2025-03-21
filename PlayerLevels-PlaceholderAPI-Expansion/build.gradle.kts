plugins {
    id("java")
}

dependencies {
    compileOnly(project(":PlayerLevels-API"))
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
}
