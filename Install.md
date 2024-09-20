## Using gradle

```gradle
    repositories {
        ... // previous repositories
        maven("https://maven.pkg.github.com/alec016")
    }
    dependencies {
        ... // previous dependencies
        implementation("database:Database:<version>")
    }
```

---

## Using maven

```maven
    <repositories>
        <repository>
            https://maven.pkg.github.com/alec016
        </repository>
    </repositories>
    <dependencies>
        <!--previus dependencies-->
        <dependency>
            <groupId>database</groupId>
            <artifactId>Database</artifactId>
            <version>{version}</version>
        </dependency>
    </dependencies>
```
