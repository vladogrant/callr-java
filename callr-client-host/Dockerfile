FROM openjdk:17-alpine
COPY target/callr-client-host-1.0.0-SNAPSHOT.jar .
ENTRYPOINT ["java","-jar","callr-client-host-1.0.0-SNAPSHOT.jar","--callr.host=callr-hub"]
#ENTRYPOINT ["/bin/sh"]
