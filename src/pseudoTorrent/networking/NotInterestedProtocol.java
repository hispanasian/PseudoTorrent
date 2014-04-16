package pseudoTorrent.networking;

import host.Host;
import networking.Protocol;
import networking.ProtocolMessage;
import networking.ProtocolPackage;

/**
 * Implements the Protocol class and defines the protocol that should be 
 * performed when a NotInterested message is received. 
 * @author Carlos Vasquez
 *
 */
public class NotInterestedProtocol extends Protocol
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
		Host.peerIsNotInterested(((TorrentSocket) protocols.getSocket()).getPeerID());
		Host.log.logReceivedNotInterested(((TorrentSocket) protocols.getSocket()).getPeerID());
		
	} /* end receiveProtocol method */

} /* end ChokeProtocol class */
