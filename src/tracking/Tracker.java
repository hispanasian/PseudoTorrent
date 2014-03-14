package tracking;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

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

public class Tracker 
{
	
	/******************* Class Attributes *******************/
	public static Hashtable<Integer, TrackerEntry> lookup;
	
	protected static int numPieces;
	protected static int numPrefNeighbors;
	protected static int unchokeInterval;
	protected static int optimisticUnchokeInterval;	
	protected static ArrayList <PeerRankEntry> AllRank;
	protected static ArrayList <PeerRankEntry> Choked;
	
	public static ArrayList <Integer>  UnchokedTopK;
	public static int optimisticUnchokedPeer;
	
	/******************* Class Methods *******************/
	
	private Tracker () {
		
	}
	
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
	public static synchronized void setup (int numPrefNeighbors, int unchokeInterval, int optimisticUnchokeInterval, int fileSize, int pieceSize)
	{
		Tracker.lookup = new Hashtable<Integer, TrackerEntry>();
		Tracker.numPieces = fileSize / pieceSize;
		if ((fileSize % pieceSize) != 0) {numPieces++;}
		Tracker.numPrefNeighbors = numPrefNeighbors;
		Tracker.unchokeInterval = unchokeInterval;
		Tracker.optimisticUnchokeInterval = optimisticUnchokeInterval;
		Tracker.UnchokedTopK = new ArrayList <Integer> ();
		Tracker.Choked = new ArrayList <PeerRankEntry> ();
		Tracker.AllRank = new ArrayList <PeerRankEntry> ();
		Tracker.optimisticUnchokedPeer = -1;
	} /* end Constructor */
	
	/**
	 * Updates the AllRank, UnchokedTopK and Choked array list.  These lists provide the user with access
	 * to which peers are to be unchoked for a give k time interval and are utilized in determining
	 * the optimistic unchoked peer.  This method should only be used by the associated UnchokeTask for timing
	 * purposes.
	 * 
	 */
	protected static synchronized void updateTopK () {
		Tracker.AllRank.clear();
		Tracker.UnchokedTopK.clear();
		Tracker.Choked.clear();
        
		Iterator<Entry<Integer, TrackerEntry>> it = Tracker.lookup.entrySet().iterator();
        while (it.hasNext()) {
        	Map.Entry<Integer, TrackerEntry> entry = (Map.Entry<Integer, TrackerEntry>)it.next();
//        	System.out.println(entry.getKey() + " " + entry.getValue().bitsReceived);
        	Tracker.AllRank.add(new PeerRankEntry(entry.getKey(), entry.getValue().bitsReceived, entry.getValue().isInteretested));
        	//it.remove();// avoids a ConcurrentModificationException
        }
        
//        System.out.println("Size of lookup: " + Tracker.lookup.size());
//        System.out.println("Size of AllRank: " + Tracker.AllRank.size());
        Collections.sort(Tracker.AllRank, PeerRankEntry.DESCENDING_COMPARATOR);
        
        int i = 0;
        for (PeerRankEntry e : Tracker.AllRank) {
        	if ((i < Tracker.numPrefNeighbors) && (e.isInterested)){
//        		System.out.println(e.peerID);
        		Tracker.UnchokedTopK.add(e.peerID);
        		i++;
        	}
        	
        }
        
        for (int j = Tracker.numPrefNeighbors; j < Tracker.AllRank.size(); j++) {
        	if (Tracker.AllRank.get(j).isInterested) {
            	Tracker.Choked.add(Tracker.AllRank.get(j));
        	}
        }
        
		Iterator<Entry<Integer, TrackerEntry>> it2 = Tracker.lookup.entrySet().iterator();
        while (it2.hasNext()) {
        	Map.Entry<Integer, TrackerEntry> entry = (Map.Entry<Integer, TrackerEntry>)it2.next();
        	entry.getValue().bitsReceived = 0;			//reset bitsRecieved
        	//it2.remove();// avoids a ConcurrentModificationException
        }
	}
	
	/**
	 * Returns the optimistically unchoked peerID for this m interval time frame.
	 * 
	 */
	public static synchronized int getOptimisticPeer () {
		return Tracker.optimisticUnchokedPeer;
	}
	
