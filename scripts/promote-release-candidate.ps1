param ([Parameter(Mandatory)]$versionTag)

write-host "Promoting release candidate images with tag $versionTag official release repos"

write-host "Promoting web image..."
docker pull specmore/spectacular-web-release-candidate:$versionTag

docker tag specmore/spectacular-web-release-candidate:$versionTag specmore/spectacular-web:$versionTag
docker push specmore/spectacular-web:$versionTag

docker tag specmore/spectacular-web-release-candidate:$versionTag specmore/spectacular-web:latest
docker push specmore/spectacular-web:latest

write-host "Promoting backend image..."
docker pull specmore/spectacular-backend-release-candidate:$versionTag

docker tag specmore/spectacular-backend-release-candidate:$versionTag specmore/spectacular-backend:$versionTag
docker push specmore/spectacular-backend:$versionTag

docker tag specmore/spectacular-backend-release-candidate:$versionTag specmore/spectacular-backend:latest
docker push specmore/spectacular-backend:latest

write-host "Promotion complete."
