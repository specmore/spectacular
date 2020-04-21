FROM gradle:6.3.0-jdk11 AS build
ARG SEMVER

WORKDIR /app
COPY build.gradle gradle.properties settings.gradle ./

COPY src ./src

RUN echo $SEMVER
RUN gradle -Pversion=$SEMVER build

### 
FROM openjdk:11-jre

ENV GITHUB_APP_PRIVATE_KEY_FILE_PATH=/usr/spectacular/github.private-key.pem

COPY --from=build app/build/libs/backend.jar /app.jar

EXPOSE 80

CMD [ "-jar", "/app.jar", "--server.port=80" ]
ENTRYPOINT [ "java" ]
