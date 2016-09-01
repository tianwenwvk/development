package org.bitcoinj.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.LoggerFactory;

/**
 * New Properties file loader.
 *
 */
public class PropertiesReader {

	private static final org.slf4j.Logger log = LoggerFactory.getLogger(PropertiesReader.class);
    private static final String settings_loc = "bitcoinj.conf";
    private static final Properties props = new Properties();

    static {
    	try {
	    	Path path = Paths.get(settings_loc);
	    	if (Files.exists(path))
	    	{
	    		try (FileInputStream fis = new FileInputStream(settings_loc)) {       	
	                props.load(fis);
	            } catch (IOException ex) {
	                log.error("IOException while reading properties, exiting", ex);
	                System.exit(-1);
	            }
	    	}
    	} catch(InvalidPathException ipe){
    		log.error("IOException while reading properties, exiting", ipe);
    	}
    }

    /**
     * Get the enabled protocols to be used with a
     * {@link javax.net.ssl.SSLEngine}.
     *
     * @return the enabled protocols to be used with a
     * {@link javax.net.ssl.SSLEngine}.
     */
    public static String[] getProtocols(String[] default_value) {
        String[] ret = getPropAsStrArr("secure.protocols", default_value);
        return ret;
    }

    /**
     * Get the enabled cipher suites to be used with a
     * {@link javax.net.ssl.SSLEngine}.
     *
     * @return the enabled cipher suites to be used with a
     * {@link javax.net.ssl.SSLEngine}.
     */
    public static String[] getCipherSuites(String[] default_value) {
        String[] ret = getPropAsStrArr("secure.cipherSuites", default_value);

        return ret;
    }

    public static boolean getUsingSSL(boolean default_value) {
        return getPropAsBool("secure.ssl", default_value);
    }

    public static String getTrustStoreLoc() {
        return getProp("trustStoreLoc");
    }

    public static String getKeyStoreLoc() {
        return getProp("keyStoreLoc");
    }
    
    public static String getTrustStorePassPhrase() {
        return getProp("trustStorePassPhrase");
    }

    public static String getKeyStorePassPhrase() {
        return getProp("keyStorePassPhrase");
    }
    
    public static String getKeyAlias() {
        return getProp("alias");
    }
    
    /**
     * Return a property as a String array.
     *
     * @param key The key used to retrieve the property
     * @return the returned String array
     */
    private static String[] getPropAsStrArr(String key, String[] default_value) {
        String str = getProp(key);
        if (str==null){
        	log.warn(key + " value is invalid: "+ str +", return default value.");
        	return default_value;
        } else {
	        String[] ret = str.split(" ");
	        return ret;
        }
    }

    /**
     * Return a property as a long. An error is thrown if the number is smaller
     * than zero.
     *
     * @param key The key used to retrieve the property
     * @return the returned long
     */
    private static long getPropAsLong(String key, long default_value) {
        String str = getProp(key);
        if (str == null){
        	 log.warn(key + " value is invalid: "+ str +", return default value.");
             return default_value;
        } else {
        	long l = -1;
	        try {
	            l = Long.parseLong(str);
	        } catch (NumberFormatException nfe) {
	            log.warn(key + " value is invalid: "+ str +", return default value.");
	            return default_value;
	        }
	        return l;
        }
    }

    /**
     * Return a property as an int. An error is thrown if the number is smaller
     * than zero.
     *
     * @param key The key used to retrieve the property
     * @return the returned int
     */
    private static int getPropAsInt(String key, int default_value) {
        String str = getProp(key);
        if(str == null){
        	log.warn(key + " value is invalid: "+ str +", return default value.");
	        return default_value;
        } else {
	        int i = -1;
	        try {
	            i = Integer.parseInt(str);
	        } catch (NumberFormatException nfe) {
	            log.warn(key + " value is invalid: "+ str +", return default value.");
	            return default_value;
	        }
	        return i;
        }
    }

    /**
     * Return a property as a boolean. An error is thrown if the values are
     * anything other than "true" or "false".
     *
     * @param key The key used to retrieve the property
     * @return the returned boolean
     */
    private static boolean getPropAsBool(String key, boolean default_value) {
        String str = getProp(key);
        if (str == null || (!str.equals("true") && !str.equals("false"))) {
            log.warn(key+" value is invalid: "+ str +", return default value");
            return default_value;
        }
        return Boolean.parseBoolean(str);
    }

    /**
     * Get a property. An error is thrown if the value of the property is not
     * found or is empty.
     *
     * @param key The key used to retrieve the property
     * @return the associated property
     */
    private static String getProp(String key) {
        String str = props.getProperty(key);
        if (str == null || str.isEmpty()) {
            log.warn(key+ " value not found, return default value.");
            return null;
        }
        return str;
    }

    private static String getPropUsingDefault(String key, String default_value) {
        String str = getProp(key);
        if (str==null){
        	log.warn(key + " value is invalid: "+ str +", return default value.");
        	return default_value;
        } else {
	        return str;
        }
    }
    
    private PropertiesReader() {
    	
    }
}
