#!/bin/bash

# Set environment variables
IMAGE_NAME="work-order-service"
CONTAINER_NAME="work-order-service"
PORT_MAPPING="8001:8001"

# Build Docker image
echo "Building Docker image..."
docker build -t $IMAGE_NAME .

# Check if the container is already running, stop it
if [ $(docker ps -q -f name=$CONTAINER_NAME) ]; then
    echo "Stopping existing container..."
    docker stop $CONTAINER_NAME
fi

# Check if the container exists, remove it
if [ $(docker ps -aq -f status=exited -f name=$CONTAINER_NAME) ]; then
    echo "Removing existing container..."
    docker rm $CONTAINER_NAME
fi

# Run the container from the image
echo "Running new container..."
docker run -d --name $CONTAINER_NAME -p $PORT_MAPPING $IMAGE_NAME

echo "Container $CONTAINER_NAME is running on port $PORT_MAPPING"
