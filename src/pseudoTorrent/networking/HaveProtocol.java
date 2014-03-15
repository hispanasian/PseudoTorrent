package pseudoTorrent.networking;




import host.Host;
import networking.Protocol;
import networking.ProtocolMessage;
import networking.ProtocolPackage;

/**
 * Implements the Protocol class and defines the protocol that should be 
 * performed when a Have message is received. 
 * @author KiranRohankar
 *
 */
public class HaveProtocol extends Protocol
{
	/******************* Class Methods *******************/
	@Override
	public void sendProtocol(ProtocolPackage protocols, ProtocolMessage message) 
	{
		//this wont have any lines 
		
		
	} /* end sendProtocol method */

	@Override
	public void receiveProtocol(ProtocolPackage protocols, ProtocolMessage message) 
	{
		//i will update bitfield of of sender
		int senderId= ((TorrentSocket)protocols.getSocket()).getPeerID();
		 int chunkID= (((Message)message).payloadToInt());
		Host.updatePeerBitfield(senderId, chunkID);
		//then if i am interested in this piece then I will send Interested message else not interested
		if(Host.isInterested(senderId))
		{
			
			 Message msg= new Message(Message.Type.INTERESTED);
			 try {
				protocols.process(msg, Protocol.Stance.SENDING);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			 Message msg= new Message(Message.Type.NOT_INTERESTED);
			 try {
					protocols.process(msg, Protocol.Stance.SENDING);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		}
		Host.log.logReceivedHave(senderId, chunkID);
		
		
	} /* end receiveProtocol method */

} /* end ChokeProtocol class */
