#!bin/bash

if [ -d "./microservices-java/auth-service/build/libs/*.jar" ]; then
    echo "Deleting jar files..."
    rm -f "./microservices-java/auth-service/build/libs/*.jar"

microservices-java/gradlew build