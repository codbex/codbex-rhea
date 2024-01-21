# codbex-rhea

<<<<<<< Upstream, based on branch 'main' of https://github.com/codbex/codbex-rhea.git
Rhea Edition contains entity and forms modeling standard components.
=======
Rhea Edition contains all the available standard components.
>>>>>>> 3d57a11 refactored for dirigible 9.x

<<<<<<< Upstream, based on branch 'main' of https://github.com/codbex/codbex-rhea.git
It is good for Model Driven Architecture related development models.
=======
It is good for exploration about the different features and their applicability in particular scenarios.
>>>>>>> 3d57a11 refactored for dirigible 9.x


#### Docker

```
docker pull ghcr.io/codbex/codbex-rhea:latest
docker run --name codbex-rhea --rm -p 80:80 ghcr.io/codbex/codbex-rhea:latest
```

- For Apple's M1: provide `--platform=linux/arm64` for better performance		

#### Build

```
mvn clean install
```
	
#### Run

```
java --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.nio=ALL-UNNAMED -jar application/target/codbex-rhea-application-*.jar
```

#### Debug

```
java --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.nio=ALL-UNNAMED -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000 -jar application/target/codbex-rhea-application-*.jar
```
	
#### Web

```
http://localhost
```

#### REST API

```
http://localhost/swagger-ui/index.html
```
