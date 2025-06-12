#!/usr/bin/env bash
# Render Start Script for Personal Finance Management API

set -o errexit  # exit on error

echo "Starting Personal Finance Management API..."

# Find the JAR file
JAR_FILE=$(find build/libs -name "*.jar" | head -1)

if [ ! -f "$JAR_FILE" ]; then
    echo "ERROR: JAR file not found in build/libs/"
    exit 1
fi

echo "Found JAR file: $JAR_FILE"
echo "Starting application with production profile..."

# Start the application with production profile
java -Dspring.profiles.active=prod \
     -Dserver.port=${PORT:-8080} \
     -Xmx512m \
     -XX:+UseStringDeduplication \
     -XX:+UseG1GC \
     -jar "$JAR_FILE" 