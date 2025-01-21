:: Create server certificate signing request (CSR):
openssl req -new -newkey rsa:4096 -keyout keystore\server\localhost\localhost.key -out keystore\server\localhost\localhost.csr -subj "/C=LH/ST=/L=/O=/OU=/CN=localhost" -passout pass:s3cr3t

:: Sign the request with our root.crt CA certificate and its private key
openssl x509 -req -CA CA\root.crt -CAkey CA\root.key -in keystore\server\localhost\localhost.csr -out keystore\server\localhost\localhost.crt -days 3650 -CAcreateserial -extfile keystore\server\localhost\localhost.ext -passin pass:s3cr3t

:: Pint our certificate details
openssl x509 -in keystore\server\localhost\localhost.crt -text

:: Import the signed certificate and the corresponding private key to the keystore
openssl pkcs12 -export -out keystore\server\localhost\localhost.p12 -name "localhost" -inkey keystore\server\localhost\localhost.key -in keystore\server\localhost\localhost.crt -passin pass:s3cr3t -passout pass:s3cr3t

:: Create a keystore.jks repository and import the localhost.p12
keytool -importkeystore -srckeystore keystore\server\localhost\localhost.p12 -srcstoretype PKCS12 -srcstorepass s3cr3t -destkeystore keystore\server\localhost\keystore.jks -deststoretype JKS -deststorepass s3cr3t
