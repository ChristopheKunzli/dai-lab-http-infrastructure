# Utiliser l'image de base officielle OpenJDK
FROM openjdk:25-jdk-slim

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR compilé dans le conteneur
COPY target/httpserver-1.0.jar /app/httpserver-1.0.jar

# Exposer le port de l'application
EXPOSE 7000

# Démarrer l'application
CMD ["java", "-jar", "httpserver-1.0.jar"]
