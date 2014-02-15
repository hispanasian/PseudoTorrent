package tracking;

import java.util.BitSet;
import pseudoTorrent.networking.*;;

/**
 * For use in Tracker lookup map.
 * 
 * @author Terek
 *
 */
public class TrackerEntry 
{
	/******************* Class Constants *******************/
	public final TorrentSocket socket; 		//socket the peer is uploading to TODO: should this be changed to diff type socket?

	/******************* Class Attributes *******************/
	public int bitsReceived;			//bit received in last p-second unchoking interval
	public boolean choked;				//whether the peer is choked or not	
	public boolean choking;				//whether the peer is choking you
	public BitSet bitfield;				//bitset for the peer (what bits the peer has)
	public boolean isInteretested;		//whether the peer is interested in you still

	/******************* Class Methods *******************/
	public TrackerEntry(final TorrentSocket socket)
	{
		this.socket = socket;
		this.choked = false;
		this.choking = false;
		this.bitsReceived = 0;
		this.isInteretested = false;
		
	} /* end Constructor */
	
	public TrackerEntry(final TorrentSocket socket, boolean choked)
	{
		this.socket = socket;
		this.choked = choked;
		this.choking = false;
		this.bitsReceived = 0;
		this.isInteretested = false;
		
	} /* end Constructor */
	
	public TrackerEntry(final TorrentSocket socket, boolean choked, boolean choking)
	{
		this.socket = socket;
		this.choked = choked;
		this.choking = choking;
		this.bitsReceived = 0;
		this.isInteretested = false;
		
	} /* end Constructor */
	
	public TrackerEntry(final TorrentSocket socket, boolean choked, boolean choking, boolean interested)
	{
		this.socket = socket;
		this.choked = choked;
		this.choking = choking;
		this.bitsReceived = 0;
		this.isInteretested = interested;
		
	} /* end Constructor */
}
