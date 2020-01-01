FROM gradle:6.0.1-jdk11 AS build

WORKDIR /app
COPY build.gradle gradle.properties settings.gradle ./

COPY github-service/build.gradle ./github-service/
COPY github-service/src ./github-service/src

RUN gradle build

### 
FROM openjdk:11-jre

VOLUME ["/usr/spectacular"]
ENV GITHUB_APP_PRIVATE_KEY_FILE_PATH=/usr/spectacular/github.private-key.pem

COPY --from=build app/github-service/build/libs/github-service.jar /app.jar

EXPOSE 5000

CMD [ "-jar", "/app.jar" ]
ENTRYPOINT [ "java" ]
