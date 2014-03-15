package pseudoTorrent.networking;

import java.util.BitSet;

import host.Host;
import networking.Protocol;
import networking.ProtocolMessage;
import networking.ProtocolPackage;

/**
 * Implements the Protocol class and defines the protocol that should be 
 * performed when a Bitfield message is received. 
 * @author KiranRohankar
 *
 */
public class BitfieldProtocol extends Protocol
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
		//host.updatePeerBitfield(peerId of the peer sending message,bitfield)
		int senderId= ((TorrentSocket)protocols.getSocket()).getPeerID();
		 BitSet bitfield=((Message)message).payloadToBitSet();
		 Host.updatePeerBitfield(senderId, bitfield);
		 
		 
		//check if i am interested in sender if send not interested message
		 boolean interest= Host.isInterested(senderId);
		 if(interest)
		 {
			 Message msg= new Message(Message.Type.INTERESTED);
			 ((TorrentSocket)protocols.getSocket()).sendMessage(msg);
		 }
		 else
		 {
			 Message msg= new Message(Message.Type.NOT_INTERESTED);
			 ((TorrentSocket)protocols.getSocket()).sendMessage(msg);
		 }
		 
		//if its interested then it sends an interested message
		// TODO Auto-generated method stub
		
		
		
	} /* end receiveProtocol method */

} /* end ChokeProtocol class */
