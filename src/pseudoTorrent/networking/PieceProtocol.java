package pseudoTorrent.networking;

import java.util.ArrayList;

import host.Host;
import networking.Protocol;
import networking.ProtocolMessage;
import networking.ProtocolPackage;

/**
 * Implements the Protocol class and defines the protocol that should be 
 * performed when a Piece message is received. 
 * @author Carlos Vasquez
 *
 */
public class PieceProtocol extends Protocol
{
	/******************* Class Methods *******************/
	@Override
	public void sendProtocol(ProtocolPackage protocols, ProtocolMessage message) 
	{
		int peerID = ((TorrentSocket) protocols.getSocket()).getPeerID();
		int chunk = ((TorrentSocket) protocols.getSocket()).request;
		Host.log.logDownloadingPiece(peerID, chunk, Host.getHostBitfield().cardinality());
		
	} /* end sendProtocol method */

	@Override
	public void receiveProtocol(ProtocolPackage protocols, ProtocolMessage message) 
	{
		int peerID = ((TorrentSocket) protocols.getSocket()).getPeerID();
		int chunkID = ((TorrentSocket) protocols.getSocket()).request;
		Message chunk = (Message) message;
		
		/* Only update piece and host if the request is not null */
		if(((TorrentSocket) protocols.getSocket()).request != null)
		{
			Host.file.giveChunk(chunkID, chunk.payload);
			Host.updatePiece(chunkID, peerID);
		} /* end if */
		
		/* Send Have to peers */
		Message have = new Message(Message.Type.HAVE, chunkID);
		ArrayList<TorrentSocket> peers = Host.getSocketList();
		for(TorrentSocket peer : peers)
		{
			try 
			{
				peer.sendMessage(have);
			} /* end try */ 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} /* end catch */ 
		} /* end for loop */
		
		/* If interested, send request. Else, send not interested */
		Message nextMessage = null;
		if(Host.isInterested(peerID))
		{
			/* Only send a request if we have not sent a request before. If we
			 * have sent a request, do not request a new piece. */
			if(((TorrentSocket) protocols.getSocket()).request == null)
			{
				chunkID = Host.getRandomChunkID(peerID);
				((TorrentSocket) protocols.getSocket()).request = chunkID;
				nextMessage = new Message(Message.Type.REQUESET, chunkID);
			} /* end if */
				
		} /* end if */
		else nextMessage = new Message(Message.Type.NOT_INTERESTED);
		
		/* Send the next message */
		try 
		{
			protocols.process(nextMessage, Protocol.Stance.SENDING);
		} /* end try */ 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /* end catch */
		
	} /* end receiveProtocol method */

} /* end ChokeProtocol class */
