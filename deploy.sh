#!/bin/bash

echo "Starting Deployment..."
cd ~/DS-LAB
git pull origin main
#Θελω να ειναι ελαφρυς ο σερβερ-γιατι το build ειναι βαρυ (θα ειναι κατω καθολη τη διαρκεια ή εαν αποτυχει)
sudo docker-compose down

./mvnw clean package -DskipTests
#μεσα στο docker γινεται το copy του νεου .jar
sudo docker-compose up --build -d
#προαιρετικο για να βλεπω τα logs του app
#sudo docker-compose logs -f app #χωρις το app, για να δω logs nginx,app,db
sudo docker image prune -f

echo "Deployment Finished! Site is up"
