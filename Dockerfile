
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Αντιγραφή του jar που παράγει το maven
COPY target/streetfoodgo-0.0.1-SNAPSHOT.jar app.jar

# Εκτέλεση της εφαρμογής
ENTRYPOINT ["java", "-jar", "app.jar"]
