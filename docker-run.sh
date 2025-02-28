#!/bin/bash

# Stop any running containers
echo "Stopping any running containers..."
docker-compose down

# Build and start the containers
echo "Building and starting containers..."
docker-compose up --build -d

# Wait for the application to start
echo "Waiting for the application to start..."
sleep 10

# Show the logs
echo "Application is running. Showing logs..."
docker-compose logs -f app 