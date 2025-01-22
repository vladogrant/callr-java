FROM openjdk:17-alpine
COPY target/calculator-service-host-1.0.0-SNAPSHOT.jar .
ENTRYPOINT ["java","-jar","calculator-service-host-1.0.0-SNAPSHOT.jar","--callr.host=calculator-hub"]
