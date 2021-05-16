# Integration Tests
This folder contains tests that test [all the architectural components](../docs/architecture.md) (that build up to form the Spectacular tool) integrated together as a whole.

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
First change working directory to the [ui-e2e](ui-e2e/) folder.

Then open (or run) cypress. For example using the npm package:
```
npx cypress@4.8.0 open
```

## Resources
### GitHub API Mock
Run the tests using a GitHub API wiremock container with the existing stubbed responses using the supplied script:
```
.\scripts\run-int-tests.bat
```
or
```
./scripts/run-int-tests.sh
```

Or run the tests with the GitHub API wiremock container recording the interactions with the real GitHub API.
```
.\scripts\record-int-tests.ps1
```
