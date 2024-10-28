## Stage 1 : build with maven builder image
FROM maven:3.6.3-openjdk-17-slim AS build
COPY --chown=quarkus:quarkus gradlew /auth_service/gradlew
COPY --chown=quarkus:quarkus gradle /auth_service/gradle
COPY --chown=quarkus:quarkus build.gradle settings.gradle gradle.properties /auth_service/
COPY --chown=quarkus:quarkus src /auth_service/src
COPY --chown=quarkus:quarkus /auth_service/postgresql/root.crt /auth_service/postgresql/root.crt 

WORKDIR /auth_service
RUN chmod +x gradlew
RUN ./gradlew build

## Stage 2 : create the docker final image
FROM registry.access.redhat.com/ubi8/openjdk-17:1.14

ENV LANGUAGE='en_US:en'

COPY --from=build /auth_service/build/libs/*-runner.jar /deployments/quarkus-run.jar

EXPOSE 8080
USER 185
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

ENTRYPOINT [ "/opt/jboss/container/java/run/run-java.sh" ]


