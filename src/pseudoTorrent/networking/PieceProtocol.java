package pseudoTorrent.networking;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import host.Host;
import host.HostEntry;
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
		// Do nothing
		
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
			System.out.println("PieceProtocol.receiveProtocol: giving file " + chunkID);
			Host.file.giveChunk(chunkID, chunk.payload);
			System.out.println("PieceProtocol.receiveProtocol: put file " + chunkID+ " " + Host.getHostBitfield());
			Host.updatePiece(chunkID, peerID);
			System.out.println("PieceProtocol: updated piece " + Host.getHostBitfield());
			Host.log.logDownloadingPiece(peerID, chunkID, Host.getHostBitfield().cardinality());
			System.out.println("PieceProtocol.receiveProtocol: received " + chunkID);
			// Reset request
			((TorrentSocket) protocols.getSocket()).request = null;
			
		} /* end if */
		
		/* Send Have to peers */
		Message have = new Message(Message.Type.HAVE, chunkID);
		ArrayList<TorrentSocket> peers = Host.getSocketList();
		for(TorrentSocket peer : peers)
		{
			try 
			{
				peer.sendMessage(have);
				System.out.println("PieceProtocol.receive: sent HAVE to " + peer + " for " + chunkID);
			} /* end try */ 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} /* end catch */ 
		} /* end for loop */
		System.out.println("PieceProtocol.receive: done sending HAVE");
		/* If done, log the completion of the file */
		int chunksObtained = Host.getHostBitfield().cardinality();
		boolean done = false;
		
		if(chunksObtained >= Host.numPieces)
		{
			/* File completed, log completion */
			done = true;
			Host.log.logCompletion();
		} /* end if */
		
		/* Check if everyone is done. If so, terminate processes */
		
		/*edit - remove this*/
		Iterator<Entry<Integer, HostEntry>> it = Host.lookup.entrySet().iterator();
        while (it.hasNext()) {
        	Map.Entry<Integer, HostEntry> entry = (Map.Entry<Integer, HostEntry>)it.next();
        	System.out.println("Lookup Table - entry key (peerID): " + entry.getKey() + " cardinaltiy: " + entry.getValue().bitfield.cardinality() );

        
        }
        /*end remove*/
		
		
		if(Host.everyoneHasFile())
		{
			done = true;
			Host.terminate();
			
		} /* end if */
		
		/* If interested, send request. Else, send not interested Furthermore,
		 * Only send a request if we have not sent a request before. If we
		 * have sent a request, do not request a new piece. Furthermore, do 
		 * not continue if done. */
		Message nextMessage = null;
		if(Host.isInterested(peerID) && ((TorrentSocket) protocols.getSocket()).request == null && !done)
		{
			chunkID = Host.getRandomChunkID(peerID);
			if(chunkID != -1)
			{/* -1 returned if all files have been requested but not 
				necessarily received. Only send request for pieces that
				have yet to be requested. */
				((TorrentSocket) protocols.getSocket()).request = chunkID;
				nextMessage = new Message(Message.Type.REQUEST, chunkID);
			} /* end if */
			
				
		} /* end if */
		else nextMessage = new Message(Message.Type.NOT_INTERESTED);
		
		/* Send the next message */
		if(nextMessage != null)
		{
			try 
			{
				protocols.process(nextMessage, Protocol.Stance.SENDING);
			} /* end try */ 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} /* end catch */
			
		} /* end if */
		
	} /* end receiveProtocol method */

} /* end ChokeProtocol class */
