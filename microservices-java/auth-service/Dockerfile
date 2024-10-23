## Stage 1 : build with maven builder image
FROM maven:3.9.9-amazoncorretto-8-al2023 AS build
COPY --chown=quarkus:quarkus gradlew /code/gradlew
COPY --chown=quarkus:quarkus gradle /code/gradle
COPY --chown=quarkus:quarkus build.gradle settings.gradle gradle.properties /code/
COPY --chown=quarkus:quarkus src /code/src

WORKDIR /code
RUN chmod +x gradlew
RUN ./gradlew build

## Stage 2 : create the docker final image
FROM registry.access.redhat.com/ubi8/openjdk-17:1.14

ENV LANGUAGE='en_US:en'

COPY --from=build /code/build/libs/*-runner.jar /deployments/quarkus-run.jar

EXPOSE 8080
USER 185
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

ENTRYPOINT [ "/opt/jboss/container/java/run/run-java.sh" ]


