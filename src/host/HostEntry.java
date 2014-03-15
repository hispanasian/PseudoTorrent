package host;

import java.util.BitSet;
import pseudoTorrent.networking.*;;

/**
 * For use in Tracker lookup map.
 * 
 * @author Terek
 *
 */
public class HostEntry 
{
	/******************* Class Constants *******************/
	public final TorrentSocket socket; 		//socket the peer is uploading to

	/******************* Class Attributes *******************/
	public int bitsReceived;			//bit received in last p-second unchoking interval
	public boolean choked;				//whether the peer is choked or not	
	public boolean choking;				//whether the peer is choking you
	public BitSet bitfield;				//bitset for the peer (what bits the peer has)
	public BitSet randBitfield;			//bitset for use by random generator
	public boolean peerInterested;		//whether the peer is interested in the host still
	public boolean hostInterested;		//whether the host is interested in this peer still

	/******************* Class Methods *******************/
	public HostEntry(final TorrentSocket socket)
	{
		this.socket = socket;
		this.choked = false;
		this.choking = false;
		this.bitsReceived = 0;
		this.peerInterested = false;
		this.hostInterested = false;
		
	} /* end Constructor */
	
	public HostEntry(final TorrentSocket socket, int bitfieldSize)
	{
		this.socket = socket;
		this.choked = false;
		this.choking = false;
		this.bitsReceived = 0;
		this.bitfield = new BitSet (bitfieldSize);
		this.randBitfield = new BitSet (bitfieldSize);
		this.peerInterested = false;
		this.hostInterested = false;
		
	} /* end Constructor */
	
	public HostEntry(final TorrentSocket socket, boolean choked)
	{
		this.socket = socket;
		this.choked = choked;
		this.choking = false;
		this.bitsReceived = 0;
		this.peerInterested = false;
		this.hostInterested = false;
		
	} /* end Constructor */
	
	public HostEntry(final TorrentSocket socket, boolean choked, boolean choking)
	{
		this.socket = socket;
		this.choked = choked;
		this.choking = choking;
		this.bitsReceived = 0;
		this.peerInterested = false;
		this.hostInterested = false;
		
	} /* end Constructor */
	
	public HostEntry(final TorrentSocket socket, boolean choked, boolean choking, boolean isInterested)
	{
		this.socket = socket;
		this.choked = choked;
		this.choking = choking;
		this.bitsReceived = 0;
		this.peerInterested = isInterested;
		this.hostInterested = false;
		
	} /* end Constructor */
	
	public HostEntry(final TorrentSocket socket, boolean choked, boolean choking, boolean isInterested, boolean imInterested)
	{
		this.socket = socket;
		this.choked = choked;
		this.choking = choking;
		this.bitsReceived = 0;
		this.peerInterested = isInterested;
		this.hostInterested = imInterested;
		
	} /* end Constructor */
}
