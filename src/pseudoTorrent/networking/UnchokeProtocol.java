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
		// Do nothing
		
	} /* end sendProtocol method */

	@Override
	public void receiveProtocol(ProtocolPackage protocols, ProtocolMessage message) 
	{
		int peerID = ((TorrentSocket) protocols.getSocket()).getPeerID();
		Host.unchokedBy(peerID);
		Host.log.logUnchoking(((TorrentSocket) protocols.getSocket()).getPeerID());
		
		if(Host.isInterested(peerID))
		{
			/* Only send a request if we have not sent a request before. If we
			 * have sent a request, do not request a new piece. */
				if(((TorrentSocket) protocols.getSocket()).request == null)
				{
					int chunk = Host.getRandomChunkID(peerID);
					if(chunk != -1)
					{ /* -1 returned if all files have been requested but not 
						necessarily received. Only send request for pieces that
						have yet to be requested. */
						((TorrentSocket) protocols.getSocket()).request = chunk;
						
						Message req = new Message(Message.Type.REQUEST, chunk);
						try 
						{
							protocols.process(req, Protocol.Stance.SENDING);
						} /* end try */
						catch (Exception e) 
						{
							// TODO determine what to do
						} /* end catch */
						
					} /* end if */
					
				} /* end if */
			
		} /* end if */
		
	} /* end receiveProtocol method */

} /* end ChokeProtocol class */
