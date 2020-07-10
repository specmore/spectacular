# Release Instructions

## Double check and finalise the draft release
The [Release Drafter](https://github.com/specmore/spectacular/actions?query=workflow%3A%22Release+Drafter%22) Action on the repository should be maintaining a list of unreleased changes in a "Draft" release. 

Check the repository's [Releases](https://github.com/specmore/spectacular/releases) page and  ensure the correct version number is set. Versioning is done according to [SemVer](http://semver.org).

## Ensure the Build version is correct
The SpectacularCI build pipeline should have created release candidate images for both Backend and Web components with the right version.

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

