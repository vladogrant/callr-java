FROM openjdk:17-alpine
COPY target/calculator-hub-host-1.0.0-SNAPSHOT.jar .
EXPOSE 9443
ENTRYPOINT ["java","-jar","calculator-hub-host-1.0.0-SNAPSHOT.jar","--callr.host=calculator-hub"]
