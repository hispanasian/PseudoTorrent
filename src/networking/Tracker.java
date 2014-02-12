package networking;

import java.net.Socket;
import java.util.Hashtable;

public class Tracker 
{
	/******************* Class Constants *******************/
	
	/******************* Class Attributes *******************/
	protected Hashtable<Integer, TrackerEntry> lookup;
	
	/******************* Class Methods *******************/
	/**
	 * Creates a Tracker that utilizes the provided TrackerEntry
	 */
	public Tracker ()
	{
		this.lookup = new Hashtable<Integer, TrackerEntry>();
	}
	
	public synchronized void add(int peerID, final Socket socket)
	{
		/* Map the peerID to tracker entry */
		this.lookup.put(peerID, new TrackerEntry(socket));
	}
	
	public synchronized void choke (int peerID) 
	{
		this.lookup.get(peerID).choked = true;
	}
	
	public synchronized void choking (int peerID) 
	{
		this.lookup.get(peerID).choking = true;
	}
	
	public synchronized int bitsRecieved (int peerID) 
	{
		return this.lookup.get(peerID).bitsReceived;
	}
	
	public synchronized void addBits (int peerID, int numBits) {
		this.lookup.get(peerID).bitsReceived += numBits;
	}
}
