# Инструкция по сборке и запуску для DevOps

## Содержание

- [Общая информация](#общая-информация)
- [Инструкция по сборке](#инструкция-по-сборке)
- [Инструкция по запуску](#инструкция-по-запуску)
- [Docker и docker-compose](#docker-и-docker-compose)
- [Дополнительные рекомендации](#дополнительные-рекомендации)

## Общая информация

- Микросервис должен быть собран в docker-compose.yml файл
- Проект основан на фреймворке Quarkus
- Сервис разработан для развертывания в облачной среде Yandex Cloud
- Логин от базы данных: iotcloud
- Пароль от базы данных: iotcloud
- quarkus.datasource.jdbc.url=jdbc:postgresql://rc1a-bfu70ickst3dddjj.mdb.yandexcloud.net:6432,rc1b-f9lhyz1dv9xlf269.mdb.yandexcloud.net:6432,rc1b-kq6i0o1caufodow3.mdb.yandexcloud.net:6432/iotcloud_users?targetServerType=master&ssl=true&sslmode=verify-full

## Инструкция по сборке

1. Требования к Java:
  - Используйте Java 17 (JDK 17) для сборки проекта
  - Убедитесь, что переменная JAVA_HOME настроена правильно

2. Система сборки:
  - Используйте Gradle версии 8.9
  - Убедитесь, что Gradle установлен и доступен в PATH

3. Структура проекта:
  - Основные классы доступны через директорию src
  - Зависимости проекта находятся в src/main/java/ru.iot_cloud/resources

4. Шаги сборки:
  - Перейдите в корневую директорию проекта
  - Выполните команду: ./gradlew build
  - После успешной сборки, JAR-файл будет доступен в директории build/libs

## Инструкция по запуску

1. Настройка окружения:
  - Сервис будет запускаться на ВМ Yandex Cloud
  - Операционная система: Linux Ubuntu 22.04

2. Порты:
  - Quarkus запускается на порту 8080
  - Сам сервис запускается на случайном порту

3. Запуск сервиса:
  - Используйте команду: java -jar <имя_jar_файла>.jar
  - Для запуска в фоновом режиме: nohup java -jar <имя_jar_файла>.jar &

## Docker и docker-compose

1. Создание Dockerfile:
   Создайте файл Dockerfile в корневой директории проекта со следующим содержимым:

```
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```
2. Создание docker-compose.yml:
   Создайте файл docker-compose.yml в корневой директории проекта:

```
version: '3'
services:
microservices-java:
build: .
ports:
- "8080:8080"
environment:
- JAVA_OPTS=-Xmx512m
```
3. Сборка и запуск с помощью docker-compose:
  - Выполните команду: docker-compose up --build
