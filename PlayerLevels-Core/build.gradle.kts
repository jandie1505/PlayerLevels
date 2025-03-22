plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
}

dependencies {
    implementation(project(":PlayerLevels-API"))
    implementation("net.chaossquad:mclib:master-fe32adc2d67aa46d5f1a3897f9d3f0a9ae0d7898")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("org.json:json:20250107")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.4.1")
    implementation("net.objecthunter:exp4j:0.4.8")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}

tasks.shadowJar {
    from(project(":PlayerLevels-API").tasks.jar)
    relocate("net.chaossquad.mclib", "net.jandie1505.playerlevels.dependencies.net.chaossquad.mclib")
    relocate("com.zaxxer.hikari", "net.jandie1505.playerlevels.dependencies.com.zaxxer.hikari")
    relocate("org.json", "net.jandie1505.playerlevels.dependencies.org.json")
    relocate("net.objecthunter.exp4j", "net.jandie1505.playerlevels.dependencies.net.objecthunter.exp4j")
}

tasks.build {
    dependsOn("shadowJar")
}