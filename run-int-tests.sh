#!/usr/bin/env sh

echo $PWD

docker-compose -f docker-compose.yml -f docker-compose.dev.yml -f docker-compose.github-stub.yml --env-file="./config/stubbed.env" up --build -d

docker run -v $PWD/integration-tests/ui-e2e:/e2e -w /e2e -e "CYPRESS_baseUrl=http://host.docker.internal:80" cypress/included:4.8.0

docker-compose -f docker-compose.yml -f docker-compose.dev.yml -f docker-compose.github-stub.yml --env-file="./config/stubbed.env" logs

docker-compose -f docker-compose.yml -f docker-compose.dev.yml -f docker-compose.github-stub.yml --env-file="./config/stubbed.env" down
