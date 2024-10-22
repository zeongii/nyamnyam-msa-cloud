FROM openjdk:17-jdk-slim
VOLUME /tmp
RUN apt-get update && apt-get install -y curl
COPY ./build/libs/*.jar nyamnyam/eureka.jar
ENTRYPOINT ["java", "-jar", "nyamnyam/eureka.jar"]