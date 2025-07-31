# Patient Management System

This repository contains a collection of Spring Boot microservices along with an AWS CDK stack for running them locally using [LocalStack](https://github.com/localstack/localstack).

## Services

| Service | Description | Default Port |
|---------|-------------|--------------|
| **auth-service** | Handles authentication and token validation. | `4005` |
| **patient-service** | Manages patient records and produces Kafka events. | `4000` |
| **billing-service** | Provides gRPC endpoints for billing. | `4001` (HTTP), `9001` (gRPC) |
| **analytics-service** | Consumes patient events from Kafka. | `4002` |
| **api-gateway** | Spring Cloud Gateway front‑end routing to other services. | `4004` |
| **infrastructure** | AWS CDK project that deploys the services to LocalStack. | N/A |

A sample architectural diagram is included below.

![Architecture Diagram](architecture%20diagram.png)

## Prerequisites

- **JDK 21** – required by the Maven projects.
- **Docker** – to build images and run LocalStack.
- **AWS CLI** – used by the deployment script.
- **LocalStack** – simulates AWS services locally.

Ensure that LocalStack is running before deploying the stack.

## Building the Services

Each microservice contains a Maven wrapper. To build a service run:

```bash
cd <service-name>
./mvnw clean package
```

Docker images can then be built using the provided `Dockerfile` inside each service directory:

```bash
docker build -t <service-name> .
```

## Deploying to LocalStack

1. Start LocalStack on your machine.
2. Build the infrastructure project and synthesize the CDK templates:

```bash
cd infrastructure
./mvnw package
```

3. Deploy the stack using the helper script (requires the AWS CLI):

```bash
./localstack-deploy.sh
```

The script deploys a CloudFormation stack to LocalStack and prints the load balancer DNS name used to access the services.

Sample HTTP and gRPC requests are located under `api-requests/` and `grpc-requests/` for convenience.

## Running Individual Services Locally

You can also run each service directly with Spring Boot:

```bash
cd auth-service
./mvnw spring-boot:run
```

Repeat for other services (patient-service, billing-service, analytics-service, api-gateway). Configuration properties for each service are found under `src/main/resources`:

- `auth-service/src/main/resources/application.properties`
- `patient-service/src/main/resources/application.properties`
- `billing-service/src/main/resources/application.properties`
- `analytics-service/src/main/resources/application.properties`
- `api-gateway/src/main/resources/application.yml`

Example of the gateway configuration:

```yaml
server:
  port: 4004
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service-routes
          uri: http://auth-service:4005
          predicates:
            - Path=/auth/**
          filters:
            - StripPrefix=1
```

## Integration Tests

Integration tests reside in the `integration-tests` module. After the stack is running, execute:

```bash
cd integration-tests
./mvnw test
```

## Useful Scripts

The `infrastructure/localstack-deploy.sh` script deploys the CDK stack:

```bash
aws --endpoint-url=http://localhost:4566 cloudformation deploy \
    --stack-name patient-management \
    --template-file "./cdk.out/localstack.template.json"
```

It then outputs the DNS of the load balancer created by the stack.

## License

This project is provided for educational purposes without any warranty.
