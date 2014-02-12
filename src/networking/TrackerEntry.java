package networking;

import java.net.Socket;
import java.util.BitSet;

public class TrackerEntry 
{
	/******************* Class Constants *******************/
	public final Socket socket; 		//socket the peer is uploading to

	/******************* Class Attributes *******************/
	public int bitsReceived;			//bit received in last p-second unchoking interval
	public boolean choked;				//whether the peer is choked or not	
	public boolean choking;				//whether the peer is choking you
	public static BitSet bitfield;		//bitset for the peer (what bits the peer has)

	/******************* Class Methods *******************/
	
	public TrackerEntry(final Socket socket)
	{
		this.socket = socket;
		this.choked = false;
		this.choking = false;
		this.bitsReceived = 0;
		
	} /* end Constructor */
	
	public TrackerEntry(final Socket socket, boolean choked)
	{
		this.socket = socket;
		this.choked = choked;
		this.choking = false;
		this.bitsReceived = 0;
		
	} /* end Constructor */
	
	public TrackerEntry(final Socket socket, boolean choked, boolean choking)
	{
		this.socket = socket;
		this.choked = choked;
		this.choking = choking;
		this.bitsReceived = 0;
		
	} /* end Constructor */
}
