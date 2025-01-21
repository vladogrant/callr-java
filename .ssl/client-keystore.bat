if not exist "keystore\client\%1\" mkdir "keystore\client\%1\"
:: Crate a client certificate signing request:
openssl req -new -newkey rsa:4096 -nodes -keyout keystore\client\%1\%1.key -out keystore\client\%1\%1.csr -subj "/C=/ST=/L=/O=/OU=/CN=%1" -passout pass:s3cr3t

:: Sign the request with our CA:
openssl x509 -req -CA CA\root.crt -CAkey CA\root.key -in keystore\client\%1\%1.csr -out keystore\client\%1\%1.crt -days 3650 -CAcreateserial -passin pass:s3cr3t

:: Package the signed certificate and the private key into the PKCS file:
openssl pkcs12 -export -inkey keystore\client\%1\%1.key -in keystore\client\%1\%1.crt -out keystore\client\%1\keystore.p12 -name "%1" -passout pass:s3cr3t

:: Create a keystore.jks repository and import the PKCS file
keytool -importkeystore -srckeystore keystore\client\%1\%1.p12 -srcstoretype PKCS12 -srcstorepass s3cr3t -destkeystore keystore\client\%1\%1.jks -deststoretype JKS -deststorepass s3cr3t
