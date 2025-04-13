#!/bin/bash

echo "🧪 Compilando backend..."
cd humami-backend
mvn clean package -DskipTests

JAR_NAME=$(ls target/*.jar | grep -v "original" | head -n 1)

if [ -z "$JAR_NAME" ]; then
  echo "❌ No se encontró el JAR compilado en target/"
  exit 1
fi

cp "$JAR_NAME" ./humami-backend.jar 
cd ..

echo "🚀 Levantando contenedores con Docker Compose..."
docker-compose up --build --force-recreate