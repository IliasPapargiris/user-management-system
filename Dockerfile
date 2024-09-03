# Use the official Maven image to build the app
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Use the official OpenJDK image as the base image for running the app
FROM openjdk:21-jdk-slim
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/user-management-system-0.0.1-SNAPSHOT.jar app.jar

# Specify the command to run the app
ENTRYPOINT ["java", "-jar", "app.jar"]

# Expose the application port
EXPOSE 8080