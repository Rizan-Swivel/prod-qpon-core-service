## QPon Core

This is the core microservice for the QPon backend.

## Requirements

* Spring boot 2.2.1
* Open JDK 11
* MySQL 5.7
* Maven 3.6 (only to run maven commands)

## Dependencies

All dependencies are available in pom.xml.

## Configuration

Configure the relevant configurations in application.yml and bootstrap.yml in src/main/resources before building the
application

## Build

```
mvn clean compile package
```

## Run

```
mvn spring-boot:run
```

or

```
java -jar target/auth-server-1.0.0-SNAPSHOT.jar
```

## Test

```
mvn test
```

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.1.RELEASE/maven-plugin/)

## License

Copyright (c) Swivel - This source code is licensed under the license. 
