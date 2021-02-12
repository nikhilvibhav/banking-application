# banking-application ![Java CI with Gradle](https://github.com/nikhilvibhav/banking-application/workflows/Java%20CI%20with%20Gradle/badge.svg)

This application is a multi-module Gradle project containing two services:

- account-service, and
- transaction-service

It also contains a basic client located under `banking-client` directory under the project's root directory. Read more
about it [here](banking-client/README.md).

### CI/CD

This application implements CI using Github Actions. Every push or the merge of a PR to `main` branch triggers the
build, which when completed successfully triggers gradle build that builds the project and runs the tests. If the build
and tests successful, then the workflow generates and pushes the docker images
to [DockerHub](https://hub.docker.com/u/nikhilvibhav).

###### Note:

> The CI/CD pipeline does not deploy the images to a cloud instance, mainly because there were some problems billing

### account-service

This exposes two REST endpoints for:

- Creating a CURRENT account - `POST http://localhost:8080/api/bank/v1/account/current`
- Getting a customer by the customer id - `GET http://localhost:8080/api/bank/v1/customer/{id}`

The OpenAPI API docs (JSON) can be found at - (http://localhost:8080/v3/api-docs) \
The OpenAPI spec can (Swagger UI) be viewed at - (http://localhost:8080/swagger-ui.html)

### transaction-service

This service exposes three REST endpoints for:

- Getting all transactions by an accountId - `GET http://localhost:8081/api/bank/v1/transaction?accountId={accountId}`
- Creating a transaction - `PUT http://localhost:8081/api/bank/v1/transaction`
- Deleting a transaction by its transaction id - `DELETE http://localhost:8081/api/bank/v1/transaction/{id}`

The OpenAPI API docs (JSON) can be found at - (http://localhost:8081/v3/api-docs) \
The OpenAPI spec can (Swagger UI) be viewed at - (http://localhost:8081/swagger-ui.html)

---

When creating a CURRENT account, the account-service will internally call transaction-service to create a corresponding
transaction. If due to unforeseen circumstances the REST call fails, then account-service will automatically delete the
account that was created.
---

## Build Steps

You do not need to run a build to test the application, but in case you want to you can build using gradle. \
Run - `./gradlew clean build` or `gradlew.bat clean build` from the root project directory.

### Build Docker Image

You can build and publish the image to your dockerhub repository using

- `./gradlew bootBuildImage` - This will generate 2 docker images for account-service and transaction-service and will
  publish the image to dockerhub,
  refer <https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#build-image>

#### NOTE:

> You would need to set environment variables `dockerUsername`, `dockerPassword`, `dockerUrl` and `dockerEmail` containing the necessary information to be able to publish to your docker repository

---

## Testing the application

Prerequisites:

- Docker and Docker Compose
- Postman/httpie/curl

#### NOTE:

> The application loads some Customers at the startup into the database. You can view at details in this [file](account-service/src/main/resources/data.sql).

Once you have cloned this repository locally, run the following command to pull the docker images and start the
containers:

```
❯ docker-compose up -d
Creating network "banking-application_banking-network" with the default driver
Creating transaction-service ... done
Creating account-service     ... done
```

You can verify that the containers are up by running the command:

```
❯ docker-compose ps
       Name               Command        State           Ports
-----------------------------------------------------------------------
account-service       /cnb/process/web   Up      0.0.0.0:8080->8080/tcp
transaction-service   /cnb/process/web   Up      0.0.0.0:8081->8081/tcp
```

Now you can run the httpie/curl commands to create a current account, or to get a customer.

1. Create Current Account
    ```
    ❯ http POST :8080/api/bank/v1/account/current customerId=1 initialCredit=20
    HTTP/1.1 201
    Connection: keep-alive
    Content-Type: application/json
    Date: Wed, 10 Feb 2021 06:52:53 GMT
    Keep-Alive: timeout=60
    Transfer-Encoding: chunked
    
    {
        "balance": 20.0,
        "customerId": 1,
        "dateCreated": "2021-02-10T06:52:53.332912Z",
        "dateUpdated": "2021-02-10T06:52:53.36911Z",
        "id": 1,
        "type": "CURRENT"
    }
    ```

2. Get Customer by ID
    ```
    ❯ http GET :8080/api/bank/v1/customer/1
    HTTP/1.1 200
    Connection: keep-alive
    Content-Type: application/json
    Date: Wed, 10 Feb 2021 06:55:25 GMT
    Keep-Alive: timeout=60
    Transfer-Encoding: chunked
    
    {
        "accounts": [
            {
                "balance": 20.0,
                "customerId": 1,
                "dateCreated": "2021-02-10T06:52:53.332912Z",
                "dateUpdated": "2021-02-10T06:52:53.36911Z",
                "id": 1,
                "transactions": [
                    {
                        "accountId": 1,
                        "amount": 20.0,
                        "dateTransacted": "2021-02-10T06:52:53.489847Z",
                        "id": 1,
                        "type": "CREDIT"
                    }
                ],
                "type": "CURRENT"
            }
        ],
        "dateCreated": "2021-02-10T04:44:58.825828Z",
        "email": "johndoe@example.com",
        "firstName": "John",
        "id": 1,
        "surname": "Doe"
    }
    ```

Once the testing is complete, you can shut down the containers by running:

```
❯ docker-compose down
Stopping account-service     ... done
Stopping transaction-service ... done
Removing account-service     ... done
Removing transaction-service ... done
Removing network banking-application_banking-network
```

For more requests, see
the [Banking Application Postman Collection](postman/Banking%20Application.postman_collection.json).

#### NOTE:

> Although you can create transactions directly, creating those will not update the balance. This can be fixed by adding a new endpoint to perform a transaction which will update the balance in the database and call transaction service to create a new transaction

---

## Improvements:

1. Both the services and the React client are in a single repository, they can be moved into separate git repositories
   of their own
2. Better fault tolerance using something like Hystrix (not supported in newer Spring Boot versions) or Resilience4J
3. API Security using JWT tokens or something similar
4. Service Registry like Eureka (does not work with the latest Spring Boot versions)
5. Better unit testing
