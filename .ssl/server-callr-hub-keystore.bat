:: Create server certificate signing request (CSR):
openssl req -new -newkey rsa:4096 -keyout keystore\server\callr-hub\callr-hub.key -out keystore\server\callr-hub\callr-hub.csr -subj "/C=LH/ST=/L=/O=/OU=/CN=callr-hub" -passout pass:s3cr3t

:: Sign the request with our root.crt CA certificate and its private key
openssl x509 -req -CA CA\root.crt -CAkey CA\root.key -in keystore\server\callr-hub\callr-hub.csr -out keystore\server\callr-hub\callr-hub.crt -days 3650 -CAcreateserial -extfile keystore\server\callr-hub\callr-hub.ext -passin pass:s3cr3t

:: Pint our certificate details
openssl x509 -in keystore\server\callr-hub\callr-hub.crt -text

:: Import the signed certificate and the corresponding private key to the keystore
openssl pkcs12 -export -out keystore\server\callr-hub\callr-hub.p12 -name "callr-hub" -inkey keystore\server\callr-hub\callr-hub.key -in keystore\server\callr-hub\callr-hub.crt -passin pass:s3cr3t -passout pass:s3cr3t

:: Create a keystore.jks repository and import the callr-hub.p12
keytool -importkeystore -srckeystore keystore\server\callr-hub\callr-hub.p12 -srcstoretype PKCS12 -srcstorepass s3cr3t -destkeystore keystore\server\callr-hub\callr-hub-keystore.jks -deststoretype JKS -deststorepass s3cr3t
