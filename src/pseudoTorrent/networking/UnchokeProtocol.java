package pseudoTorrent.networking;

import host.Host;
import networking.Protocol;
import networking.ProtocolMessage;
import networking.ProtocolPackage;

/**
 * Implements the Protocol class and defines the protocol that should be 
 * performed when a Unchoke message is received. 
 * @author Carlos Vasquez
 *
 */
public class UnchokeProtocol extends Protocol
{
	/******************* Class Methods *******************/
	@Override
	public void sendProtocol(ProtocolPackage protocols, ProtocolMessage message) 
	{
		Host.log.logChoking(((TorrentSocket) protocols.getSocket()).getPeerID());
		
	} /* end sendProtocol method */

	@Override
	public void receiveProtocol(ProtocolPackage protocols, ProtocolMessage message) 
	{
		Host.chokedBy(((TorrentSocket) protocols.getSocket()).getPeerID());
		
	} /* end receiveProtocol method */

} /* end ChokeProtocol class */
