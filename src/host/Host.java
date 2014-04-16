package host;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import filechunk.ByteReadAndWrite;
import pseudoTorrent.TorrentLogger;
import pseudoTorrent.networking.*;


/**
 * The tracker will handle keeping track of various static information for a peer process.
 * It mainly will provide a way of looking up various information for connected peers through a lookup table.
 * It also will store the optimistically unchoked peer, and an array list of the top k peers to be unchoked.
 * It works in conjunction with 2 timers - one for unchoking the top k peers every p seconds, and 
 * one for unchoking a optimistically selected peer every m seconds.
 * 
 * @author Terek Arce
 *
 */

// TODO: Could be a problem with optimisticUnchokedPeer.  If not enough time passes, it will be -1.

public class Host 
{

	/******************* Class Attributes *******************/
	protected static int hostID;

	public static Hashtable<Integer, HostEntry> lookup;

	public static int numPieces;
	protected static int numPrefNeighbors;
	protected static int unchokeInterval;
	protected static int optimisticUnchokeInterval;	
	protected static ArrayList <PeerRankEntry> AllRank;
	protected static ArrayList <PeerRankEntry> Choked;

	protected static ArrayList <Integer>  UnchokedTopK;
	protected static ArrayList <Integer>	ChokedInterested;
	protected static int optimisticUnchokedPeer;
	public static ArrayList <String>  allfiles; //added by kiran
	
	

	protected static BitSet bitfield;
	protected static BitSet randBitfield;
	public static TorrentLogger log;
	public static ByteReadAndWrite file;
	public static TorrentServer server;

	public static OptUnchokeTimer optUnchokeTimer;
	public static UnchokeTimer unchokeTimer;
	public static boolean terminated;

	/******************* Class Methods *******************/

	private Host () {}

