package tracking;

import java.net.Socket;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;



/*
 * Could be a problem with optimisticUnchokedPeer.  If not enough time passes, it will be -1.
 */
public class Tracker 
{
	/******************* Class Constants *******************/
	
	/******************* Class Attributes *******************/
	protected static Hashtable<Integer, TrackerEntry> lookup;
	protected static int numPieces;
	protected static int numPrefNeighbors;
	protected static int unchokeInterval;
	protected static int optimisticUnchokeInterval;
	public static ArrayList <Integer>  UnchokedTopK;
	protected static ArrayList <PeerRankEntry> AllRank;
	protected static ArrayList <PeerRankEntry> Choked;
	public static int optimisticUnchokedPeer;
	
	/******************* Class Methods *******************/
	/**
	 * Creates a Tracker that utilizes the provided TrackerEntry
	 */
	public Tracker (int numPrefNeighbors, int unchokeInterval, int optimisticUnchokeInterval, int fileSize, int pieceSize)
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
	}
	
	protected static synchronized void updateTopK () {
		Tracker.AllRank.clear();
		Tracker.UnchokedTopK.clear();
		Tracker.Choked.clear();
        
		Iterator<Entry<Integer, TrackerEntry>> it = Tracker.lookup.entrySet().iterator();
        while (it.hasNext()) {
        	Map.Entry<Integer, TrackerEntry> entry = (Map.Entry<Integer, TrackerEntry>)it.next();
        	Tracker.AllRank.add(new PeerRankEntry(entry.getKey(), entry.getValue().bitsReceived, entry.getValue().isInteretested));
        	it.remove();// avoids a ConcurrentModificationException
        }
        
        Collections.sort(Tracker.AllRank, PeerRankEntry.DESCENDING_COMPARATOR);
        
        int i = 0;
        for (PeerRankEntry e : Tracker.AllRank) {
        	if ((i < Tracker.numPrefNeighbors) && (e.isInterested)){
        		Tracker.UnchokedTopK.add(e.peerID);
        		i++;
        	}
        	
        }
        
        for (int j=Tracker.numPrefNeighbors; j < Tracker.AllRank.size(); j++) {
        	if (Tracker.AllRank.get(j).isInterested) {
            	Tracker.Choked.add(Tracker.AllRank.get(j));
        	}
        }
        
		Iterator<Entry<Integer, TrackerEntry>> it2 = Tracker.lookup.entrySet().iterator();
        while (it2.hasNext()) {
        	Map.Entry<Integer, TrackerEntry> entry = (Map.Entry<Integer, TrackerEntry>)it.next();
        	entry.getValue().bitsReceived = 0;			//reset bitsRecieved
        	it2.remove();// avoids a ConcurrentModificationException
        }
	}
	
	public static synchronized int getOptimisticPeer () {
		return Tracker.optimisticUnchokedPeer;
	}
	
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
	
	//TODO: should we update after every add?
	public static synchronized void add(int peerID, final Socket socket)
	{
		/* Map the peerID to tracker entry */
		Tracker.lookup.put(peerID, new TrackerEntry(socket));
		//Tracker.updateTopK();
		//Tracker.findOptimalNeighbor();
	}
	
	public static synchronized void choke (int peerID) 
	{
		Tracker.lookup.get(peerID).choked = true;
	}
	
	public static synchronized void unchoke (int peerID) 
	{
		Tracker.lookup.get(peerID).choked = false;
	}
	
	public synchronized boolean getIsChoked (int peerID) {
		return Tracker.lookup.get(peerID).choked;
	}
	
	public synchronized void setIsPeerChoking (int peerID, boolean choking) 
	{
		Tracker.lookup.get(peerID).choking = choking;
	}
	
	public synchronized Boolean getIsPeerChoking (int peerID) 
	{
		return Tracker.lookup.get(peerID).choking;
	}
	
	public synchronized int getBitsRecieved (int peerID) 
	{
		return Tracker.lookup.get(peerID).bitsReceived;
	}
	
	public synchronized void addBits (int peerID, int numBits) {
		Tracker.lookup.get(peerID).bitsReceived += numBits;
	}
	
	public synchronized void hasFile (int peerID) 
	{
		BitSet temp = (BitSet) Tracker.lookup.get(peerID).bitfield.clone();
		//TODO:temp.flip(i);
		
	}
	
	public synchronized void updateBitfield (int peerID, int piece) 
	{
		Tracker.lookup.get(peerID).bitfield.set(piece);
	}
	
	public synchronized BitSet getBitfield (int peerID) 
	{
		return Tracker.lookup.get(peerID).bitfield;
	}
	
	public synchronized void setInterested (int peerID, boolean isInterested) 
	{
		Tracker.lookup.get(peerID).isInteretested = isInterested;
	}
	
	public synchronized boolean getInterested (int peerID) 
	{
		return Tracker.lookup.get(peerID).isInteretested;
	}
}
