#!/bin/bash
if [ $# -ne 1 ]; then
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

######gen server private key and store in keystore
keytool -genkey -alias serverkey -validity 365 -keyalg RSA -keysize 1024 -keystore $directory/serverkey.jks -storepass blockchain -keypass blockchain
######extract server req
keytool -certreq -alias serverkey -sigalg SHA1withRSA -file $directory/servercert.csr -storepass blockchain -keypass blockchain -keystore $directory/serverkey.jks
######server certificate
openssl ca -in $directory/servercert.csr -out $directory/servercert.crt -cert ./CA/rootCA.pem -keyfile ./CA/private/rootCAkey.pem -notext -config openssl.cnf

######format
openssl x509 -in $directory/servercert.crt -out $directory/servercert.der -outform DER 
openssl x509 -in ./CA/rootCA.pem -out ./CA/rootCA.der -outform DER 

#####import keystore
keytool -import -v -trustcacerts -alias ca_root -file ./CA/rootCA.der -storepass blockchain -keystore $directory/serverkey.jks
keytool -import -v -alias serverkey -file $directory/servercert.der -storepass blockchain -keystore $directory/serverkey.jks

####import truststore
keytool -import -alias ca_root -file ./CA/rootCA.der -storepass blockchain -keystore $directory/servertrust.jks

