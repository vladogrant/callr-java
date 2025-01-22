#!/bin/bash
start /k java -jar callr-example-calculator-hub-host\target\callr-example-calculator-hub-host-1.0.0-SNAPSHOT.jar
timeout 5 > NUL
start /k java -jar callr-example-calculator-service-host\target\callr-example-calculator-service-host-1.0.0-SNAPSHOT.jar
timeout 5 > NUL
start /k java -jar callr-example-calculator-client-host\target\callr-example-calculator-client-host-1.0.0-SNAPSHOT.jar
