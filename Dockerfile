# ---- Build Stage ----
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle clean build -x test --no-daemon

# ---- Run Stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar

# Expose the port Render will use
EXPOSE 8080

# Use PORT env variable if set (Render sets $PORT)
ENV PORT=8080

# Start the app with production profile
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-Dserver.port=${PORT}","-jar","/app/app.jar"] 