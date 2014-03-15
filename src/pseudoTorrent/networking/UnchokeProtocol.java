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
		Host.log.logUnchoking(((TorrentSocket) protocols.getSocket()).getPeerID());
		
	} /* end sendProtocol method */

	@Override
	public void receiveProtocol(ProtocolPackage protocols, ProtocolMessage message) 
	{
		int peerID = ((TorrentSocket) protocols.getSocket()).getPeerID();
		Host.unchokedBy(peerID);
		if(Host.isInterested(peerID))
		{
			int chunk = Host.getRandomChunkID(peerID);
			Message req = new Message(Message.Type.REQUESET, chunk);
			try 
			{
				protocols.process(req, Protocol.Stance.SENDING);
			} /* end try */
			catch (Exception e) 
			{
				// TODO determine what to do
			} /* end catch */
			
		} /* end if */
		
	} /* end receiveProtocol method */

} /* end ChokeProtocol class */
