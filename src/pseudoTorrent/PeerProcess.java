package pseudoTorrent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.BitSet;
import java.util.StringTokenizer;

public class PeerProcess 
{
	/******************* Class Attributes *******************/
	private static int peerID;
	private static int numPrefNeighbors;
	private static int unchokeInterval;
	private static int optimisticUnchokeInterval;
	private static String fileName;
	private static int fileSize;
	private static int pieceSize;
	private static BitSet bitfield; 
	private static int numPieces = 6;
	
	
	
	/******************* Class Methods *******************/
	
	/**
	 * Creates the PeerProcess object
	 * @param peerID	The objects unique ID
	 */
	public PeerProcess(int peerID)
	{
		this.peerID = peerID;
	} /* end Constructor */
	
	public static void main(String argv[]) throws Exception {
		peerID = Integer.parseInt(argv[0]);
		loadCommonCfg();
		loadPeerInfoCfg();
	}
	
	private static void loadCommonCfg() {
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
			//TODO: set num of pieces.
			
		} 
		catch (IOException e) {e.printStackTrace();} 
		finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {ex.printStackTrace();}
		}
		
		System.out.println(numPrefNeighbors);
		System.out.println(unchokeInterval);
		System.out.println(optimisticUnchokeInterval);
		System.out.println(fileName);
		System.out.println(fileSize);
		System.out.println(pieceSize);
	}

	private static void loadPeerInfoCfg() {
		BufferedReader br = null;
		
		String line = null;
		try {
			int readPeerID;
			bitfield = new BitSet(numPieces);
			br = new BufferedReader(new FileReader("PeerInfo.cfg"));
			while ((line = br.readLine()) != null){
				StringTokenizer tokens = new StringTokenizer (line);
				readPeerID = Integer.parseInt(tokens.nextToken());
				if (peerID > readPeerID){
					//TODO:startup port to that peer
					System.out.println("Set up port to: " + readPeerID);
				}
				if (peerID == readPeerID){
					tokens.nextToken();
					tokens.nextToken();
					int hasFile = Integer.parseInt(tokens.nextToken());
					
					if (hasFile == 1) {
						for (int i = 0; i < numPieces; i++) {
							bitfield.set(i);
						}
					}
				}
			}
			
			if (bitfield.isEmpty()){
				System.out.println("Bitfield empty");
			}
			else {
				System.out.println(toString(bitfield));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    private static String toString(BitSet bs) {
        return Long.toString(bs.toLongArray()[0], 2);
    }
    
} /* end PeerProcess class */
