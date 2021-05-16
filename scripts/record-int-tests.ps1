echo %cd%

Remove-Item -Recurse .\integration-tests\github-mock\test

docker-compose -f docker-compose.yml -f docker-compose.dev.yml -f docker-compose.github-stub.yml -f docker-compose.github-stub-record.yml up --build -d

docker run -v %cd%\integration-tests\ui-e2e:/e2e -w /e2e -e "CYPRESS_baseUrl=http://host.docker.internal:80" cypress/included:4.8.0

docker-compose -f docker-compose.yml -f docker-compose.dev.yml -f docker-compose.github-stub.yml -f docker-compose.github-stub-record.yml logs

docker-compose -f docker-compose.yml -f docker-compose.dev.yml -f docker-compose.github-stub.yml -f docker-compose.github-stub-record.yml down
