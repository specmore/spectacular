FROM gradle:6.0.1-jdk11 AS build
#FROM openjdk:11 AS build
WORKDIR /app
COPY build.gradle gradle.properties settings.gradle ./
#COPY build.gradle gradlew gradlew.bat gradle.properties settings.gradle ./
COPY gradle ./gradle
COPY github-service/build.gradle ./github-service/
COPY github-service/src ./github-service/src
#RUN ./gradlew build
RUN gradle build

FROM openjdk:11-jre
COPY --from=build app/github-service/build/libs/github-service.jar /app.jar

EXPOSE 5000

CMD [ "-jar", "/app.jar" ]
ENTRYPOINT [ "java" ]
