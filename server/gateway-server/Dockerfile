FROM openjdk:17-jdk-slim
VOLUME /tmp
RUN apt-get update && apt-get install -y curl
COPY ./build/libs/*.jar gateway-server.jar
ENTRYPOINT ["java", "-jar", "/gateway-server.jar"]