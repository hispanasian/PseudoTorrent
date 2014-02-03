package pseudoTorrent.networking;

import java.net.Socket;

import pseudoTorrent.PseudoTorrent;

/**
 * The TorrentSocket class will perform the socket functionality necessary for
 * the PseudoTorrent program. It will be used to send messages and receive and 
 * interpret messages by performing the necessary protocol as defined by
 * the Protocol class. 
 * 
 * @author Carlos Vasquez
 *
 */
public class TorrentSocket extends ThreadedSocket
{
	/******************* Class Attributes *******************/
	private final PseudoTorrent torrent;
	
	/******************* Class Methods *******************/
	public TorrentSocket(PseudoTorrent torrent, final Socket socket)
	{
		super(socket);
		this.torrent = torrent;
		
	} /* end constructor */

	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		
		this.closeStreams();
	} /* end run method */
	
} /* end TorrentSocket class */
