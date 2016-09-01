#!/bin/bash
######gen ca ("self-signed Root CA")
openssl genrsa -des3 -out ./CA/private/rootCAkey.pem 2048
openssl req -new -days 3650 -x509 -key ./CA/private/rootCAkey.pem -out ./CA/rootCA.pem -config openssl.cnf
