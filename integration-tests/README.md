### GitHub API Mock
start github wiremock
```
docker run -d --name github-wiremock -p 5006:5000 -v $PWD/github-mock/test:/home/wiremock rodolpheche/wiremock --port 5000
```
stop github wiremock
```
docker rm -f github-wiremock
```
recording mode
```
docker run -d --name github-wiremock -p 5006:5000 -v $PWD/github-mock/test:/home/wiremock rodolpheche/wiremock --proxy-all="https://api.github.com" --record-mappings --verbose --port 5000
```

### Mock Environment Variables
```
SPECTACULAR_GITHUB_API_ROOT_URL=http://host.docker.internal:5006
```

### Start Services
```
docker-compose up
```

### Run UI End-to-end tests
```
npx cypress run --config baseUrl=http://localhost:80
```
or
```
docker run -it -v $PWD/:/e2e -w /e2e -e "CYPRESS_baseUrl=http://host.docker.internal:80" cypress/included:3.3.2
```