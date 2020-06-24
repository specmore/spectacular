# Spectacular &middot; [![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/specmore/spectacular/blob/master/LICENSE) [![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/specmore/spectacular/blob/master/CONTRIBUTING.md#your-first-pull-request)
Keep your interface specifications Spectacular!

Spectacular helps to make the change review process for interface specifications more visible and organised, resulting in the healthier evolution of interfaces. Ultimately fostering an "API First" culture.

## Key Features
* Integration with GitHub for user login and access control to your interface spec files
* Ability to organise your interface spec files into "Catalogues", making them easier to find
* Visualising your interface specifications using graphical documentation tools like [Swagger UI](https://github.com/swagger-api/swagger-ui) for OpenAPI files
* Easier tracking of open change proposals for an interface by showing all the open Pull Requests in one place

<!-- Todo: Link to demo -->

## Run the Demo App Locally
You can get Spectacular App up and running locally connected to our demo catalogue in no time. Just follow these steps:

1. Clone this repository to your computer
2. Run the `start-demo` script

For example, on Mac:
```shell
$ git clone git@github.com:specmore/spectacular.git
$ cd spectacular
$ ./start-demo.sh
```

## Setup Guide
To get Spectacular working with your own interface specification files (e.g. OpenAPI files), the Setup Guide section will help you get your own installation of Spectacular up and running.

### 1. Give Spectacular Access to your Interface Specification Files
Spectacular requires your interface specification files to be stored in git repositories. Therefore, you will first need to configure your git repository hosting solution (only GitHub is currently supported) to give Spectacular access.

This can be done by following the [Git Integration](/docs/git-integration.md) instructions.

### 2. Create your Catalogue Config Manifest files
To configure Spectacular display your interface specification files in nice organised catalogues, you will need to add a `spectacular-config.yml` in one of the git repositories Spectacular can access (as configured in the step above).

Guidelines for creating a `spectacular-config.yml` can be found in the [Catalogue Configuration Document](docs/catalogue-configuration.md). Or you can use the Demo project's [spectacular-config.yml](https://github.com/specmore/spectacular-demo/blob/master/spectacular-config.yml) as a template.

### 3. Configure your Spectacular instance
After setting up a git integration with your git hosting solution (in step 1. above), you should be ready to configure and run your own instance of Spectacular with access to your spec files.

As described in the [Architecture](docs/architecture.md) document, an instance of Spectacular is actually built up using 3 services. Please follow the instructions in the [Configuration](docs/configuration.md) guide on how to configure each service using their required Environment Variables.

#### Configure and Run using Docker Compose
To help build, configure and run all 3 components in one quick command, a [docker-compose.yml](docker-compose.yml) file is provided in this repository. (Docker Engine v17.05 or above with Docker Compose is required)

The [docker-compose.yml](docker-compose.yml) requires 6 environment variables (beginning with the `SPECTACULAR_` prefix) to be set corresponding to each of the 6 environment variables in the [Configuration](docs/configuration.md) guide. It is recommended to create a [.env file](https://docs.docker.com/compose/environment-variables/#the-env-file) along side the [docker-compose.yml](docker-compose.yml) file to capture these environment variables in one place.

For example:
```
SPECTACULAR_GITHUB_APP_ID=12345
SPECTACULAR_GITHUB_APP_INSTALLATION_ID=987654
SPECTACULAR_GITHUB_APP_PRIVATE_KEY_FILE_PATH=c:/temp/spectacular-app.private-key.pem
SPECTACULAR_GITHUB_CLIENT_ID=Iv1.41eb20b07bce2547
SPECTACULAR_GITHUB_CLIENT_SECRET=3dd2b1d461df1688dfdd32169cc8075e19c4f59a
SPECTACULAR_JWT_SHARED_SECRET=0c4ec70dbe9cceba51455c402b35d3a5
```

You can now start your Spectacular instance with the following command:
```
$ docker-compose up
```

Once started, your Spectacular app should be available at [http://localhost/](http://localhost/).

## Contributing

### Contribution Guidelines
Want to contribute to Spectacular? Please refer to the [Contributing Guidelines](CONTRIBUTING.md) document for more information on how to do it.

### Code of Conduct
The Spectacular project has adopted the [Contributor Covenant](https://www.contributor-covenant.org/) as its Code of Conduct, and we expect project participants to adhere to it. Please read the full text in our [Code of Conduct](CODE_OF_CONDUCT.md) so that you can understand what actions will and will not be tolerated.

### License
Spectacular is [MIT licensed](LICENSE).
