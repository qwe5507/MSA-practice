FROM openjdk:17-ea-slim
VOLUME /tmp
COPY target/apigateway-service-1.0.jar ApiGatewayService.jar
ENTRYPOINT ["java", "-jar", "ApiGatewayService.jar"]