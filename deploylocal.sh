#!/bin/bash

echo "Starting Local Deployment ..."

./mvnw clean package -DskipTests
#μεσα στο docker γινεται το copy του νεου .jar
sudo docker-compose up --build -d
#προαιρετικο για να βλεπω τα logs του app

sudo docker-compose logs -f #χωρις το app, για να δω logs nginx,app,db


echo "Deployment Finished! Site is up"
