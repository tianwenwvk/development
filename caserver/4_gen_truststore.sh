#!/bin/bash

if [ $# -ne 2 ]; then 
	echo "illegal number of parameters(directory cert_alias)"
	exit 0
fi

directory=$1
cert_alias=$2
# bash check if directory exists
if [ -d $directory ]; then
	echo "Directory exists, start generate certs"
else 
	echo "Directory does not exists, create new directory: $directory"
	mkdir $directory
fi

openssl x509 -in $directory/servercert.pem -out $directory/servercert.der -outform DER
keytool -import -alias $cert_alias -file $directory/servercert.der -storepass blockchain -keystore $directory/servertrust.jks
