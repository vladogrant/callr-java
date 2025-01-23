FROM alpine/java:21-jdk
RUN apk update
RUN apk upgrade
RUN apk add busybox-extras
RUN apk add curl
COPY target/callr-hub-host-1.0.0-SNAPSHOT.jar .
EXPOSE 8443
ENTRYPOINT ["java","-jar","callr-hub-host-1.0.0-SNAPSHOT.jar","--callr.host=callr-hub"]
