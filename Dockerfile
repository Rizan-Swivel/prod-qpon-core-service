FROM openjdk:latest
COPY /target/core-server-1.0.1-SNAPSHOT.jar /home/core-server-1.0.1-SNAPSHOT.jar
COPY qpon-google-secret.json /usr/local/bin/qpon-google-secret.json
WORKDIR /home
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "core-server-1.0.1-SNAPSHOT.jar"]
