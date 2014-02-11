package pseudoTorrent.networking;

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
		// TODO Auto-generated method stub
		
	} /* end sendProtocol method */

	@Override
	public void receiveProtocol(ProtocolPackage protocols, ProtocolMessage message) 
	{
		// TODO Auto-generated method stub
		
	} /* end receiveProtocol method */

} /* end ChokeProtocol class */
