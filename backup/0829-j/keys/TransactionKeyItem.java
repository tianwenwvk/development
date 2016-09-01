package org.bitcoinj.keys;

import org.slf4j.LoggerFactory;

public class TransactionKeyItem {

	private static final org.slf4j.Logger log = LoggerFactory.getLogger(TransactionKeyItem.class);
	
	/* Possible to extend more fields if need */
	private String TX_ID;
	private String key;
	private String AC_List;
	private Boolean status;
	
	public String getTX_ID() {
		return TX_ID;
	}
	public void setTX_ID(String tX_ID) {
		TX_ID = tX_ID;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getAC_List() {
		return AC_List;
	}
	public void setAC_List(String aC_List) {
		AC_List = aC_List;
	}
	
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "TransactionKeyItem [TX_ID=" + TX_ID + ", key=" + key + ", AC_List=" + AC_List + ", status=" + status
				+ "]";
	}
	
	
	
}
