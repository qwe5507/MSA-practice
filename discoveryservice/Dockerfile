FROM openjdk:17-ea-slim
VOLUME /tmp
COPY target/discoveryservice-1.0.jar Discoveryservice.jar
ENTRYPOINT ["java", "-jar", "Discoveryservice.jar"]