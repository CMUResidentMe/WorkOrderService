#!/bin/bash

# Navigate to the directory containing your docker-compose.yml file
cd "$(dirname "$0")"

# Check if docker-compose is installed
if ! [ -x "$(command -v docker-compose)" ]; then
  echo 'Error: docker-compose is not installed.' >&2
  exit 1
fi

# Stop all running containers based on the docker-compose file
echo "Stopping all running containers..."
docker-compose down

# Cleanup dangling images
echo "Cleaning up dangling Docker images..."
docker image prune -f

# Run the containers using docker-compose
echo "Starting containers..."
docker-compose up -d

echo "Containers are up and running."
