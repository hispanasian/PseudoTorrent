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
public class TorrentSocket implements Runnable
{
	/******************* Class Methods *******************/
	private final PseudoTorrent torrent;
	private final Socket socket;
	
	/******************* Class Methods *******************/
	public TorrentSocket(final PseudoTorrent torrent, final Socket socket)
	{
		this.torrent = torrent;
		this.socket = socket;
	} /* end constructor */
	
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		
	} /* end run method */

} /* end TorrentSocket class */
