# API: Getting started

## Add dependency
To use the PlayerLevels API, you need to import it into your project.  
You can do this with gradle or maven.

Gradle:
```kotlin
repositories {
    // [...]
    maven {
        name = "chaossquad-releases"
        url = uri("https://maven.chaossquad.net/releases")
    }
}

dependencies {
    // [...]
    compileOnly("net.jandie1505:PlayerLevels-API:VERSION")
}
```

Maven:
```xml
    <repositories>
        <repository>
            <id>chaossquad-releases</id>
            <url>https://maven.chaossquad.net/releases</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>net.jandie1505</groupId>
            <artifactId>PlayerLevels-API</artifactId>
            <version>VERSION</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
```

## Obtaining the API object

```java
public void onEnable() {
    PlayerLevelsAPI api = PlayerLevelsAPIProvider.getApi();
}
```