package pseudoTorrent.networking;

import host.Host;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import networking.Protocol;
import networking.ProtocolMessage;
import networking.ProtocolPackage;
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
	/******************* Class Attributes *******************/
	public final boolean isSender;
	private Integer peerID;
	private int request;	/* the previously requested piece */
	
	/******************* Class Methods *******************/
	public TorrentSocket(Integer peerID, final Socket socket, ProtocolPackage protocols, boolean isSender) throws SocketException, IOException
	{
		super(socket, protocols);
		this.peerID = peerID;
		this.socket.setSoTimeout(TIMEOUT);
		this.isSender = isSender;
	} /* end constructor */
	
	public TorrentSocket(final Socket socket, ProtocolPackage protocols, boolean isSender) throws SocketException, IOException
	{
		this(null, socket, protocols, isSender);
	} /* end constructor */

	@Override
	public void initialProcess() 
	{
		/* Note that you must use the getPacket method here to get each byte
		 * individually and similarly, use sendPacket to send each byte 
		 * individually. If getMessage, sendMessage, definedGetMessage or 
		 * definedSendMessage are used, the packet will be de-constructed 
		 * according to spec (which the handshake does not follow).
		 */
		
		if(this.isSender)
		{
			/* Sender starts handshake */
			this.sendHandshake();
			Integer peer = this.getHandshake();
			
			/* Check handshake */
			if(peer == null || this.peerID != peer)
			{
				/* Incorrect handshake, terminate */
				this.terminate();
			} /* end if */
			else
			{
				/* Correct handshake, continue */
				Message bitfield = new Message(Message.Type.BITFIELD, Host.getHostBitfield());
				this.definedSendMessage(bitfield);
				
				/* If a bitfield is received from the peer, it will be taken 
				 * care of by the BitfieldProtocol */
			} /* end else */
			
		} /* end if */
		else
		{
			/* Receiver receives first handshake */
			this.peerID = this.getHandshake();
			this.sendHandshake();
			Message bitfield = (Message) this.definedGetMessage();
			Host.add(this.peerID, this);
			
			if(bitfield.type == Message.Type.BITFIELD)
			{
				/* Process the bitfield message */
				try 
				{
					this.protocols.process(bitfield, Protocol.Stance.RECEIVING);
				} /* end try */
				catch (Exception e) 
				{
					this.terminate();
				} /* end catch */
			} /* end if */
			else
			{
				this.terminate();
			} /* end else */
			
		} /* end else */
		
		/* If there were no issues, log the connection */
		if(!this.done)
		{
			Host.log.logTCPConnection(this.peerID, isSender);
			
			/* Send bitfield if it is not empty */
			if(!Host.getHostBitfield().isEmpty())
			{
				Message bitfield = new Message(Message.Type.BITFIELD, Host.getHostBitfield());
				this.definedSendMessage(bitfield);
			} /* end if */
		} /* end if */
		
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
		for(int i = 0; i < packet.length;  i++)
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
	
	/**
	 * Returns a handshake that will be used by this Peer. A handshake has the
	 * following form: 
	 * HELLO (5 bytes) 0(23 bytes) peerID (4 bytes)
	 * 
	 * Note that this requires PeerProcess to exist and provide this method the
	 * peerID of this Peer
	 * @return	the handshake that will be used by this Peer
	 */
	public Byte[] makeHandshake()
	{
		Byte[] handshake = new Byte[32];
		
		/* Make first 4 bytes, "HELLO" */
		handshake[0] = new Byte("H");
		handshake[1] = new Byte("E");
		handshake[2] = new Byte("L");
		handshake[3] = new Byte("L");
		handshake[4] = new Byte("O");
		
		/* Make next 23 bytes: a series of 0 */
		for(int i = 5; i < 28; i++)
		{
			handshake[i] = 0;
		} /* end for loop */
		
		/* Make last bytes, the peer ID */
		byte[] peerID = Message.intToBytes(Host.getID());
		
		handshake[28] = peerID[0];
		handshake[29] = peerID[1];
		handshake[30] = peerID[2];
		handshake[31] = peerID[3];
		
		return handshake;
	} /* end makeHandshake method */
	
	/**
	 * Sends a handshake to the peer. 
	 * 
	 */
	public void sendHandshake()
	{
		Byte[] handshake = this.makeHandshake();
		
		/* Send the handshake */
		for(int i = 0; i < handshake.length; i++)
		{
			try 
			{
				this.sendPacket(handshake[i]);
			} /* end try */ 
			catch (IOException e) 
			{
				// TODO Decide what to do for exception
				e.printStackTrace();
			} /* end catch */
		} /* end for loop */
		
	} /* end sendHandhsake method */
	
	/**
	 * Get's the handshake from a Peer and returns the Integer representing the
	 * peers id or NULL if the handshake was incorrect.
	 * @return
	 */
	public Integer getHandshake()
	{
		Byte[] handshake = new Byte[32];
		Integer peerID = null;
		
		/* The handshake should be 32 bytes */
		for(int i = 0; i < 32; i++)
		{
			try 
			{
				handshake[i] = (Byte) this.getPacket();
			} /* end try */
			catch (SocketTimeoutException e) 
			{
				// TODO Determine what to do
				e.printStackTrace();
			} /* end catch */
			catch (ClassNotFoundException e) 
			{
				// TODO Determine what to do
				e.printStackTrace();
			} /* end catch */
			catch (IOException e) 
			{
				// TODO Determine what to do
				e.printStackTrace();
			} /* end catch */
		} /* end for loop */
		
		/* Check the handshake */
		Byte h = new Byte("H");
		Byte e = new Byte("E");
		Byte l = new Byte("L");
		Byte o = new Byte("O");
		
		if(h.equals(handshake[0]) &&
				e.equals(handshake[1]) &&
				l.equals(handshake[2]) &&
				l.equals(handshake[3]) &&
				o.equals(handshake[4]))
		{ /* The handshake is correct, get the peerID */
			byte[] id = new byte[4];
			id[0] = handshake[28];
			id[1] = handshake[29];
			id[2] = handshake[30];
			id[3] = handshake[31];
			peerID = Message.bytesToInt(id);
		} /* end if */
		
		return peerID;
	} /* end getHandshake method */
	
	/**
	 * Returns the peerID of the Peer this socket connects to
	 * @return	the peerID of the Peer this socket connects to
	 */
	public int getPeerID()
	{
		return this.peerID;
	} /* end getPeerID method */
	
} /* end TorrentSocket class */
