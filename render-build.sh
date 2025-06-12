#!/usr/bin/env bash
# Render Build Script for Personal Finance Management API

set -o errexit  # exit on error

echo "Starting build process for Personal Finance Management API..."

# Set Java version
echo "Java version:"
java -version

# Clean and build the application
echo "Building the application..."
./gradlew clean build -x test

echo "Build completed successfully!"
echo "JAR file location: build/libs/"
ls -la build/libs/

echo "Ready for deployment!" 