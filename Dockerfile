FROM eclipse-temurin:17-jdk-alpine

COPY ./target/dataviz-0.0.1-SNAPSHOT.jar app.jar
COPY ./.keystore .keystore

ENTRYPOINT ["java", "-jar", "/app.jar"]
