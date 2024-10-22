FROM openjdk:17-jdk-slim
VOLUME /tmp
RUN apt-get update && apt-get install -y curl
COPY ./build/libs/*.jar eureka-server.jar
ENTRYPOINT ["java", "-jar", "/eureka-server.jar"]