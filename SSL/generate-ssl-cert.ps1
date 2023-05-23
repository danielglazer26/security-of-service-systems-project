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