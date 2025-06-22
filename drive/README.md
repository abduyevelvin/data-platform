# Drive Service

## Overview

Drive Service is a Java 21 application built with Maven, designed to manage and interact with a data platform drive. The service is containerized using Docker and supports deployment to Kubernetes using Helm charts. CI/CD is managed via Azure Pipelines.

## Prerequisites

- Java 21
- Maven
- Docker
- Docker Compose (for local development)
- Azure DevOps (for CI/CD)
- Kubernetes cluster (for production deployment)
- Helm

## API Documentation

Swagger UI is integrated for easy API exploration and testing.  
After starting the service, access the documentation at:  
http://localhost:8080/api/v1/swagger-ui/index.html#/

## Local Development

You can run the service locally using Docker Compose. The configuration is provided in the `docker-compose.yaml` file.

**To build and start the service:**
```sh
docker-compose up --build
```

This will:
- Build the Docker image using the provided `Dockerfile`
- Start the service on port 8080
- Mount a local directory to `/data/drive` inside the container

## Local Volume Requirement

The `docker-compose.yaml` mounts a local directory for persistent storage:
```yaml
volumes:
  - "C:/Users/Elvin.Abduyev/Downloads/test driver:/data/drive"
```
**Note:** Update the host path (`C:/Users/Elvin.Abduyev/Downloads/test driver`) to a valid directory on your machine before running locally.

## Dockerfile

The `Dockerfile` defines a multi-stage build:
- The first stage builds the application using Maven.
- The second stage creates a lightweight image with the built JAR and required environment variables.

## CI/CD with Azure Pipelines

- **CI Pipeline (`azure-pipelines-ci.yaml`):**  
  Runs on every push to `develop`, `bugfix/*`, or `task/*` branches. It builds, tests, and pushes the Docker image to Docker Hub.

- **CD Pipeline (`azure-pipelines-cd.yaml`):**  
  Triggered after a successful CI run and a merge to `develop`. It deploys the latest image to Kubernetes using Helm.

## Helm Chart

The `helm` directory contains:
- `Chart.yaml`: Helm chart metadata
- `values.yaml`: Default configuration values
- `templates/`: Kubernetes resource templates

**To deploy manually with Helm:**
```sh
helm upgrade --install drive-service helm/drive-service \
  --values helm/drive-service/values.yaml \
  --set image.repository=<your-dockerhub-username>/drive-service \
  --set image.tag=<image-tag>
```

## Deployment Flow

1. **Code Push:**  
   Push changes to a tracked branch. CI pipeline builds, tests, and pushes the Docker image.

2. **Merge to Develop:**  
   After merging to `develop`, the CD pipeline is triggered, deploying the new image to Kubernetes using Helm.

3. **Kubernetes Deployment:**  
   The Helm chart manages the deployment, configuration, and updates of the service in your cluster.

## Health and Liveness Probes

Kubernetes deployment includes health and liveness checks, configured in the Helm chart templates.  
These ensure the service is running and ready to receive traffic.

---

Update `<your-dockerhub-username>` and `<image-tag>` as needed.

---