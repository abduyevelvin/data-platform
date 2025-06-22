# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package \
    -DskipTests \
    -Ddata.platform.drive.path="${DATA_PLATFORM_DRIVE_PATH}" \
    -Ddata.platform.file.max.size="${DATA_PLATFORM_FILE_MAX_SIZE}" \
    --batch-mode

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/drive-0.0.1-SNAPSHOT.jar drive-service.jar

# Environment variables for configuration
ENV DATA_PLATFORM_DRIVE_PATH="/data/drive"
ENV DATA_PLATFORM_FILE_MAX_SIZE=10485760

# Pass env vars to Spring Boot as properties
ENV SPRING_APPLICATION_JSON='{"data.platform.drive.path":"${DATA_PLATFORM_DRIVE_PATH}","data.platform.file.max.size":"${DATA_PLATFORM_FILE_MAX_SIZE}"}'

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "drive-service.jar"]