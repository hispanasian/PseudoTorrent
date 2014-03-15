package pseudoTorrent.networking;

import host.Host;
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
		// Do nothing
		
	} /* end sendProtocol method */

	@Override
	public void receiveProtocol(ProtocolPackage protocols, ProtocolMessage message) 
	{
		/* Check if interested. If so, send the requested chunk */
		if(Host.isInterested(((TorrentSocket) protocols.getSocket()).getPeerID()));
		{
			Message req = (Message) message;
			byte[] chunk = Host.file.getChunk(req.payloadToInt());
			
			Message piece = new Message(Message.Type.PIECE, chunk);
			
			try 
			{
				protocols.process(piece, Protocol.Stance.SENDING);
			} /* end try */ 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} /* end catch */
		} /* end if */
		
	} /* end receiveProtocol method */

} /* end ChokeProtocol class */
