package org.bitcoinj.ssl;

import java.io.File;
import java.net.InetAddress;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerAddress;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;

public class WalletSSLTobitcoindTest extends WalletAppKit {

		public WalletSSLTobitcoindTest(NetworkParameters params, File directory, String filePrefix) {
			super(params, directory, filePrefix);
		}

		public void startUp(){
			try {
				super.startUp();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public static void main(String[] args){
			 
			//System.setProperty("javax.net.debug", "all");
			
			try {
				/* SSL Connection server*/
				InetAddress addr = InetAddress.getByName("10.6.49.66");//("127.0.0.1");//
				int port = 18334;
				
				/* Tcp Connection server*/
//				InetAddress addr = InetAddress.getByName("10.6.49.66");//("127.0.0.1");
//				int port = 18333;
		
				NetworkParameters params = RegTestParams.get();
				
				WalletSSLTobitcoindTest kit = new WalletSSLTobitcoindTest(params, new File("."), "j-wallet");
		
				kit.setPeerNodes(new PeerAddress(addr, port));

				kit.startUp();
		
				kit.awaitRunning();
				System.out.print("Jwalllet Chain: " + kit.chain().getChainHead());
				System.out.print("Jwalllet Balance: " + kit.wallet().getBalance());
			
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
	}