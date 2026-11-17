# TP Microservices — Eureka, Client, Gateway et Voiture

## Prérequis
- Java 17 ou supérieur (compiler en Java 17)
- Maven 3.9+
- MySQL sur localhost:3306, utilisateur root (mot de passe vide par défaut, à adapter)
- IntelliJ IDEA (ouvrir le `pom.xml` racine comme projet Maven)

Versions configurées : Spring Boot 3.3.13 et Spring Cloud 2023.0.6. Maven téléchargera les dépendances à la première compilation (connexion Internet nécessaire).

## Compiler tout le projet
Depuis le dossier racine :
```powershell
mvn clean package -DskipTests
```

## Démarrer (4 terminaux, dans cet ordre)
```powershell
mvn -f eureka-server/pom.xml spring-boot:run
mvn -f client-service/pom.xml spring-boot:run
mvn -f gateway-service/pom.xml spring-boot:run
mvn -f car-service/pom.xml spring-boot:run
```
Chaque commande s'exécute dans son propre terminal, depuis la racine du projet.

## Ports
- Eureka : http://localhost:8761
- Client : http://localhost:8081/api/client
- Gateway : http://localhost:8888
- Voiture : http://localhost:8082/api/car

## Tester avec PowerShell
Créer un client :
```powershell
Invoke-RestMethod -Method Post -Uri 'http://localhost:8081/api/client' -ContentType 'application/json' -Body '{"nom":"Anas","age":21}'
```
Créer une voiture associée au client d'identifiant 1 :
```powershell
Invoke-RestMethod -Method Post -Uri 'http://localhost:8082/api/car' -ContentType 'application/json' -Body '{"brand":"Renault","model":"Clio","matricule":"12345-A-6","client_id":1}'
```
Lire via la Gateway :
```powershell
Invoke-RestMethod 'http://localhost:8888/SERVICE-CLIENT/api/client'
Invoke-RestMethod 'http://localhost:8888/SERVICE-CAR/api/car'
```
Le serveur Eureka doit afficher SERVICE-CLIENT, SERVICE-CAR et GATEWAY.

## Remarques
- `clientservicedb` et `carservicedb` sont créées automatiquement si le compte MySQL dispose du droit CREATE DATABASE.
- Pour un mot de passe MySQL non vide, définir la variable d'environnement `DB_PASSWORD` dans chaque configuration d'exécution ou terminal.
- La Gateway utilise la configuration automatique `discovery.locator.enabled`; aucun bean manuel de localisation de routes n'est nécessaire.
- La dépendance `spring-boot-starter-web` n'est volontairement pas ajoutée à la Gateway réactive pour éviter un conflit MVC/WebFlux.
- Les annotations `@EnableEurekaClient` et `@EnableDiscoveryClient` ne sont pas nécessaires avec l'auto-configuration de Spring Cloud.
- Le modèle Client du service voiture utilise `nom` et `age` pour correspondre au JSON réel du service client.
- Un endpoint POST `/api/car` a été ajouté pour permettre d'insérer des voitures sans SQL manuel.
- L'appel Car -> Client passe par la Gateway comme dans l'énoncé ; pour la production, préférer un appel par nom de service avec load balancing, sans URL localhost figée.
- Configuration de TP local, non durcie pour la production.
