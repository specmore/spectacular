# Spectacular
Spectacular helps to make the change review process for interface specifications easier and more organised resulting in the healthier evolution of interfaces. Ultimately fostering an "API First" culture.

## Key Features
* Integration with GitHub for user login and access to your interface spec files in your git repositories
* Ability to organise your interface spec files into "Catalogues" making it easier to find them
* Visualise your interface specifications using graphical documentation tools like [Swagger UI](https://github.com/swagger-api/swagger-ui) for OpenAPI files
* Easier tracking of open change proposals to an interface by listing all the open Pull Requests against a given interface specification files

<!-- Todo: Link to demo -->

## Quick Setup Guide
### Installation Guide
As described in the [Architecture](docs/architecture.md) document, Spectacular is built up of 3 components.

To help build, configure and run all 3 components in one quick command, a [docker-compose.yml](docker-compose.yml) file is provided. This requires Docker Engine v17.05 or above with Docker Compose to be installed.

#### 1. Set Environment Variables
The [docker-compose.yml](docker-compose.yml) requires 6 environment variables to be set to get a basic setup of Spectacular running. The following `.env` file can be used 
```
SPECTACULAR_GITHUB_APP_ID=52196
SPECTACULAR_GITHUB_APP_INSTALLATION_ID=6436743
SPECTACULAR_GITHUB_APP_PRIVATE_KEY_FILE_PATH=c:/temp/spectacular-dev-app.2020-01-26.private-key.pem
SPECTACULAR_GITHUB_CLIENT_ID=Iv1.41eb20b07bce2545
SPECTACULAR_GITHUB_CLIENT_SECRET=3dd2b1d461df1688dfdd32169cc8075e19c4f59b
SPECTACULAR_JWT_SHARED_SECRET=this_test_shared_key_is_32_bytes
```
### Configuration Guide
