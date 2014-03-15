package pseudoTorrent.networking;

import networking.Protocol;
import networking.ProtocolMessage;
import networking.ProtocolPackage;

/**
 * Implements the Protocol class and defines the protocol that should be 
 * performed when a Request message is received. 
 * @author Carlos Vasquez
 *
 */
public class RequestProtocol extends Protocol
{
	/******************* Class Methods *******************/
	@Override
	public void sendProtocol(ProtocolPackage protocols, ProtocolMessage message) 
	{
		// TODO Auto-generated method stub
		
	} /* end sendProtocol method */

	@Override
	public void receiveProtocol(ProtocolPackage protocols, ProtocolMessage message) 
	{
		//If will get the requested chunkID and peerID.
		//if its chocked ignore the message else send piece message of that request
		
		
	} /* end receiveProtocol method */

} /* end ChokeProtocol class */
