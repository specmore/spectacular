# Integration Tests
This folder contains tests that test [all the architectural components](../docs/design/architecture.md) (that build up to form the Spectacular tool) integrated together and as a whole.

## Decisions
- External resources outside the Spectacular system are stubbed out. e.g. GitHup API

## Test Packs
The following test packs are used to test different concerns:
- End-to-End UI tests:
  - Test the core UI user journeys
  - Test the integrated between the components are working

### Running the End-to-End UI Tests Locally
#### Prerequisites
First ensure the Spectacular configuration environment variables are set.

Because the End-to-End tests are written against a specific set of test data setup in GitHub, you then need to ensure your Backend Service configuration item `GITHUB_API_ROOT_URL` is set to either of the following:
- The URL of the [GitHub API Mock](#github-api-mock) you have started (e.g. http://localhost:5006 or http://host.docker.internal:5006)
- Or the actual GitHub App configuration of the test data setup.

Finally, start all the components manually or using the docker-compose file in root of the repository.

#### Run the tests
Run cypress using a docker container
```
docker run -it -v $PWD/:/e2e -w /e2e -e "CYPRESS_baseUrl=http://host.docker.internal:80" cypress/included:3.3.2
```

Or if you have cypress installed in your development environment
```
npx cypress run --config baseUrl=http://localhost:80
```

## Resources
### GitHub API Mock
Start the GitHub API wiremock container with the existing stubbed responses
```
docker run -d --name github-wiremock -p 5006:5000 -v $PWD/github-mock/test:/home/wiremock rodolpheche/wiremock --port 5000
```
Stop the GitHub API wiremock container
```
docker rm -f github-wiremock
```

Start the GitHub API wiremock container in "Recording Mode" to record responses from the _actual_ GitHub API.
```
docker run -d --name github-wiremock -p 5006:5000 -v $PWD/github-mock/test:/home/wiremock rodolpheche/wiremock --proxy-all="https://api.github.com" --record-mappings --verbose --port 5000
```
