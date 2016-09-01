#!/bin/bash

if [$# -ne 1]; then 
	echo "illegal number of parameters(directory)"
	exit 0
fi

directory=$1
# bash check if directory exists
if [ -d $directory ]; then
	echo "Directory exists, start generate certs"
else 
	echo "Directory does not exists, create new directory: $directory"
	mkdir $directory
fi 
#gen private key and public key signature
openssl req -newkey rsa:1024 -out $directory/serverreq.pem -keyout $directory/serverkey.pem -config openssl.cnf
#ca signed public key
openssl ca -in $directory/serverreq.pem -out $directory/servercert.pem -config openssl.cnf
openssl rsa -in $directory/serverkey.pem -out $directory/serverkeynopass.pem
