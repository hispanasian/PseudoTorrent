package pseudoTorrent.networking;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import pseudoTorrent.PseudoTorrent;
import networking.ProtocolMessage;
import networking.ProtocolSocket;


/**
 * The TorrentSocket class will perform the socket functionality necessary for
 * the PseudoTorrent program. It will be used to send messages and receive and 
 * interpret messages by performing the necessary protocol as defined by the 
 * Protocols class. 
 * 
 * @author Carlos Vasquez
 *
 */
public class TorrentSocket extends ProtocolSocket
{
	/******************* Class Constants *******************/
	public static final int TIMEOUT = 1000;
	
	/******************* Class Attributes *******************/
	public final PseudoTorrent torrent;
	
	/******************* Class Methods *******************/
	public TorrentSocket(PseudoTorrent torrent, final Socket socket) throws SocketException, IOException
	{
		super(socket);
		this.torrent = torrent;
		this.socket.setSoTimeout(TIMEOUT);
	} /* end constructor */

	@Override
	public void initialProcess() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endProcess() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected ProtocolMessage definedGetMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void definedSendMessage(ProtocolMessage message) {
		// TODO Auto-generated method stub
		
	}
	
} /* end TorrentSocket class */
