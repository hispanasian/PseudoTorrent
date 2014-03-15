package pseudoTorrent.networking;

import host.Host;
import networking.Protocol;
import networking.ProtocolMessage;
import networking.ProtocolPackage;

/**
 * Implements the Protocol class and defines the protocol that should be 
 * performed when an Interested message is received. 
 * @author Carlos Vasquez
 *
 */
public class InterestedProtocol extends Protocol
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
		Host.peerIsInterested(((TorrentSocket) protocols.getSocket()).getPeerID());
		Host.log.logReceivedInterested(((TorrentSocket) protocols.getSocket()).getPeerID());
		
	} /* end receiveProtocol method */

} /* end ChokeProtocol class */
