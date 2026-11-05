# Build stage (using Java 21)
FROM maven:3-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Run stage (using Java 21)
FROM eclipse-temurin:21-alpine
COPY --from=build /target/inventory-management-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
