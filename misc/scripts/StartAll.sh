#!bin/bash

nohup java -jar microservices-java/auth-service/build/libs/*.jar &
sudo docker compose up --build -d
echo "Services are starting... Wait 10-15 secs"