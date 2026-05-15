# Key Generator

Application web Spring Boot qui permet de generer rapidement des identifiants et secrets de test depuis une interface locale.

## Fonctionnalites

- Code secret configurable :
  - longueur 16, 32, 48 ou 64 caracteres
  - minuscules, majuscules, chiffres et caracteres speciaux
- Code PIN :
  - 4, 6 ou 8 chiffres
- UUID
- Token API :
  - prefixe configurable
  - longueur 32, 48 ou 64 caracteres
- Cle hexadecimal :
  - longueur 32, 48 ou 64 caracteres
- IBAN / RIB francais fictif
- Bouton de copie du resultat
- Changement de theme pastel selon le type de generation choisi

Les codes secrets, tokens, PIN et cles sont generes avec `SecureRandom`.

## Prerequis

- Java 17
- Maven

Verifier l'installation :

```bash
java -version
mvn -version
```

## Installation locale

Depuis le dossier dans lequel vous voulez installer le projet :

```bash
git clone <url-du-repo>
cd key-generator
```

Si le projet est deja present localement :

```bash
cd /home/anas/Desktop/personel/key-generator
```

## Lancer l'application

```bash
mvn spring-boot:run
```

Puis ouvrir :

```text
http://localhost:8080
```

Si le port `8080` est deja utilise, lancer l'application sur un autre port :

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

Puis ouvrir :

```text
http://localhost:8081
```

## Build

Pour compiler et verifier le projet :

```bash
mvn clean test
```

Pour generer un fichier `.jar` :

```bash
mvn package
```

Puis lancer le `.jar` :

```bash
java -jar target/keygenerator-0.0.1-SNAPSHOT.jar
```

## Structure du projet

```text
src/main/java/com/example/keygenerator/
  KeyGeneratorApplication.java   Point d'entree Spring Boot
  GeneratorController.java       Routes web et logique de generation

src/main/resources/templates/
  index.html                     Interface Thymeleaf

src/main/resources/static/css/
  styles.css                     Styles de l'application
```
