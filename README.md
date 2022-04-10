# EventDispatcher

Небольшая библиотека, для распространения event'ов внутри приложения

Можно подключить следующими спосабами

### Gradle

```groovy
repositories {
    maven {
        url = "https://nexus.spliterash.ru/repository/group/"
    }
}

dependencies {
    implementation 'ru.spliterash:event-dispatcher:1.0.0'
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>spliterash-group</id>
        <url>https://nexus.spliterash.ru/repository/group/</url>
    </repository>
</repositories>


<dependencies>
    <dependency>
        <groupId>ru.spliterash</groupId>
        <artifactId>event-dispatcher</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```