package org.bitcoinj.keys;

import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class TransactionKeyHandler {

	private static final org.slf4j.Logger log = LoggerFactory.getLogger(TransactionKeyHandler.class);
	
	public static Gson gson = new Gson();
	
	public static String get(TransactionKeyItem request) {
		
		if (request == null || request.getTX_ID() == null)
			return null;
		
		try {
			MutualAuthenticationHttps https = new MutualAuthenticationHttps();

			String response = https.sendRequest(gson.toJson(request));

			if (response != null) {
				TransactionKeyItem item = gson.fromJson(response, TransactionKeyItem.class);
				return item.getKey();
			}
		} catch (Exception e) {
			log.error("Faild request " + gson.toJson(request));
		}
		return null;
	}
	
	public static boolean set(TransactionKeyItem request){
		
		if (request.getTX_ID() == null || request.getKey() == null) {
			log.error("Invalid request " + gson.toJson(request));
			return false;
		}
		
		try {
			MutualAuthenticationHttps https = new MutualAuthenticationHttps();
			String response = https.sendRequest(gson.toJson(request));
			if (response != null) {
				TransactionKeyItem item = gson.fromJson(response, TransactionKeyItem.class);
				if (item.getStatus()!= null && item.getStatus())
					return true;
			}
		} catch (Exception e) {
			log.error("Faild request " + gson.toJson(request));
		}
		return false;		
	}
	
	
}
