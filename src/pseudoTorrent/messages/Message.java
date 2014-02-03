package pseudoTorrent.messages;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
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
public abstract class Message 
{
	/******************* Class Constants *******************/
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
		this.payload = this.intToBytes(payload);
	} /* end Constructor */
	
	protected Message(Type type, byte[] payload)
	{
		this.length = payload.length + 1;
		this.type = type;
		this.payload = payload;
	} /* end Constructor */
	
	protected Message(Type type)
	{
		this.length = 1;
		this.type = type;
		this.payload = null;
	} /* end Constructor */
	
	public Message(byte[] header)
	{
		
	} /* end overloaded constructor */
	
	
	public byte[] toByte()
	{
		byte[] header = null;
		
		
		return header;
	} /* end toByte method */
	
	protected byte[] intToBytes(int val)
	{
		byte[] returner = null;
		ByteBuffer buff = null; 
		
		buff = ByteBuffer.allocate(4).putInt(val);
		buff.order(ByteOrder.BIG_ENDIAN);
		returner = buff.array();
		
		return returner;
	} /* end intToBytes method */
	
} /* end Message class */
