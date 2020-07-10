#!/usr/bin/env sh

echo $PWD
echo $@

docker-compose -f docker-compose.yml -f docker-compose.dev.yml up $@
