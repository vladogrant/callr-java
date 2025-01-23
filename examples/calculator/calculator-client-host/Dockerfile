FROM alpine/java:21-jdk
RUN apk update
RUN apk upgrade
RUN apk add busybox-extras
RUN apk add curl
COPY target/calculator-client-host-1.0.0-SNAPSHOT.jar .
ENTRYPOINT ["java","-jar","calculator-client-host-1.0.0-SNAPSHOT.jar","--callr.host=calculator-hub"]
