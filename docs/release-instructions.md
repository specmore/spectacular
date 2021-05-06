# Release Instructions

## Double check and finalise the draft release
The [Release Drafter](https://github.com/specmore/spectacular/actions?query=workflow%3A%22Release+Drafter%22) Action on the repository should be maintaining a list of unreleased changes in a "Draft" release. 

Check the repository's [Releases](https://github.com/specmore/spectacular/releases) page and  ensure the correct version number is set. Versioning is done according to [SemVer](http://semver.org).

## Create a build from the release tag
Once release has been published and the tag is created, run the SpectacularCI build pipeline for the `refs/tags/vX.X.X` tag.

Ensure the pipeline run has created release candidate images for both Backend and Web components with the right version.
This can be done by running the [Spectacular Test Deploy](https://dev.azure.com/specmore/Spectacular/_build?definitionId=2&_a=summary) pipeline with the `overrideImageTag` set to the `X.X.X` version number and check that the [test site](https://spectacular-test.specmore.org/) reports the appropriate version number.

## Promote the Docker Images
Pull, tag and push the docker images with the new version number.

Web
```
docker pull specmore/spectacular-web-release-candidate:<version number>

docker tag specmore/spectacular-web-release-candidate:<version number> specmore/spectacular-web:<version number>
docker push specmore/spectacular-web:<version number>

docker tag specmore/spectacular-web-release-candidate:<version number> specmore/spectacular-web:latest
docker push specmore/spectacular-web:latest
```

Backend
```
docker pull specmore/spectacular-backend-release-candidate:<version number>

docker tag specmore/spectacular-backend-release-candidate:<version number> specmore/spectacular-backend:<version number>
docker push specmore/spectacular-backend:<version number>

docker tag specmore/spectacular-backend-release-candidate:<version number> specmore/spectacular-backend:latest
docker push specmore/spectacular-backend:latest
```

## Update Spectacular Helm Charts
Update the following files to reflect the new App version number:
- Chart.yaml
- templates/deployment.yaml
- Readme.md - create a new release in #changelog section

Create new release for the project.

Check the new chart has been published by the [Spectacular Chart Publish](https://g.codefresh.io/pipelines/edit/new/builds?id=5f01aae83ba05b283bdc5d3b&pipeline=Spectacular%20Chart%20Publish&projects=Spectacular%20Publishing&projectId=5f01a9a13ba05be95fdc5d3a&rightbar=steps&context=github&filter=page:1;pageSize:10;timeFrameStart:week) build to the [Spectacular Helm](https://g.codefresh.io/helm/charts/CF_HELM_DEFAULT/spectacular) repo.


## upgrade Demo Site
Upgrade the demo environment with the latest helm chart as per instructions in [spectacular-demo-config](https://github.com/specmore/spectacular-demo-config).

