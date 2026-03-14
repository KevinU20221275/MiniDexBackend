# ---- Build state ----
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /build

# copy pom file for cache dependencies
COPY pom.xml .

RUN mvn -B dependency:go-offline

# copy project
COPY src ./src

# Build jar
RUN mvn clean package -DskipTests

# --- Runtime stage ----
FROM eclipse-temurin:17-jdk

WORKDIR /app

# copi jar
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]