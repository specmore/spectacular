# Spectacular Backend Service
This component acts as a [Backend Service](../docs/architecture.md#backend-service) that supports the UI for Spectacular and is the "brains" of the tool. It encapsulates almost all the logic behind most of Spectacular's features and functionality.

## Config
The backend project requires the following configuration Environment Variables to be set before starting the application:
- `GITHUB_APP_ID`
- `GITHUB_APP_PRIVATE_KEY_FILE_PATH`
- `GITHUB_CLIENT_ID`
- `GITHUB_CLIENT_SECRET`
- `JWT_SHARED_SECRET`

Please refer to the dedicated [configuration guide](../docs/configuration.md) for instruction on how to set these values.

The following configuration values can also be changed from their defaults:
- `GITHUB_API_ROOT_URL` - The root/base url of the GitHup API (defaults to `https://api.github.com`)
- `JWT_COOKIE_NAME` - The unique name of the cookie for storing the user's JWT on their client agent and sent in each API request (defaults to `jwt_token`)

## Local Development and Testing

### Development Environment Requirements:
- [Java 11 JDK](https://jdk.java.net/11/) - ensure your JAVA_HOME environment variable is set to the location of your Java 11 JDK

### Tasks
This project uses the [Gradle](https://docs.gradle.org/current/userguide/userguide.html) build tool to provide the following key application development life cycle tasks:

#### Running the Application
First ensuring the environment variables in the [Config](#config) section above are set appropriately, then run the following command:
```
./gradlew bootRun
```

#### Testing the Application
```
./gradlew check
```

#### Regenerating the API Controllers and Models
This API and its models are described by the [backend-api.yaml](/specs/backend-api.yaml) OpenAPI file.
To regenerate the API interface and model classes from this spec file after making changes to it, use the following gradle command:
```
./gradlew openApiGenerate
```

## Design Decisions
### Architecture Pattern
The objects in this project could be organised horizontally using the following conceptual layers:
- A RESTful API layer to expose functionality publicly and it's associated models (DTOs).
- A service layer containing all the logic required to fulfil each API request
- Adaption layer for making API requests to other resources like GitHub's API

However the project is organised vertically by the domain concerns instead, for example:
- [App](src/main/java/spectacular/backend/app) - Responsible for handling request specific to this instance of the Spectacular app and the SCM integrations it has been configured with. This includes handling user login workflows.
- [Catalogue Manifests](src/main/java/spectacular/backend/cataloguemanifest) - Responsible for finding and parsing [Catalogue Manifest files](../docs/catalogue-configuration.md).
- [Catalogues](src/main/java/spectacular/backend/catalogues) - Responsible for collecting information about Interface Catalogues and returning detailed representations back to the user.
- [GitHub](src/main/java/spectacular/backend/github) - An adaptor to the GitHub SCM from which interface information is gathered.
- [Interfaces](src/main/java/spectacular/backend/interfaces) - Responsible for collecting information about a specific Interface and returning detailed representations back to the user.
- [Security](src/main/java/spectacular/backend/security) - Responsible for supplying services to help secure the backend application.
- [Spec Evolution](src/main/java/spectacular/backend/specevolution) - Responsible for collecting git file history data about an Interface's spec file and returning evolutionary timeline information back to the user.
- [Specs](src/main/java/spectacular/backend/specs) - Responsible for parsing the contents of Interface spec files into objects usable by the Front End application.

### Architecture Decisions
This project has been built with the following architectural design decisions in mind:
- This component is stateless to allow easy horizontal scaling. No session state will be kept. All state needed to perform an operation needs to be sent by the client or be retrieved from another service. 

### Technology Choices
#### Implementation
This service is implemented as a Java 11 application using the [Spring Boot](https://spring.io/projects/spring-boot) framework.

#### Security
Client authorisation to the REST API endpoints is controlled using the [Spring Security](https://spring.io/projects/spring-security) framework and is configured as an OAuth2 Resource Server accepting JWTs. The JWTs are generated for the client by the [User Authentication Service](../docs/architecture.md#user-authentication-service) and using the `HS512` signing algorithm.

#### REST Client
To make calls to other RESTful APIs, this service uses the Spring framework's RestTemplate backed by Apache HttpComponent's [HttpClient](https://hc.apache.org/httpcomponents-client-ga/) configured to cache responses in memory.

#### Build Tooling
This Java project is built using [Gradle](https://docs.gradle.org/current/userguide/userguide.html).

#### Testing
This projected is tested using two methods:
- Unit tests to test code level units
- "In Process" integration tests to test the fully integrated application in the same process as the test runner

The unit and integration tests for this project have been written using the [Spock](http://spockframework.org/) testing framework and Groovy.

#### Code Style
[CheckStyle](https://checkstyle.org/) is used to add a code quality check step to the gradle build. The default [Google Java Style](https://github.com/checkstyle/checkstyle/blob/master/src/main/resources/google_checks.xml) checks are used.
