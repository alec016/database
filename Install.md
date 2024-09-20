## Using gradle

```gradle
    repositories {
        ... // previous repositories
        maven("https://maven.pkg.github.com/alec016/database")
    }
    dependencies {
        ... // previous dependencies
        implementation("es.degrassi:database:<version>")
    }
```

---

## Using maven

```maven
    <repositories>
        <repository>
            https://maven.pkg.github.com/alec016/database
        </repository>
    </repositories>
    <dependencies>
        <!--previus dependencies-->
        <dependency>
            <groupId>es.degrassi</groupId>
            <artifactId>database</artifactId>
            <version>{version}</version>
        </dependency>
    </dependencies>
```
