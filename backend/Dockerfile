# Use a base image that has Java installed
FROM openjdk:17 as build

# Set the working directory
WORKDIR /app

# Copy the Maven wrapper and pom.xml files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (optional: can speed up builds by caching dependencies)
RUN ./mvnw dependency:go-offline

# Copy the rest of the application code
COPY src ./src

# Build the application
RUN ./mvnw package -DskipTests

# Use a smaller image for the final build
FROM openjdk:17

# Set the working directory
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
