package org.bitcoinj.keys;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
 
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;

import org.bitcoinj.utils.PropertiesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MutualAuthenticationHttps {
    
	private static final Logger log = LoggerFactory.getLogger(MutualAuthenticationHttps.class);
	
	private static String url = "https://keyserver.local/";//format(request)//TODO
	
	private static String keyStoreFileName = PropertiesReader.getKeyStoreLoc();
	private static String keyStorePassword = PropertiesReader.getKeyStorePassPhrase();
	private static String trustStoreFileName = PropertiesReader.getTrustStoreLoc();
	private static String trustStorePassword = PropertiesReader.getTrustStorePassPhrase();
	private static String keyAlias = PropertiesReader.getKeyAlias();
    
    public String sendRequest(String request) {
        String response = null;
        try {
            KeyManager[] keyManagers = createKeyManagers(keyStoreFileName, keyStorePassword, keyAlias);
            TrustManager[] trustManagers = createTrustManagers(trustStoreFileName, trustStorePassword);
            //init context 
            SSLSocketFactory factory = initSSLContext(keyManagers, trustManagers);
            //get the url and return content
            response = request(url, factory);
 
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return response;
    }
 
    private String request(String urlString, SSLSocketFactory sslSocketFactory) {
        String response = null;
        HttpURLConnection connection = null;
       
        try {
	    	URL url = new URL(urlString);
	        connection = (HttpURLConnection) url.openConnection();
	        if (connection instanceof HttpsURLConnection) {
	            ((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
	        }
	
	        int status = connection.getResponseCode();
	        switch (status) {
	            case 200:
	            case 201:
	                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	                StringBuilder sb = new StringBuilder();
	                String line;
	                while ((line = br.readLine()) != null) {
	                    sb.append(line+"\n");
	                }
	                br.close();
	                response = sb.toString();
	        }
	      }catch(Exception e){
	        	
	      }finally {
            if (connection != null) {
                try {
                	connection.disconnect();
                } catch (Exception ex) {
                   log.error("", ex);
                }
             }
          }
        return response;
    }
 
    private static SSLSocketFactory initSSLContext(KeyManager[] keyManagers, TrustManager[] trustManagers)
        throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext context = SSLContext.getInstance("TLS");

        context.init(keyManagers, trustManagers, null);
        SSLSocketFactory socketFactory = context.getSocketFactory();
        return socketFactory;
    }
 
    private static KeyManager[] createKeyManagers(String keyStoreFileName, String keyStorePassword, String alias)
        throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {

        InputStream inputStream = new java.io.FileInputStream(keyStoreFileName);

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(inputStream, keyStorePassword == null ? null : keyStorePassword.toCharArray());

        KeyManager[] managers;
        if (alias != null) {
            managers =
                new KeyManager[] {
                     new MutualAuthenticationHttps().new AliasKeyManager(keyStore, alias, keyStorePassword)};
        } else {
            //create keymanager factory and load the keystore object in it 
            KeyManagerFactory keyManagerFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyStorePassword == null ? null : keyStorePassword.toCharArray());
            managers = keyManagerFactory.getKeyManagers();
        }

        return managers;
    }
 
    private static TrustManager[] createTrustManagers(String trustStoreFileName, String trustStorePassword)
        throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

        java.io.InputStream inputStream = new java.io.FileInputStream(trustStoreFileName);

        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(inputStream, trustStorePassword == null ? null : trustStorePassword.toCharArray());

        TrustManagerFactory trustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        return trustManagerFactory.getTrustManagers();
    }
 
    private class AliasKeyManager implements X509KeyManager {
 
        private KeyStore _ks;
        private String _alias;
        private String _password;
 
        public AliasKeyManager(KeyStore ks, String alias, String password) {
            _ks = ks;
            _alias = alias;
            _password = password;
        }
 
        public String chooseClientAlias(String[] str, Principal[] principal, Socket socket) {
            return _alias;
        }
 
        public String chooseServerAlias(String str, Principal[] principal, Socket socket) {
            return _alias;
        }
 
        public X509Certificate[] getCertificateChain(String alias) {
            try {
                java.security.cert.Certificate[] certificates = this._ks.getCertificateChain(alias);
                if(certificates == null){throw new FileNotFoundException("no certificate found for alias:" + alias);}
                X509Certificate[] x509Certificates = new X509Certificate[certificates.length];
                System.arraycopy(certificates, 0, x509Certificates, 0, certificates.length);
                return x509Certificates;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
 
        public String[] getClientAliases(String str, Principal[] principal) {
            return new String[] { _alias };
        }
 
        public PrivateKey getPrivateKey(String alias) {
            try {
                return (PrivateKey) _ks.getKey(alias, _password == null ? null : _password.toCharArray());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
 
        public String[] getServerAliases(String str, Principal[] principal) {
            return new String[] { _alias };
        }
 
    }
}