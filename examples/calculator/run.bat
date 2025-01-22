start /k java -jar calculator-hub-host\target\calculator-hub-host-1.0.0-SNAPSHOT.jar
timeout 5 > NUL
start /k java -jar calculator-service-host\target\calculator-service-host-1.0.0-SNAPSHOT.jar
timeout 5 > NUL
start /k java -jar calculator-client-host\target\calculator-client-host-1.0.0-SNAPSHOT.jar
