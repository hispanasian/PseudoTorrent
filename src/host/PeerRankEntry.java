package host;

import java.util.Comparator;

/**
 * For use in Tracker array lists.
 * 
 * @author Terek
 *
 */
public class PeerRankEntry {
	
	/******************* Class Attributes *******************/
	public int peerID;
	public int bitsReceived;
	public boolean isInterested;
	
    public static final Comparator<PeerRankEntry> DESCENDING_COMPARATOR = new Comparator<PeerRankEntry>() {
        // Overriding the compare method to sort the bitsRecieved
        public int compare(PeerRankEntry p, PeerRankEntry p1) {
            return p.bitsReceived - p1.bitsReceived;
        }
    };
    
    /******************* Class Methods *******************/
    public PeerRankEntry (int peerID, int bits, boolean isInterested) {
    	this.peerID = peerID;
    	this.bitsReceived = bits;
    	this.isInterested = isInterested;
    }
 
}
