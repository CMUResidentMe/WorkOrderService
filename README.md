# WorkOrder Service

## Overview
This README provides instructions on how to set up and run the WorkOrderService using Maven and Docker. The service is packaged into a Docker container, which can be rebuilt and rerun using the provided script.

## Prerequisites
Before you begin, ensure you have the following installed on your machine:
- **Java**: Needed to run Maven builds.
- **Maven**: Required for building the application. You can download and install it from [Maven's official website](https://maven.apache.org/download.cgi).
- **Docker**: Needed for creating and managing your Docker container. Download and install Docker from [Docker's official website](https://www.docker.com/get-started).

## Getting Started

### Step 1: Clone the Repository
To get started, clone this repository to your local machine using the following command:

```
git clone [repository-url]
cd CommunicationBoardService
```
Replace `[repository-url]` with the actual URL of the repository.

### Step 2: Update Database Configuration
Before building the project, update the database URI in the `application.yml` file:

```
spring.data.mongodb.uri=your_own_db_uri
```
Replace `your_own_db_uri` with your MongoDB URI to ensure the application connects to your database correctly.

### Step 3: Build the Project
Once the database URI is configured, build the project with Maven to ensure all dependencies are downloaded and the project compiles successfully:

```
mvn clean install
```

### Step 4: Make the Script Executable
Before running the script, you need to make sure it is executable. Run the following command in your terminal:

```
chmod +x ./rebuild_and_run.sh
```

### Step 5: Run the Script
Once the script is executable, you can run it to rebuild and start the Docker container:

```
./rebuild_and_run.sh
```

## What Does the Script Do?
The `rebuild_and_run.sh` script performs the following actions:
1. Builds a new Docker image from the Dockerfile.
2. Stops any previously running containers of this service.
3. Starts a new container using the newly built image.
