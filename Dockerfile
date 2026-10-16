# ==========================================
# STAGE 1: BUILD
# ==========================================
FROM maven:3.9.11-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy Maven files first for better Docker caching
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build Spring Boot application
RUN mvn clean package -DskipTests


# ==========================================
# STAGE 2: RUN
# ==========================================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy generated JAR from builder
COPY --from=builder /app/target/*.jar app.jar

# Render provides PORT environment variable
EXPOSE 8080

# Start Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
