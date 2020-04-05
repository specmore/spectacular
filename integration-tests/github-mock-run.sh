#!/bin/bash

docker run -d --name github-wiremock -p 5006:5000 -v $PWD/github-mock/test:/home/wiremock rodolpheche/wiremock --port 5000
