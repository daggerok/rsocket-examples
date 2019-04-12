# rsocket-examples
Java RSockert client

## maven

_fat jar_

```bash
./mvnw
java -jar ./rsocket-java-example/target/rsocket-java-examples-1.0-SNAPSHOT-all.jar
```

## gradle

_fat jar_

```bash
./gradlew
java -jar ./rsocket-java-example/build/libs/rsocket-java-example-1.0-SNAPSHOT-all.jar
```

_installDist_

```bash
./gradlew installDist
bash ./rsocket-java-example/build/install/rsocket-java-example/bin/rsocket-java-example
```

NOTE: _This project has been based on [GitHub: daggerok/main-starter](https://github.com/daggerok/main-starter)_

resources:

- see [GitHub: rsocket/rsocket-java](https://github.com/rsocket/rsocket-java)
- read [Weld SE](https://docs.jboss.org/weld/reference/3.1.0.Final/en-US/html_single/#weld-se)