	/**
	 * Setup a Tracker.  This should be created once at the start of main for a peer process.
	 * 
	 * @param numPrefNeighbors				the number of preferred neighbors as specified in Common.cfg
	 * @param unchokeInterval				the unchoke interval as specified in Common.cfg
	 * @param optimisticUnchokeInterval		the optimistic unchoke interval as specified in Common.cfg
	 * @param fileSize						the file size as specified in Common.cfg
	 * @param pieceSize						the piece size as specified in Common.cfg
	 * 
	 */
	public static synchronized void setup (String filename,int HostID, int numPrefNeighbors, int unchokeInterval, int optimisticUnchokeInterval, int fileSize, int pieceSize, String logPath)
	{
		System.out.println(Thread.currentThread().getId() + " enter setup");
		Host.lookup = new Hashtable<Integer, HostEntry>();
		//Host.numPieces = fileSize / pieceSize;
		//if ((fileSize % pieceSize) != 0) {numPieces++;}
		Host.numPieces = 100;
		Host.numPrefNeighbors = numPrefNeighbors;
		Host.unchokeInterval = unchokeInterval;
		Host.optimisticUnchokeInterval = optimisticUnchokeInterval;
		Host.UnchokedTopK = new ArrayList <Integer> ();
		Host.ChokedInterested = new ArrayList <Integer> ();
		Host.Choked = new ArrayList <PeerRankEntry> ();
		Host.AllRank = new ArrayList <PeerRankEntry> ();
		Host.optimisticUnchokedPeer = -1;
		Host.hostID = HostID;
		Host.bitfield = new BitSet(numPieces);
		Host.randBitfield = new BitSet(numPieces);
		Host.file= new ByteReadAndWrite(filename, pieceSize);
		Host.terminated = false;

		try {
			Host.log = new TorrentLogger(hostID, logPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getId() + " exit setup");
	} /* end Constructor */

	public static void addUnchokeTimer (UnchokeTimer t) {
		System.out.println(Thread.currentThread().getId() + " enter unchokeTimer");
		Host.unchokeTimer = t;
		System.out.println(Thread.currentThread().getId() + " exit unchokeTimer");
	}

	public static void addOptUnchokeTimer (OptUnchokeTimer t) {
		System.out.println(Thread.currentThread().getId() + " enter addOptUnchokeTimer");
		Host.optUnchokeTimer = t;
		System.out.println(Thread.currentThread().getId() + " exit addOptUnchokeTimer");
	}

	/**
	 * Returns the host's ID
	 * 
	 */
	public static int getID (){
		System.out.println(Thread.currentThread().getId() + " enter/exit getID");
		return Host.hostID;
	}

	/**
	 * Allows the host's ID to be set.  This should only be called once at the start of PeerProcess.
	 * 
	 * @param hostID	the host id of the process
	 * 
	 */
	public static void setHostID (int hostID) {
		System.out.println(Thread.currentThread().getId() + " enter setHostID");
		Host.hostID = hostID; 
		System.out.println(Thread.currentThread().getId() + " exit setHostID");
	}

	/**
	 * Returns the host's bitfield
	 * 
	 */	
	public static synchronized BitSet getHostBitfield() {
		System.out.println(Thread.currentThread().getId() + " enter/exit getHostBitfield");
		return Host.bitfield;
	}

	/**
	 * Chokes the peer associated with peerID.
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */
	public static synchronized void choke (int peerID) 
	{
		System.out.println(Thread.currentThread().getId() + " enter choke");
		Host.lookup.get(peerID).choked = true;
		System.out.println(Thread.currentThread().getId() + " exit choke");
	}

	/**
	 * Unchokes the peer associated with the peerID
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */
	public static synchronized void unchoke (int peerID) 
	{
		System.out.println(Thread.currentThread().getId() + "enter unchoke");
		Host.lookup.get(peerID).choked = false;
		System.out.println(Thread.currentThread().getId() + "exit unchoke");
	}

	/**
	 * Returns if the peer associated with the peerID is choked or not
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */	
	public static synchronized boolean peerIsChoked (int peerID) {
		System.out.println(Thread.currentThread().getId() + "enter/exit peerIsChoked ");
		if (Host.lookup.isEmpty()) {return true;} //TODO: do I need this?
		return Host.lookup.get(peerID).choked;
	}

	/**
	 * Allows the user to add a peer and associated socket to the lookup map.  
	 * 
	 * @param peerID	the peer id of the connected peer
	 * @param socket	the socket the peer is using
	 * 
	 */
	public static synchronized void add(int peerID, final TorrentSocket socket)
	{
		System.out.println(Thread.currentThread().getId() + "enter add ");
		Host.lookup.put(peerID, new HostEntry(socket, numPieces));
		System.out.println(Thread.currentThread().getId() + "exit add ");
	}

	/**
	 * Allows the host to set its bitfield to either having the file (all 1s/true)
	 * or not having the file (all 0s/false)  
	 * 
	 * @param hasFile	the value 1 represents the host has the file, 0 otherwise
	 * 
	 */
	public static void setFile(int hasFile) {
		System.out.println(Thread.currentThread().getId() + " enter setFile");
		if (hasFile == 1) {
			Host.bitfield.clear();
			Host.bitfield.flip(0, Host.numPieces);
		}
		else {
			//TODO: error
		}
		System.out.println(Thread.currentThread().getId() + " exit setFile");
	}

	/**
	 * Updates the bitfield record for a given peer using the piece info.
	 * This is overloaded.
	 * 
	 * @param peerID	the peer id of the peer
	 * @param piece		the piece that needs to be updated in the bitfield
	 * 
	 */
	public static synchronized void updatePeerBitfield (int peerID, int chunkID) 
	{
		System.out.println(Thread.currentThread().getId() + " enter updatePeerBitfield");
		Host.lookup.get(peerID).bitfield.set(chunkID);
		System.out.println(Thread.currentThread().getId() + " Bitfield of peer: " + peerID + " is " + Host.lookup.get(peerID).bitfield.toString());

		Iterator<Entry<Integer, HostEntry>> it = Host.lookup.entrySet().iterator();
        while (it.hasNext()) {
        	Map.Entry<Integer, HostEntry> entry = (Map.Entry<Integer, HostEntry>)it.next();
        	BitSet result = Host.compare(entry.getValue().bitfield, Host.bitfield);
        	if (result.isEmpty()) {
        		entry.getValue().hostInterested = false;
        	}
        	else {
        		entry.getValue().hostInterested = true;
        	}
        }
        System.out.println(Thread.currentThread().getId() + " exit updatePeerBitfield");
	}

	/**
	 * Updates the bitfield record for a given peer using the piece info.
	 * This is overloaded
	 * 
	 * @param peerID	the peer id of the peer
	 * @param bitfield	the bitset the bitfield of the peer will be set to
	 * 
	 */
	public static synchronized void updatePeerBitfield (int peerID, BitSet bitfield) {

		System.out.println(Thread.currentThread().getId() + " enter updatePeerBitfield-bitfield");
		Host.lookup.get(peerID).bitfield = bitfield;

		Iterator<Entry<Integer, HostEntry>> it = Host.lookup.entrySet().iterator();
        while (it.hasNext()) {
        	Map.Entry<Integer, HostEntry> entry = (Map.Entry<Integer, HostEntry>)it.next();
        	BitSet result = Host.compare(entry.getValue().bitfield, Host.bitfield);
        	if (result.isEmpty()) {
        		entry.getValue().hostInterested = false;
        	}
        	else {
        		entry.getValue().hostInterested = true;
        	}
        }
        System.out.println(Thread.currentThread().getId() + " exit updatePeerBitfield-bitfield");
	}

	/**
	 * Sets the host to be choked by this peer
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */	
	public static synchronized void chokedBy (int peerID) 
	{
		System.out.println(Thread.currentThread().getId() + " enter updatePeerBitfield-bitfield");
		Host.lookup.get(peerID).choking = true;
		System.out.println(Thread.currentThread().getId() + " exit updatePeerBitfield-bitfield");
	}

	/**
	 * Sets the host to be unchoked by this peer
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */	
	public static synchronized void unchokedBy (int peerID) 
	{
		System.out.println(Thread.currentThread().getId() + " enter unchokedBy");
		Host.lookup.get(peerID).choking = false;
		System.out.println(Thread.currentThread().getId() + " exit unchokedBy");
	}

	/**
	 * Returns true/false for whether or not the peer associated with the peerID is choking the host.
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */
	public static synchronized boolean peerIsChoking (int peerID) 
	{
		System.out.println(Thread.currentThread().getId() + " enter/exit peerIsChoking");
		return Host.lookup.get(peerID).choking;
	}

	/**
	 * Sets the field to let the host know the peer is interested in the its pieces.
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */
	public static synchronized void peerIsInterested (int peerID) 
	{
		System.out.println(Thread.currentThread().getId() + " enter peerIsInterested");
		Host.lookup.get(peerID).peerInterested = true;
		System.out.println(Thread.currentThread().getId() + " exit peerIsInterested");
	}

	/**
	 * Sets the field to let the host know the peer is NOT interested in its pieces.
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */
	public static synchronized void peerIsNotInterested (int peerID) 
	{
		System.out.println(Thread.currentThread().getId() + " enter peerIsNotInterested");
		Host.lookup.get(peerID).peerInterested = false;
		System.out.println(Thread.currentThread().getId() + " exit peerIsNotInterested");
	}

	/**
	 * Returns whether or not the host is interested in the peer pieces.
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */
	public static synchronized boolean isInterested (int peerID) {
		System.out.println(Thread.currentThread().getId() + " enter/exit isInterested");
		return Host.lookup.get(peerID).hostInterested;
	}

	/**
	 * Sets the field to let the host know the peer is NOT interested in its pieces.
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */
	public static ArrayList<TorrentSocket> getSocketList () {
		System.out.println(Thread.currentThread().getId() + " enter getSocketList");
		ArrayList<TorrentSocket> result = new ArrayList <TorrentSocket>();

		System.out.print("Socket list: ");
		Iterator<Entry<Integer, HostEntry>> it = Host.lookup.entrySet().iterator();
        while (it.hasNext()) {
        	Map.Entry<Integer, HostEntry> entry = (Map.Entry<Integer, HostEntry>)it.next();
        	result.add(entry.getValue().socket);
        	System.out.print(entry.getKey() + " ");
        }
        System.out.println();
		System.out.println(Thread.currentThread().getId() + " exit getSocketList");
		return result;
	}

	/**
	 * Updates the bitfield record for the host using the piece info.
	 * 
	 * @param chunkID	the chunk id of the piece sent by the peer
	 * @param peerID	the peer id of the peer that is sending the piece
	 * 
	 */
	public static synchronized void updatePiece (int chunkID, int peerID) {
		System.out.println(Thread.currentThread().getId() + " enter updatePiece");
		//System.out.println(Thread.currentThread().getId() + " Host.updatePiece: start");
		Host.bitfield.set(chunkID);
		//System.out.println(Thread.currentThread().getId() + " Host.updatePiece: set bitfield for " + chunkID + " from " + peerID);
		Host.lookup.get(peerID).bitsReceived += 1;
		//System.out.println(Thread.currentThread().getId() + " Host.update: incremented bits received for " + peerID);
		Host.randBitfield.set(chunkID);
		//System.out.println(Thread.currentThread().getId() + " Host.update: finished random set");
		System.out.println(Thread.currentThread().getId() + " exit updatePiece");
	}

	/**
	 * Returns whether or not this host believes everyone, including itself has the file.
	 * 
	 */
	public static synchronized boolean everyoneHasFile() 
	{
		System.out.println(Thread.currentThread().getId() + " enter everyoneHasFile");
		boolean result = true;

		if (bitfield.cardinality() < Host.numPieces) {
			result = false;
		}
		else {
			Iterator<Entry<Integer, HostEntry>> it = Host.lookup.entrySet().iterator();
	        while (it.hasNext()) {
	        	Map.Entry<Integer, HostEntry> entry = (Map.Entry<Integer, HostEntry>)it.next();
	        	BitSet toTest = (BitSet) entry.getValue().bitfield.clone();
	        	if (toTest.cardinality() < Host.numPieces) {
	        		result = false;
	        		break;
	        	}
	        }
		}	
		System.out.println(Thread.currentThread().getId() + " exit everyoneHasFile");
		return result;
	}

	public static synchronized int getRandomChunkID(int peerID) 
	{

		System.out.println(Thread.currentThread().getId() + " enter getRandChunk");
		
		BitSet hostBitfield = (BitSet) bitfield.clone();
		BitSet peerBitfield = (BitSet) lookup.get(peerID).bitfield.clone();
		BitSet randomBitfield = (BitSet) randBitfield.clone();

		hostBitfield.flip(0, hostBitfield.size());
		randomBitfield.flip(0,randomBitfield.size());

		hostBitfield.and(peerBitfield);
		hostBitfield.and(randomBitfield);

		int result = -1;
		if (!hostBitfield.isEmpty()) {
			while (true) {
			      Random randomNum = new Random();
			      int index = randomNum.nextInt(Host.numPieces);
			      if (hostBitfield.get(index) == true) {
			    	  result = index;
			    	  randBitfield.set(index);
			    	  break;
			    	  //set other bitset to true at index
			      }
			}
		}

		
		System.out.println(Thread.currentThread().getId() + " exit getRandChunk");
		return result;		
	}

	/**
	 * Allows the host to unset a bit in the random bitfield for use with getRandomChunk.
	 * 
	 * @param chunkID	the chunkID of the piece that should be unset in the random bitfield
	 * 
	 */
	public static synchronized void unsetRandomChunk(int chunkID) {
		System.out.println(Thread.currentThread().getId() + " enter unsetRandChunk");
		randBitfield.set(chunkID, false);
		System.out.println(Thread.currentThread().getId() + " exit unsetRandChunk");
	}

	/**
	 * Updates the AllRank, UnchokedTopK and Choked array list.  These lists provide the user with access
	 * to which peers are to be unchoked for a give k time interval and are utilized in determining
	 * the optimistic unchoked peer.  This method should only be used by the associated UnchokeTask for timing
	 * purposes.
	 * 
	 */
	protected static synchronized void updateTopK () {
		System.out.println(Thread.currentThread().getId() + " enter updateTopK");
		Host.AllRank.clear();
		Host.UnchokedTopK.clear();
		Host.Choked.clear();
		Host.ChokedInterested.clear();
        
		Iterator<Entry<Integer, HostEntry>> it = Host.lookup.entrySet().iterator();
        while (it.hasNext()) {
        	Map.Entry<Integer, HostEntry> entry = (Map.Entry<Integer, HostEntry>)it.next();
        	Host.AllRank.add(new PeerRankEntry(entry.getKey(), entry.getValue().bitsReceived, entry.getValue().peerInterested));
        }

        Collections.sort(Host.AllRank, PeerRankEntry.DESCENDING_COMPARATOR);
        
        int i = 0;
        int j = 0;
        for (PeerRankEntry e : Host.AllRank) {
        	if ((i < Host.numPrefNeighbors) && (e.isInterested)){
        		Host.UnchokedTopK.add(e.peerID);
        		i++;
        		j++;
        	}
        	else {
        		Host.Choked.add(Host.AllRank.get(j));
        		if (e.isInterested) {
            		Host.ChokedInterested.add(e.peerID);
        		}
        		j++;
        	}
        }
        
		Iterator<Entry<Integer, HostEntry>> it2 = Host.lookup.entrySet().iterator();
        while (it2.hasNext()) {
        	Map.Entry<Integer, HostEntry> entry = (Map.Entry<Integer, HostEntry>)it2.next();
        	entry.getValue().bitsReceived = 0;			//reset bitsRecieved
        }
        
        if (bitfield.cardinality() == bitfield.size()) {
        	UnchokedTopK.clear();
        	Choked.clear();

    		Iterator<Entry<Integer, HostEntry>> it3 = Host.lookup.entrySet().iterator();
            while (it3.hasNext()) {
            	Map.Entry<Integer, HostEntry> entry = (Map.Entry<Integer, HostEntry>)it3.next();
            	UnchokedTopK.add(entry.getKey());
            }
        }
        
        if (!UnchokedTopK.isEmpty()){
            for (Integer peerID: UnchokedTopK) {
            	
            	if (Host.lookup.get(peerID).choked == true){
            		Host.lookup.get(peerID).choked = false;
            		Message m = new Message (Message.Type.UNCHOKE);
            		try {
    					Host.lookup.get(peerID).socket.sendMessage(m);
    				} catch (InstantiationException e1) {
    					e1.printStackTrace();
    				} catch (IllegalAccessException e1) {
    					e1.printStackTrace();
    				}
            		Host.log.logChangePreferredNeighbors(UnchokedTopK.toArray(new Integer[UnchokedTopK.size()]));
            	}
            }
        }

        if (!Choked.isEmpty()) {
            for (PeerRankEntry e: Choked) {
            	if (Host.lookup.get(e.peerID).choked == false) {
            		Host.lookup.get(e.peerID).choked = true;
            		Message m = new Message (Message.Type.CHOKE);
            		try {
    					Host.lookup.get(e.peerID).socket.sendMessage(m);
    				} catch (InstantiationException e1) {
    					e1.printStackTrace();
    				} catch (IllegalAccessException e1) {
    					e1.printStackTrace();
    				}
            	}
            }
        }
        
        //Host.printContents();
        System.out.println(Thread.currentThread().getId() + " exit updateTopK");
	}

	/**
	 * Determines the current optimistically unchoked peer for this m interval time frame.
	 * This method should only be used by the associated OptUnchokeTask for timing purposes.
	 * 
	 */
	protected static synchronized void findOptimisticPeer () {

		System.out.println(Thread.currentThread().getId() + " enter findOptPeer");
		if (!Host.ChokedInterested.isEmpty()) {
			Random randomGenerator = new Random();
			int index = randomGenerator.nextInt(Host.ChokedInterested.size());
			System.out.println(Thread.currentThread().getId() + " Host.findOptimisticPeer: entered loop, got index");
			Host.optimisticUnchokedPeer = Host.ChokedInterested.get(index);
			Message m = new Message (Message.Type.UNCHOKE);
			Host.log.logChangeOptimisticallyUnchokedNeighbor(optimisticUnchokedPeer);
			try {Host.lookup.get(optimisticUnchokedPeer).socket.sendMessage(m);} 
			catch (InstantiationException e) {e.printStackTrace();} 
			catch (IllegalAccessException e) {e.printStackTrace();}
		}
		else {
			Host.optimisticUnchokedPeer = -1;
		}
		System.out.println(Thread.currentThread().getId() + " exit findOptPeer");
	}

	/**
	 * Returns if he peer associated with the peerID has the complete file.
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */
	private static synchronized BitSet compare(BitSet peer, BitSet host) {
		System.out.println(Thread.currentThread().getId() + " enter compare");
		BitSet host1 = (BitSet) host.clone();
		BitSet peer1 = (BitSet) peer.clone();
		host1.flip(0, host1.size());
		host1.and(peer1);
		System.out.println(Thread.currentThread().getId() + " exit compare");
		return host1;	//changed from peer1

	}

	/**
	 * Returns if he peer associated with the peerID has the complete file.
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */
	private static synchronized boolean hasFile (int peerID) 
	{
		boolean hasFile = false;
		BitSet temp = (BitSet) Host.lookup.get(peerID).bitfield.clone();
		if (temp.cardinality() == Host.numPieces) {hasFile = true;
		
	
		}
		return hasFile;

	}

	/**
	 * Returns the number of bits recieved from the peer associated with the peerID
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */
	private static synchronized int getBitsRecieved (int peerID) 
	{
		return Host.lookup.get(peerID).bitsReceived;
	}

	/**
	 * Returns whether or not the peer is interested in the host pieces.
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */
	private static synchronized boolean isPeerInterested (int peerID) {
		return Host.lookup.get(peerID).peerInterested;
	}

	/**
	 * Sets host as being interested in the peer.
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */
	private static synchronized boolean isHostInterested (int peerID) {
		return Host.lookup.get(peerID).hostInterested;
	}

	//TODO:
	private static synchronized void updateFileCompletion () {

	}
	
	private static synchronized void printHost() {
		System.out.println(Thread.currentThread().getId() + " Printing Host: ");
		System.out.println(Thread.currentThread().getId() + " HostID: " + Host.hostID);
		System.out.println(Thread.currentThread().getId() + " NumPieces: " + Host.numPieces);
		System.out.println(Thread.currentThread().getId() + " numPrefNeighbors: " + Host.numPrefNeighbors);
		System.out.println(Thread.currentThread().getId() + " optUnnchokePeerID: " + Host.optimisticUnchokedPeer);
		System.out.println(Thread.currentThread().getId() + " unchokeInterval: " + Host.unchokeInterval);
		System.out.println(Thread.currentThread().getId() + " optUnchokeInterval: " + Host.optimisticUnchokeInterval);
		System.out.println(Thread.currentThread().getId() + " Host.Bitfield: " + Host.bitfield.toString());
		System.out.println(Thread.currentThread().getId() + " Host.randBitfield " + Host.randBitfield.toString());		
		System.out.println(Thread.currentThread().getId() + " End Host printing. ");
	}
	
	private static synchronized void printContents (){
		
		System.out.println(Thread.currentThread().getId() + " Lookup Table: ");
		
		Iterator<Entry<Integer, HostEntry>> it = Host.lookup.entrySet().iterator();
        while (it.hasNext()) {
        	Map.Entry<Integer, HostEntry> entry = (Map.Entry<Integer, HostEntry>)it.next();
        	System.out.println(Thread.currentThread().getId() + " Peer: " + entry.getKey() + " is host interested?: " + entry.getValue().hostInterested + " is interested in host?" + entry.getValue().peerInterested + " cardinaltiy: " + entry.getValue().bitfield.cardinality() );

        
        }
        

        System.out.println(Thread.currentThread().getId() + " Unchoked Top K: ");
        for (int peer: UnchokedTopK) {
        	System.out.print(peer);
        }
        
        System.out.println(Thread.currentThread().getId() + " Choked: ");
        
        for (PeerRankEntry peer: Choked) {
        	System.out.print(peer.peerID);
        }
        
        System.out.println();

	}
	

	public static synchronized void terminate()
	{
		if(!Host.terminated)
		{
			Host.terminated = true;
			Host.optUnchokeTimer.stop();
			Host.unchokeTimer.stop();
			Host.server.terminate();
			Iterator<Entry<Integer, HostEntry>> it = Host.lookup.entrySet().iterator();
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        while (it.hasNext()) 
	        {
	        	System.out.println("terminating!!!!");
	        	Map.Entry<Integer, HostEntry> entry = (Map.Entry<Integer, HostEntry>)it.next();
	  
	        	entry.getValue().socket.terminate();
	        } /* end while loop */
	        
	       
	        System.exit(0);
	        
		} /* end if */
		
	} /* end terminate */
	
}

