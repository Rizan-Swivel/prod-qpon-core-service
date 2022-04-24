FROM openjdk:11-jre-slim
COPY /target/qpon-core-0.0.1-SNAPSHOT.jar /home/qpon-core-0.0.1-SNAPSHOT.jar
RUN mkdir -p /usr/local/bin
COPY qpon-google-secret.json /usr/local/bin/qpon-google-secret.json
ENV GOOGLE_APPLICATION_CREDENTIAL /usr/local/bin/qpon-google-secret.json
ARG GOOGLE_APPLICATION_CREDENTIAL /usr/local/bin/qpon-google-secret.json
ENV DB_USERNAME qponadmin
ENV DB_PASSWORD QPonpD42cR8823
WORKDIR /home
EXPOSE 8084
CMD ["java", "-jar", "qpon-core-0.0.1-SNAPSHOT.jar"]
