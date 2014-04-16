package pseudoTorrent.networking;

import host.Host;
import networking.Protocol;
import networking.ProtocolMessage;
import networking.ProtocolPackage;

/**
 * Implements the Protocol class and defines the protocol that should be 
 * performed when a Choke message is received. 
 * @author Carlos Vasquez
 *
 */
public class ChokeProtocol extends Protocol
{
	/******************* Class Methods *******************/
	@Override
	public void sendProtocol(ProtocolPackage protocols, ProtocolMessage message) 
	{
		// Do nothing
		
	} /* end sendProtocol method */

	@Override
	public void receiveProtocol(ProtocolPackage protocols, ProtocolMessage message) 
	{
		Host.chokedBy(((TorrentSocket) protocols.getSocket()).getPeerID());
		Host.log.logChoking(((TorrentSocket) protocols.getSocket()).getPeerID());
		
		/* Tell the host and the TorrentSocket that we are no longer expecting
		 * the request (if it is not null) */
		if(((TorrentSocket) protocols.getSocket()).request != null)
		{
			Host.unsetRandomChunk(((TorrentSocket) protocols.getSocket()).request);
			((TorrentSocket) protocols.getSocket()).request = null;
		} /* end if */
		
	} /* end receiveProtocol method */

} /* end ChokeProtocol class */
