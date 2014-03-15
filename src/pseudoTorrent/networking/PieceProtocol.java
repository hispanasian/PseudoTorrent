package pseudoTorrent.networking;

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
		int chunkID = ((TorrentSocket) protocols.getSocket()).request;
		Message chunk = (Message) message;
		
		/* Store chunk and update host */
		Host.file.giveChunk(chunkID, chunk.payload);
		Host.updateHostBitfield(chunkID);
		
		/* Send Have to peers */
		
		/* If interested, send request. Else, send not interested */
		
	} /* end receiveProtocol method */

} /* end ChokeProtocol class */
