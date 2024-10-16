FROM openjdk:17-jdk-slim
WORKDIR /auth_service
COPY . .
EXPOSE 8080
ARG quarkus.datasource.jdbc.url=jdbc:postgresql://rc1a-bfu70ickst3dddjj.mdb.yandexcloud.net:6432,rc1b-f9lhyz1dv9xlf269.mdb.yandexcloud.net:6432,rc1b-kq6i0o1caufodow3.mdb.yandexcloud.net:6432/iotcloud_users?targetServerType=master&ssl=true&sslmode=verify-full
RUN bash ./gradlew build