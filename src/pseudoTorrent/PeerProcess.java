package pseudoTorrent;

import host.Host;
import host.OptUnchokeTimer;
import host.UnchokeTimer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

import networking.ProtocolPackage;
import pseudoTorrent.networking.BitfieldProtocol;
import pseudoTorrent.networking.ChokeProtocol;
import pseudoTorrent.networking.HaveProtocol;
import pseudoTorrent.networking.InterestedProtocol;
import pseudoTorrent.networking.Message;
import pseudoTorrent.networking.NotInterestedProtocol;
import pseudoTorrent.networking.PieceProtocol;
import pseudoTorrent.networking.RequestProtocol;
import pseudoTorrent.networking.TorrentServer;
import pseudoTorrent.networking.TorrentSocket;
import pseudoTorrent.networking.UnchokeProtocol;

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
		new UnchokeTimer();
		new OptUnchokeTimer();
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
			String logPath = "";
			Host.setup(numPrefNeighbors, unchokeInterval, optimisticUnchokeInterval, fileSize, pieceSize, logPath);
			
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
		
		/* Initialize the ProtocolPackage */
		ProtocolPackage.addStaticProtocol(new BitfieldProtocol(), Message.Type.BITFIELD.val);
		ProtocolPackage.addStaticProtocol(new ChokeProtocol(), Message.Type.CHOKE.val);
		ProtocolPackage.addStaticProtocol(new HaveProtocol(), Message.Type.HAVE.val);
		ProtocolPackage.addStaticProtocol(new InterestedProtocol(), Message.Type.INTERESTED.val);
		ProtocolPackage.addStaticProtocol(new NotInterestedProtocol(), Message.Type.NOT_INTERESTED.val);
		ProtocolPackage.addStaticProtocol(new PieceProtocol(), Message.Type.PIECE.val);
		ProtocolPackage.addStaticProtocol(new RequestProtocol(), Message.Type.REQUEST.val);
		ProtocolPackage.addStaticProtocol(new UnchokeProtocol(), Message.Type.UNCHOKE.val);
		
		String line = null;
		try {
			int readPeerID;
			br = new BufferedReader(new FileReader("PeerInfo.cfg"));
			while ((line = br.readLine()) != null){
				StringTokenizer tokens = new StringTokenizer (line);
				readPeerID = Integer.parseInt(tokens.nextToken());
				if (hostID > readPeerID){
					String domain=tokens.nextToken();
					int port = Integer.parseInt(tokens.nextToken());
					Socket socket = new Socket(domain,port);
					TorrentSocket tsocket = new TorrentSocket(readPeerID, socket, new ProtocolPackage(), true);
					tsocket.start();
					Host.add(readPeerID, tsocket);
				}
				if(readPeerID==hostID)
				{
					tokens.nextToken();
					//start my own server
					TorrentServer ts= new TorrentServer(Integer.parseInt(tokens.nextToken()));
					Thread serverThread = new Thread(ts);
					serverThread.start();
					
					int val=Integer.parseInt(tokens.nextToken());
					Host.setFile(val);
				}
				
			}
			
			
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
    
} /* end PeerProcess class */
