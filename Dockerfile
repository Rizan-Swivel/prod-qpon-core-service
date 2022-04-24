FROM openjdk:latest
COPY /target/qpon-core-0.0.1-SNAPSHOT.jar /home/qpon-core-0.0.1-SNAPSHOT.jar
COPY qpon-google-secret.json /usr/local/bin/qpon-google-secret.json
WORKDIR /home
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "qpon-core-0.0.1-SNAPSHOT.jar"]