	/**
	 * Determines the current optimistically unchoked peer for this m interval time frame.
	 * This method should only be used by the associated OptUnchokeTask for timing purposes.
	 * 
	 */
	protected static synchronized void findOptimisticPeer () {
		Random randomGenerator = new Random();
		
		if (!Tracker.Choked.isEmpty()) {
			int index = randomGenerator.nextInt(Tracker.Choked.size());
	        Tracker.optimisticUnchokedPeer = Tracker.Choked.get(index).peerID;
		}
		else {
			Tracker.optimisticUnchokedPeer = -1;
		}
	}
	
	//TODO: should we update after every add?  should the type socket be changed?  Torrent Socket!
	/**
	 * Allows the user to add a peer and associated socket to the lookup map.  This should be invoked at
	 * the start of main as threads are being spun up.  
	 * 
	 * @param peerID	the peer id of the connected peer
	 * @param socket	the socket the peer is using
	 * 
	 */
	public static synchronized void add(int peerID, final TorrentSocket socket)
	{
		/* Map the peerID to tracker entry */
		System.out.println("HERE");
		Tracker.lookup.put(peerID, new TrackerEntry(socket));
		//Tracker.updateTopK();
		//Tracker.findOptimalNeighbor();
	}
	
	/**
	 * Chokes the peer associated with peerID.
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */
	protected static synchronized void choke (int peerID) 
	{
		Tracker.lookup.get(peerID).choked = true;
	}
	
	/**
	 * Unchokes the peer associated with the peerID
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */
	protected static synchronized void unchoke (int peerID) 
	{
		Tracker.lookup.get(peerID).choked = false;
	}
	
	/**
	 * Returns if the peer associated with the peerID is choked or not
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */	
	public synchronized boolean getIsChoked (int peerID) {
		if (Tracker.lookup.isEmpty()) {return true;} //TODO: do I need this?
		return Tracker.lookup.get(peerID).choked;
	}
	
	/**
	 * Sets the peer associated with the peerID as choking/unchoking the user peer process
	 * 
	 * @param peerID	the peer id of the peer
	 * @param choking	true/false peer is choking the user
	 * 
	 */
	public synchronized void setIsPeerChoking (int peerID, boolean choking) 
	{
		Tracker.lookup.get(peerID).choking = choking;
	}
	
	/**
	 * Returns a true/false for whether or not the peer associated with the peerID is choking the user.
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */
	public synchronized Boolean getIsPeerChoking (int peerID) 
	{
		return Tracker.lookup.get(peerID).choking;
	}
	
	/**
	 * Returns the number of bits recieved from the peer associated with the peerID
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */
	public synchronized int getBitsRecieved (int peerID) 
	{
		return Tracker.lookup.get(peerID).bitsReceived;
	}
	
	/**
	 * Adds some number of bits to the total number of bits recieved in the last time period 
	 * from the peer associated with the peerID.  This is used in determining the download 
	 * rate from the peers.
	 * 
	 * @param peerID	the peer id of the peer
	 * @param numBits	the number of bits to add to this peer
	 * 
	 */
	public static synchronized void addBytes (int peerID, int bytes) {
		Tracker.lookup.get(peerID).bitsReceived += bytes;
	}
	
	/**
	 * Returns if he peer associated with the peerID has the complete file.
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */
	public static synchronized boolean hasFile (int peerID) 
	{
		BitSet temp = (BitSet) Tracker.lookup.get(peerID).bitfield.clone();
		//TODO:temp.flip(i);
		return false;
		
	}
	
	/**
	 * Updates the bitfield record for a given peer using the piece information that it says it no longer needs.
	 * 
	 * @param peerID	the peer id of the peer
	 * @param piece		the piece that needs to be updated in the bitfield
	 * 
	 */
	public static synchronized void updateBitfield (int peerID, int piece) 
	{
		Tracker.lookup.get(peerID).bitfield.set(piece);
	}
	
	/**
	 * Returns the bitfield of the give peer.
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */
	public static synchronized BitSet getBitfield (int peerID) 
	{
		return Tracker.lookup.get(peerID).bitfield;
	}
	
	/**
	 * Sets the field to let the tracker know if the peer is interested in the user's pieces.
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */
	public static synchronized void setInterested (int peerID, boolean isInterested) 
	{
		Tracker.lookup.get(peerID).isInteretested = isInterested;
	}
	
	/**
	 * Returns whether or not the peer is interested in the user's pieces.
	 * 
	 * @param peerID	the peer id of the peer
	 * 
	 */
	public static synchronized boolean getInterested (int peerID) 
	{
		return Tracker.lookup.get(peerID).isInteretested;
	}
}
