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
This will bundle the web project files and start a [Webpack Dev Server](https://webpack.js.org/configuration/dev-server/) to serve the bundled web application.

#### Testing the Application
This project uses the [Jest test framework](https://jestjs.io/). To run the test, use the following command:
```
npm run test
```

#### Checking the Code
This project uses [ESLint](https://eslint.org/) to ensure code quality and style. To check the code after any changes, use the following command:
```
npm run lint
```

## Design Decisions

### Architecture Decisions
This component is purposefully kept relatively "dumb" with the exception of:
- the presentational logic required to drive the User Experience journeys
- data fetching logic required to populate the visual components
- logic to maintain the user context, such as authentication tokens

#### Architecture Patterns
- This React.js application is broken down into UI components following the [Thinking in React](https://reactjs.org/docs/thinking-in-react.html) pattern. It allows us to follow a single responsibility principle for our components and allow them to map nicely with the data model returned by the backend API.
- The components are named according to the domain object they map to
- The unit test files for each component are kept alongside their implementation counterpart for ease of access

#### State
While the application state is limited to the following to areas, the choice has been made to not use and maintain any global application state stores (e.g. Redux) to reduce complexity:
- Component state is used to hold any additional data required to populate a visual component. This component state is maintained using [React Hooks](https://reactjs.org/docs/hooks-intro.html).
- User journey state is kept and updated solely in the browser's location URL. This allows the application to be loaded at the exact same point of the user journey when reloading the page or when sharing the URL. This is managed using [React-Router](https://reacttraining.com/react-router/web/guides/quick-start) to allow components to access parameters stored in the URL  when loading by using `React-Router` hooks and to modify the URL using `React-Router` navigation link components.
- The User context state is provided by the [User Authentication Service](../docs/design/architecture.md#user-authentication-service) in the form of a JWT stored in a cookie.
- The context of which GitHub App Installation this instance of the UI is configured to serve is maintained and injected into the headers of each API request by the Reverse Proxy that is serving this web app.

#### Type safety
No React propTypes or other tool is used to offer some form of type safety when working with the data objects. There maybe a decision to move to generated TypeScript models created from the backend API spec in the future.

### Technology Choices
#### Implementation
This web application is written in JavaScript ES6 using the [React](https://reactjs.org/) framework. It also consists of some HTML and CSS.

#### Build Tooling
The application is bundled (built) using [Webpack](https://webpack.js.org/) and transpiled from ES6 using [Babel](https://babeljs.io/).

#### Testing
This project uses the [Jest test framework](https://jestjs.io/) and runner. [React Testing Library](https://testing-library.com/docs/react-testing-library/intro) is used for unit testing React components.

#### Code Style
This project uses [ESLint](https://eslint.org/) to ensure code quality and style. It uses the [Airbnb JavaScript Styleguide](https://github.com/airbnb/javascript) config as a base.
