package pseudoTorrent.networking;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import pseudoTorrent.PseudoTorrent;
import networking.ProtocolMessage;
import networking.ProtocolSocket;


/**
 * The TorrentSocket class will perform the socket functionality necessary for
 * the PseudoTorrent program. It will be used to send messages and receive and 
 * interpret messages by performing the necessary protocol as defined by the 
 * Protocols class. 
 * 
 * @author Carlos Vasquez
 *
 */
public class TorrentSocket extends ProtocolSocket
{
	/******************* Class Constants *******************/
	public static final int TIMEOUT = 1000;
	
	/******************* Class Attributes *******************/
	public final PseudoTorrent torrent;
	public final boolean isSender;
	
	/******************* Class Methods *******************/
	public TorrentSocket(PseudoTorrent torrent, final Socket socket, boolean isSender) throws SocketException, IOException
	{
		super(socket);
		this.torrent = torrent;
		this.socket.setSoTimeout(TIMEOUT);
		this.isSender = isSender;
	} /* end constructor */

	@Override
	public void initialProcess() 
	{
		// TODO Implement handshake here
		
	} /* end initialProcess */

	@Override
	public void endProcess() 
	{
		// TODO: nothing?
	} /* end process */

	/**
	 * Receives a stream of bytes (at least 5) and based on the first 4 bytes, 
	 * receives x more bytes that represent the payload of the message.
	 */
	@Override
	protected ProtocolMessage definedGetMessage() 
	{
		ProtocolMessage message = null;
		// TODO: Define what happens during exception
		byte[] length = new byte[4];
		ArrayList<Byte> packets = new ArrayList<Byte>();
		try 
		{
			/* First, get the length (first 4 packets ie 4 bytes) */
			length[0] = (Byte) this.getPacket();
			length[1] = (Byte) this.getPacket();
			length[2] = (Byte) this.getPacket();
			length[3] = (Byte) this.getPacket();
			packets.add(length[0]);
			packets.add(length[1]);
			packets.add(length[2]);
			packets.add(length[3]);
			
			/* Second, get the type (5th packet, ie 5th bit) */
			packets.add((Byte) this.getPacket());
			
			/* Third, get the payload */
			for(int i = 0; i < (Message.bytesToInt(length) - 1); i++)
			{
				packets.add((Byte) this.getPacket());
			} /* end for loop */
			
			/* Finally, make into one array */
			Byte[] mssg = new Byte[packets.size()];
			packets.toArray(mssg);
			message = new Message(mssg);
			
		} /* end try */
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /* end catch */
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /* end catch */
		
		return message;
	} /* end definedGetMessage method */

	/**
	 * Sends the Message by getting the byte representation of the Message (as
	 * defined by Message) and sending the bytes to the receiver. 
	 */
	@Override
	protected void definedSendMessage(ProtocolMessage message) 
	{
		byte[] packet = ((Message) message).toBytes();
		for(int i = 0; i < packet.length; i++)
		{// TODO: Define what happens during exception
			try 
			{
				this.sendPacket(packet[i]);
			} /* end try */
			catch (IOException e) 
			{
				e.printStackTrace();
			} /* end catch */
			
		} /* end for loop */
		
	} /* end definedSendMessage method */
	
} /* end TorrentSocket class */
