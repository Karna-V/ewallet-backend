# Use Maven image to build the application
FROM maven:3.8.3-openjdk-17 AS build
LABEL maintainer="demo"

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and project files to the container
COPY pom.xml .
COPY src ./src

# Build the application using Maven
RUN mvn clean install -DskipTests

# Use a slim image for the final output to keep it lightweight
FROM openjdk:17-jdk-slim
LABEL maintainer="demo"

# Copy the jar file from the build stage to the final image

COPY --from=build /app/target/ewallet-0.0.1-SNAPSHOT.jar /springboot-docker-ewallet.jar

# Set the entrypoint for the container
ENTRYPOINT ["java", "-XX:+UseZGC", "-jar", "/springboot-docker-ewallet.jar"]
