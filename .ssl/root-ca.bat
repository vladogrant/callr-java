:: Create our own self-signed root CA certificate
if not exist CA\ (
  mkdir CA
)
openssl req -x509 -sha256 -days 3650 -newkey rsa:4096 -keyout CA\root.key -out CA\root.crt -subj "/C=/ST=/L=/O=/OU=/CN=root" -passout pass:s3cr3t
