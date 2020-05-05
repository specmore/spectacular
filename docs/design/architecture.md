# System Architecture
The systems is built up of 3 core separately deployable components:
- The [Web UI]((#web-ui)) frontend component that handles:
  - The bundling of the web UI single page application (SPA) files
  - And the serving of web requests to the web UI bundled files and backend services
- The [Backend Service](#backend-service) backend component that drives much of the Spectacular features and is invoked via a RESTful API
- The [User Authentication Service](#user-authentication-service) backend component that handles the User Login process

These 3 components can be seen in the Architecture diagram below:

![system architecture diagram](diagrams/system-architecture.png)

## Web UI
Useful links for the `Web UI` component:
- The [web](../../web) project folder contains 
  - the Single Page Web Application (SPA) source code files
  - web server configuration files
- Have a look at the project folder [README](../../web/README.md) for further documentation

## Backend Service
Useful links for the `Backend Service` component:
- The [backend](../../backend) project folder contains the application source code
- Have a look at the project folder [README](../../backend/README.md) for further documentation

## User Authentication Service
The Authentication Service is implemented using an opensource application called [loginsrv](https://github.com/tarent/loginsrv). For more details please read the [authentication-service](../authentication-service.md) documentation.
