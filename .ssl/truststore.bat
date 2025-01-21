:: Create a truststore.jks file for the server and import the root.crt there
keytool -import -trustcacerts -noprompt -alias root -ext san=ip:127.0.0.1 -file CA\root.crt -keystore truststore\truststore.jks -deststorepass s3cr3t
