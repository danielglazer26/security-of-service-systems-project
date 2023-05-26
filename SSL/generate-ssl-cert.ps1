openssl req `
    -newkey rsa:2048 `
    -x509 `
    -nodes `
    -keyout ./<key-name>.key `
    -new `
    -out ./<cert-name>.crt `
    -config ./certdef.cnf `
    -sha256 `
    -days 3650

openssl pkcs12 -export -in .\<cert-name>.crt -inkey .\<key-name>.key -out <keystore-name>.p12 -name <keystore-name>.p12

keytool -importkeystore -srckeystore .\<keystore-name>.p12 -srcstoretype PKCS12 -srcalias <keystore-name>.p12 -srcstorepass password -destkeystore keystore -deststoretype PKCS12 -destalias <keystore-name> -deststorepass password
