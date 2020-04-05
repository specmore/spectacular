#!/bin/bash

docker run -it -v $PWD/:/e2e -w /e2e -e "CYPRESS_baseUrl=http://host.docker.internal:80" cypress/included:3.3.2
