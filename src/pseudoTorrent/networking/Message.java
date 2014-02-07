package pseudoTorrent.networking;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;

import networking.ProtocolMessage;

/** TODO: Test this class
 * The Message class will provide the functionality needed to implement the 8
 * different message types as per specification. Every message will have a 
 * length and type field and a message may have a payload. The break down (from
 * most significant bit to least significant bit) is as follows:
 * 
 * Length (4 bytes) | Type (1 byte) | Payload (x bytes)
 * 
 * The length does not include the size of the length field and thus is always
 * at least 1 byte. 
 * 
 * This class will be sublcassed into the 8 different types of message (which 
 * can be found in the Type enum).
 * 
 * @author Carlos Vasquez
 *
 */
public class Message implements ProtocolMessage
{
	/******************* Class Constants *******************/
	private static final long serialVersionUID = 1L;
	public static enum Type
	{
		CHOKE(0),
		UNCHOKE(1),
		INTERESTED(2),
		NOT_INTERESTED(3),
		HAVE(4),
		BITFIELD(5),
		REQUESET(6),
		PIECE(7);
		
		public final Integer val;
		
		Type (int val)
		{
			this.val = val;
		} /* end TargetCount index */
		
	} /* end Type enum */
	
	/******************* Class Attributes *******************/
	public final Integer length;
	public final Type type;
	public final byte[] payload;
	
	/******************* Class Methods *******************/
	/**
	 * Constructs a message with the given type and payload. Note that the only
	 * messages that provide an int type payload are:
	 * HAVE
	 * REQUEST
	 * PIECE
	 * According to the specification, these payloads will always be 4 bytes. 
	 * Thus, the size of the message will always be 4+1=5 bytes. The payload 
	 * itself will be converted to 
	 * @param type		the type of message
	 * @param payload	the message payload
	 */
	protected Message(Type type, int payload)
	{
		this.length = 5;
		this.type = type;
		this.payload = Message.intToBytes(payload);
	} /* end Constructor */
	
	/**
	 * Constructs a message with the given type and payload. Note that the only
	 * message that provides a BitSet type payload is:
	 * BITFIELD
	 * According to the specification, this payload will be of variable size 
	 * depending on the payload. Because the length is the size of the message
	 * no including the length field (but including the one byte type), the 
	 * length is the size of the payload + 1.
	 * @param type		the type of message
	 * @param payload	the message payload
	 */
	protected Message(Type type, BitSet payload)
	{
		this.payload = payload.toByteArray();
		this.length = 1 + this.payload.length;
		this.type = type;
		
	} /* end constructor */
	
	/**
	 * Constructs a message with the given type and payload. Note that the only
	 * message that is meant to provide a byte array as a payload is:
	 * PIECE
	 * However, this could be used for all the messages.According to the 
	 * specification, this payload will be of variable size depending on the 
	 * payload. Because the length is the size of the message no including the 
	 * length field (but including the one byte type), the length is the size 
	 * of the payload + 1.
	 * @param type		the type of message
	 * @param payload	the message payload
	 */
	protected Message(Type type, byte[] payload)
	{
		this.length = payload.length + 1;
		this.type = type;
		this.payload = payload;
	} /* end Constructor */
	
	/**
	 * Constructs a message with the given type. This message has no payload.
	 * The only messages with no payload are:
	 * CHOKE
	 * UNCHOKE
	 * INTERESTED
	 * NOT_INTERESTED
	 * PIECE
	 * The size of the message is 1: the single byte used to represent the type.
	 * @param type		the type of message
	 * @param payload	the message payload
	 */
	protected Message(Type type)
	{
		this.length = 1;
		this.type = type;
		this.payload = null;
	} /* end Constructor */
	
	/**
	 * Constructs a message based on a deconstruction of the byte array.
	 * @param message	the byte array
	 */
	public Message(Byte[] message)
	{
		this.length = message.length - 4;
		this.type = this.determineType(message[4]);
		
		byte[] payload = new byte[this.length - 1];
		
		for(int i = 0; i < (payload.length - 1); i++)
		{
			payload[i] = message[i+4];
		} /* end for loop */
		
		this.payload = payload;
		
	} /* end overloaded constructor */
	
	/**
	 * Returns a byte array representation of the message according to spec.
	 * @return	a byte array representation of the message.
	 */
	public byte[] toBytes()
	{
		/* message length is the length plus 4 to take into account the length
		 * field size */
		byte[] message = new byte[this.length + 4];
		byte[] length = Message.intToBytes(this.length);
		byte type = this.type.val.byteValue();
		message[0] = length[0];
		message[1] = length[1];
		message[2] = length[2];
		message[3] = length[3];
		message[4] = type;
		
		for(int i = 0; i < (this.length - 1); i++)
		{
			message[i+4] = this.payload[i];
		} /* end for loop */
		return message;
	} /* end toByte method */
	
	/**
	 * Returns a 4 byte array of bytes that represent val in big endian byte
	 * order where 0 is the most significant byte and 3 is the least significant
	 * byte.
	 * TODO: Verify which is the most significant byte
	 * @param val	the value to be converted to a 4 byte array
	 * @return the byte array of val in big endian order
	 */
	public static byte[] intToBytes(int val)
	{
		byte[] returner = null;
		ByteBuffer buff = null; 
		
		buff = ByteBuffer.allocate(4).putInt(val);
		buff.order(ByteOrder.BIG_ENDIAN);
		returner = buff.array();
		
		return returner;
	} /* end intToBytes method */
	
	/**
	 * Returns the Integer representation of the 4 byte byte array bytes which
	 * are expected to be in big endian order (most significant byte in position
	 * 0 and least significant byte in position 3).
	 * @param bytes	a 4 byte array in big endian order
	 * @return	the integer representation of bytes
	 */
	public static int bytesToInt(byte[] bytes)
	{
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.order(ByteOrder.BIG_ENDIAN);
		return buff.getInt();
	} /* end bytesToInt method */
	
	/**
	 * Returns the payload as an integer
	 * @return	the integer representation of the payload
	 */
	protected int payloadToInt()
	{
		return Message.bytesToInt(this.payload);
	} /* end payloadToInt method */
	
	/**
	 * Returns the payload as a BitSet
	 * @return	the payload as a BitSet
	 */
	protected BitSet payloadToBitSet()
	{
		return (BitSet.valueOf(payload));
	} /* end payloadToBitSet */
	
	/**
	 * Returns the Type with the given val
	 * @param val	the val of the type
	 * @return		the Type with the given val
	 */
	private Type determineType(int val)
	{
		Type returner = null;
		switch(val)
		{
			case 0: returner = Type.CHOKE;
			break;
			case 1: returner = Type.UNCHOKE;
			break;
			case 2: returner = Type.INTERESTED;
			break;
			case 3: returner = Type.NOT_INTERESTED;
			break;
			case 4: returner = Type.HAVE;
			break;
			case 5: returner = Type.BITFIELD;
			break;
			case 6: returner = Type.REQUESET;
			break;
			case 7: returner = Type.PIECE;
			break;
			default: returner = null;
			break;
		} /* end switch */
		
		return returner;
	} /* end determineType method */
	
	@Override
	public final int getProtocolID()
	{
		return this.type.val;
	} /* end getID method */
	
} /* end Message class */
