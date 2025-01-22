FROM openjdk:17-alpine
COPY target/callr-hub-host-1.0.0-SNAPSHOT.jar .
EXPOSE 8443
ENTRYPOINT ["java","-jar","callr-hub-host-1.0.0-SNAPSHOT.jar","--callr.host=callr-hub"]
