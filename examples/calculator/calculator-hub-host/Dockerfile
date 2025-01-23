FROM alpine/java:21-jdk
RUN apk update
RUN apk upgrade
RUN apk add busybox-extras
RUN apk add curl
COPY target/calculator-hub-host-1.0.0-SNAPSHOT.jar .
EXPOSE 9443
ENTRYPOINT ["java","-jar","calculator-hub-host-1.0.0-SNAPSHOT.jar","--callr.host=calculator-hub"]
