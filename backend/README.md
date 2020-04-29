# Spectacular Backend Service
This component acts as a [Backend Service](../docs/design/architecture.md#backend-service) that supports the UI for Spectacular and is the "brains" of the tool. It encapsulates almost all the logic behind most of Spectacular's features and functionality.

## Architecture
The objects in this project could be organised horizontally using the following conceptual layers:
- A RESTful API layer to expose functionality publicly
- A service layer containing all the logic required to fulfil each API request
- Domain model layer representing the tools [Domain Model](../docs/design/domain-modal.md)
- DTOs used for different API resources
- Client layer for making API requests to other resources like GitHub's API

However the project is organised vertically by the domain concerns instead, for example:
- Catalogues
- Files
- PullRequests
- Specs
- Security

### Design Decisions
This project has been built with the following architectural design decisions in mind:
- This component is stateless to allow easy horizontal scaling. No session state will be kept. All state needed to perform an operation needs to be sent by the client or be retrieved from another service. 

## Technology Choices
### Implementation
This service is implemented as a Java 11 application using the [Spring Boot](https://spring.io/projects/spring-boot) framework.

### Security
Client authorisation to the REST API endpoints is controlled using the [Spring Security](https://spring.io/projects/spring-security) framework and is configured as an OAuth2 Resource Server accepting JWTs. The JWTs are generated for the client by the [User Authentication Service](../docs/design/architecture.md#user-authentication-service).

### REST Client
To make calls to other RESTful APIs, this service uses the Spring framework's RestTemplate backed by Apache HttpComponent's [HttpClient](https://hc.apache.org/httpcomponents-client-ga/) configured to cache responses in memory.

