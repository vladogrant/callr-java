FROM openjdk:17-alpine
COPY target/calculator-client-host-1.0.0-SNAPSHOT.jar .
ENTRYPOINT ["java","-jar","calculator-client-host-1.0.0-SNAPSHOT.jar","--callr.host=calculator-hub"]
