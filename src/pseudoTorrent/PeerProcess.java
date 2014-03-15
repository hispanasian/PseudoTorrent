package pseudoTorrent;

import host.Host;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import pseudoTorrent.networking.TorrentServer;

public class PeerProcess 
{
	/******************* Class Attributes *******************/
	private  int hostID;
	private int numPrefNeighbors;
	private int unchokeInterval;
	private int optimisticUnchokeInterval;
	private String fileName;
	private  int fileSize;
	private  int pieceSize;
	
	
	
	/******************* Class Methods *******************/
	
	/**
	 * Creates the PeerProcess object
	 * @param peerID	The objects unique ID
	 */
	public PeerProcess(int peerID)
	{
		this.hostID = peerID;
	} /* end Constructor */
	
	public static void main(String argv[]) throws Exception {
		
		int peerID = Integer.parseInt(argv[0]);
		PeerProcess peer = new PeerProcess(peerID);
		Host.setHostID(peer.hostID);
		peer.loadCommonCfg();
		peer.loadPeerInfoCfg();
		
		
	}
	
	private  void loadCommonCfg() {
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader("Common.cfg"));
			StringTokenizer tokens = new StringTokenizer (br.readLine());
			tokens.nextToken();
			numPrefNeighbors = Integer.parseInt(tokens.nextToken());
			tokens = new StringTokenizer (br.readLine());
			tokens.nextToken();
			unchokeInterval = Integer.parseInt(tokens.nextToken());
			tokens = new StringTokenizer (br.readLine());
			tokens.nextToken();
			optimisticUnchokeInterval = Integer.parseInt(tokens.nextToken());
			tokens = new StringTokenizer (br.readLine());
			tokens.nextToken();
			fileName = tokens.nextToken();
			tokens = new StringTokenizer (br.readLine());
			tokens.nextToken();
			fileSize = Integer.parseInt(tokens.nextToken());
			tokens = new StringTokenizer (br.readLine());
			tokens.nextToken();
			pieceSize = Integer.parseInt(tokens.nextToken());
			
			
			Host.setup(numPrefNeighbors, unchokeInterval, optimisticUnchokeInterval, fileSize, pieceSize);
			
		} 
		catch (IOException e) {
			//TODO:: Auto-generated catch block
			e.printStackTrace();} 
		finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {ex.printStackTrace();}
		}
		

	}

	private void loadPeerInfoCfg() {
		BufferedReader br = null;
		
		String line = null;
		try {
			int readPeerID;
			br = new BufferedReader(new FileReader("PeerInfo.cfg"));
			while ((line = br.readLine()) != null){
				StringTokenizer tokens = new StringTokenizer (line);
				readPeerID = Integer.parseInt(tokens.nextToken());
				if (hostID > readPeerID){
			
					int port = Integer.parseInt(tokens.nextToken());
					TorrentServer ts= new TorrentServer(port);
					Thread serverThread = new Thread(ts);
					serverThread.start();
		
				}
				
			}
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
    
} /* end PeerProcess class */
