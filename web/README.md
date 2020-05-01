# Spectacular Web User Interface
This web SPA project provides the [User Interface](../docs/design/architecture.md#web-ui) for the Spectacular tool.

## Config
The web project requires the following configuration values to be set as Environment Variables before starting the application:
- `GITHUB_APP_INSTALLATION_ID` - The specific installation (tenant) this instance of the UI will be serving. Required to be sent to the Backend Service in each API request. The installation needs to belong to the GitHub App the [Backend Service](../backend/README.md#config) is configured to use.

The following configuration values can also be changed from their defaults:
- `API_LOCATION` - The root/base url or the Backend Service API (defaults to `http://localhost:5000`)
- `AUTH_LOCATION` - The root/base url or the User Authentication Service API (defaults to `http://localhost:5001`)

## Local Development and Testing

### Development Environment Requirements:
- [Node.js v10](https://nodejs.org/en/download/releases/) or above

This project uses different Node.js packages. To ensure all these packages are installed, please run the following command before trying to use any of the tasks below:
```
npm install
```

### Tasks
The following standard set of [NPM run scripts](https://docs.npmjs.com/cli/run-script) have been provided to perform the different key application development life cycle tasks:

#### Running the Application
First ensuring the environment variables in the [Config](#config) section above are set appropriately, then run the following command:
```
npm run start
```
This will bundle the web project files and start a webpack development server to serve the bundled web application.

#### Testing the Application
This project uses the [Jest test framework](https://jestjs.io/). To run the test, use the following command:
```
npm run test
```

#### Checking the Code
This project uses [ESLint](https://eslint.org/) to ensure code quality and style. To check the code, use the following command:
```
npm run lint
```

## Design Decisions
### Architecture Pattern

### Architecture Decisions
This component is purposefully kept relatively "dumb" with exception of:
- the presentational logic required to drive the User Experience
- data fetching logic required to populate the visual components and application state
- logic to maintain the application state, such as authentication tokens

### Technology Choices
#### Implementation

#### Security

#### REST Client

#### Build Tooling

#### Testing

#### Code Style