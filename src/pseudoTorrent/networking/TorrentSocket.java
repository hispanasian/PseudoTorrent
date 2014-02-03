package pseudoTorrent.networking;

import java.net.Socket;
import java.net.SocketException;

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
	/******************* Class Constants *******************/
	public static final int TIMEOUT = 1000;
	
	/******************* Class Attributes *******************/
	private final PseudoTorrent torrent;
	
	/******************* Class Methods 
	 * @throws SocketException *******************/
	public TorrentSocket(PseudoTorrent torrent, final Socket socket) throws SocketException
	{
		super(socket);
		this.torrent = torrent;
		this.socket.setSoTimeout(TIMEOUT);
	} /* end constructor */

	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		
		this.closeStreams();
	} /* end run method */
	
} /* end TorrentSocket class */
