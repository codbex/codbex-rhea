# codbex-rhea-tomcat

The `codbex` rhea `tomcat` package

To build the docker image:

    docker build -t codbex-rhea-tomcat:latest .

To run a container:

    docker run --name codbex --rm -p 8080:8080 -p 8081:8081 codbex-rhea-tomcat:latest

To tag the image:

    docker tag codbex-rhea-tomcat codbex.jfrog.io/codbex-docker/codbex-rhea-tomcat:latest

To push to JFrog Container Registry:

    docker push codbex.jfrog.io/codbex-docker/codbex-rhea-tomcat:latest

To pull from JFrog Container Registry:

    docker pull codbex.jfrog.io/codbex-docker/codbex-rhea-tomcat:latest
