package org.bitcoinj.ssl;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerAddress;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletChangeEventListener;

public class PeerSSLTobitcoindTest {

	public static void main( String[] args ) throws BlockStoreException, UnknownHostException
	   {
		   System.setProperty("javax.net.debug", "all");
		   
		   NetworkParameters params = RegTestParams.get();
		   InetAddress addr = InetAddress.getByName("127.0.0.1");
		   int port = 18334;
	       
	       Wallet wallet = new Wallet(params);
	       ECKey key = new ECKey();
	       wallet.importKey(key);
	       System.out.println("Public address: " + key.toAddress(params).toString());
	       System.out.println("Private key: " + key.getPrivateKeyEncoded(params).toString());

	       File file = new File("my-blockchain");
	       SPVBlockStore store=new SPVBlockStore(params, file);
	       BlockChain chain = new BlockChain(params, wallet, store);
	       PeerAddress address = new PeerAddress(addr, port);
		   
	       PeerGroup peerGroup = new PeerGroup(params, chain);
	       peerGroup.addAddress(address);//.addPeerDiscovery(new DnsDiscovery(params));
	       peerGroup.addWallet(wallet);
	       peerGroup.setMaxConnections(1);
	       peerGroup.start();
	       peerGroup.downloadBlockChain();
	      
	       
//	       Peer peer = new Peer(params, chain, address, "bitcoinj", "0.14.3");
//	       peer.addWallet(wallet);
//	       peer.startBlockChainDownload();
	       
	       WalletChangeEventListener listener= new WalletChangeEventListener()
	       {
				@Override
				public void onWalletChanged(Wallet wallet) {
					// TODO Auto-generated method stub
					System.out.println("Jwalllet Balance: " + wallet.getBalance() + " satoshis");
				}
	       };
	       wallet.addChangeEventListener(listener);
	       
	       //while(true){}
	   }
}
