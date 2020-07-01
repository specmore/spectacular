#!/bin/bash

echo $PWD

docker-compose -f docker-compose.yml -f docker-compose.ci.yml -f docker-compose.github-stub.yml --env-file="./config/stubbed.env" config

docker-compose -f docker-compose.yml -f docker-compose.ci.yml -f docker-compose.github-stub.yml --env-file="./config/stubbed.env" up --build -d
echo $?

docker run -v $PWD/integration-tests/ui-e2e:/e2e -w /e2e -e "CYPRESS_baseUrl=http://host.docker.internal:80" --add-host=host.docker.internal:$(ip route | grep docker0 | awk '{print $9}') cypress/included:4.8.0
echo $?

docker-compose -f docker-compose.yml -f docker-compose.ci.yml -f docker-compose.github-stub.yml --env-file="./config/stubbed.env" logs
echo $?

docker-compose -f docker-compose.yml -f docker-compose.ci.yml -f docker-compose.github-stub.yml --env-file="./config/stubbed.env" down
echo $?
