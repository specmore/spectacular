# Contributing to Spectacular
*Spectacular is a new opensource project and we are eager to foster healthy community around it. All contributions are welcome, no matter how big or small. We're still working on how best to make contributing to the project as easy as possible, but hopefully this document makes the contribution process a bit clearer.*

### Code of Conduct

The Spectacular project has adopted the [Contributor Covenant](https://www.contributor-covenant.org/) as its Code of Conduct, and we expect project participants to adhere to it. Please read the full text in our [Code of Conduct](CODE_OF_CONDUCT.md) so that you can understand what actions will and will not be tolerated.

### Semantic Versioning

React follows [semantic versioning](https://semver.org/). We release patch versions for critical bugfixes, minor versions for new features or non-essential changes, and major versions for any breaking changes. When we make breaking changes, we also introduce deprecation warnings in a minor version so that our users learn about the upcoming changes and migrate their code in advance.

### Branch Organization

Submit all changes directly to the [`master branch`](https://github.com/specmore/spectacular/tree/master). We don't use separate branches for development or for upcoming releases. We do our best to keep `master` in good shape, with all tests passing.

Code that lands in `master` must be compatible with the latest stable release. It may contain additional features, but no breaking changes. We should be able to release a new minor version from the tip of `master` at any time.

### Where to Find Known Issues

We are using [GitHub Issues](https://github.com/specmore/spectacular/issues) for tracking our bugs. Before filing a new issue, try to make sure your problem doesn't already exist.

### Proposing a Change

If you intend to change the API, or make any non-trivial changes to the implementation, we recommend [filing an issue](https://github.com/specmore/spectacular/issues/new). This lets us reach an agreement on your proposal before you put significant effort into it.

If you're only fixing a bug, it's fine to submit a pull request right away but we still recommend to file an issue detailing what you're fixing. This is helpful in case we don't accept that specific fix but want to keep track of the issue.

### Contribution Prerequisites
Spectacular is built up using several components, so please familiarise yourself with the [architecture](docs/architecture.md) to help ensure you know where best to contribute.

Documentation for the development environment setup required for each component can be found below:
   1. Backend Service [Development Requirements](/backend/README.md#development-environment-requirements)
   2. Web UI [Development Requirements](/web/README.md#development-environment-requirements)

Also be sure to familiar yourself with Git.

### Your First Pull Request

Working on your first Pull Request? You can learn how from this free video series:

**[How to Contribute to an Open Source Project on GitHub](https://egghead.io/series/how-to-contribute-to-an-open-source-project-on-github)**

To help you get your feet wet and get you familiar with our contribution process, we have a list of **[good first issues](https://github.com/specmore/spectacular/issues?q=is:open+is:issue+label:"good+first+issue")** that contain bugs that have a relatively limited scope. This is a great place to get started.

If you decide to fix an issue, please be sure to check the comment thread in case somebody is already working on a fix. If nobody is working on it at the moment, please leave a comment stating that you intend to work on it so other people don't accidentally duplicate your effort.

If somebody claims an issue but doesn't follow up for more than two weeks, it's fine to take it over but you should still leave a comment.

### Sending a Pull Request

The core team is monitoring for pull requests. We will review your pull request and either merge it, request changes to it, or close it with an explanation.

**Before submitting a pull request,** please make sure the following is done:

1. Fork [the repository](https://github.com/specmore/spectacular) and create your branch from `master`.
2. Ensure you have have followed the [Contribution Prerequisites](#contribution-prerequisites).
3. If you've fixed a bug or added code that should be tested, add tests!
4. Ensure the test suite of each modified service passes:
   1. Running [Backend Service Tests](/backend/README.md#testing-the-application)
   2. Running [Web UI Tests](/web/README.md#testing-the-application)
5. Ensure the integration tests passes. **TBD**
6. Ensure the docker images build. This can be by building and running the [docker-compose.yml](docker-compose.yml) file.

### Development Workflow
To be able to make your changes to the required components, first make sure you have the necessary development environment setup by following the [Contribution Prerequisites](#contribution-prerequisites) instructions.

To help run components you don't need to change locally, the following convenience script has be provided to run any of the components in a local docker container using docker compose.
Bash:
```bash
$ ./scripts/start-dev.sh [SERVICE...]
```
Batch:
```powershell
$ ..\scripts\start-dev.bat [SERVICE...]
```

The table below shows you the different options for building and running each architectural component locally on your development machine.
| Spectacular Component | Setup Development Environment | Docker Compose Service Name |
| ----------------------| ------------------------- | ---------------- |
| Web UI | [Development Environment Requirements](web/README.md#development-environment-requirements) | `web` |
| Backend Service | [Development Environment Requirements](backend/README.md#development-environment-requirements) | `backend` |

For example, you may wish to make changes to the [Web UI](web/) component only by:
1. Starting the Backend services in containers using `$ ./scripts/start-dev.sh backend` (also achievable using `npm run start-deps`)
2. Building and running the Web UI locally using `npm run start`

**WIP**

### License

Spectacular is [MIT licensed](LICENSE).
