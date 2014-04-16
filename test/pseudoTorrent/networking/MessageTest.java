package pseudoTorrent.networking;

import static org.junit.Assert.*;

import java.util.BitSet;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.experimental.theories.*;

/**
 * JUnit test for Message class
 * @author Carlos Vasquez
 *
 */
@RunWith(Theories.class)
public class MessageTest 
{
	@DataPoints 
	public static int[] input = new int[] {1500, 0, 1, 3865450, -98, 69, -1984354};
	
	@Theory
	public void testIntPayload(int payload) 
	{
		Message.Type type = Message.Type.HAVE;
		Message test = new Message(type, payload);
		
		assertEquals("Payload must be " + payload, payload, test.payloadToInt());
		assertEquals("Type must be " + type.val, type.val, test.getProtocolID());
		assertEquals("Length must be 5", 5, test.getLength());
		
	} /* end testIntPayload */
	
	@Test
	public void testEmptyPayload()
	{
		Message.Type type = Message.Type.CHOKE;
		Message test = new Message(type);
		
		assertEquals("Payload must be empty", null, test.payload);
		assertEquals("Type must be " + type.val, type.val, test.getProtocolID());
		assertEquals("Length must be 1", 1, test.getLength());
		
	} /* end testEmptyPayload */
	
	@Theory
	public void intToBytesTest(int i)
	{
		byte[] test = Message.intToBytes(i);
		int result = Message.bytesToInt(test);
		assertEquals("Result should be " + i, i, result);
	} /* end intToBytesTest */
	
	@Theory
	public void payloadToIntTest(int payload)
	{
		Message.Type type = Message.Type.REQUEST;
		Message test = new Message(type, payload);
		assertEquals("Payload must be " + payload, payload, test.payloadToInt());
	} /* end bytesToIntTest */
	
	@Test
	public void payloadToBitSetTest()
	{
		Message.Type type = Message.Type.BITFIELD;
		BitSet payload = new BitSet(1200);
		Random rand = new Random();
		for(int i = 0; i < 1200/2; i++)
		{
			payload.flip(Math.abs(rand.nextInt()) % 1200);
		} /* end loop */
		
		Message test = new Message(type, payload);

		System.out.println("payload: " + payload.toString());
		System.out.println("test: " + test.payloadToBitSet().toString());
		assertEquals("Verifying payload", true, payload.equals(test.payloadToBitSet()));
		assertEquals("Type must be " + type.val, type.val, test.getProtocolID());
		
		// Testing empty bitset
		BitSet bits = new BitSet(100);
		System.out.println("Message.payloadToBitSetTest: bits is " + bits.toString());
		BitSet b = new BitSet(100);

	} /* end payloadToBitSet */
	
	@Theory
	public void toBytesTest1(int payload)
	{
		Message.Type type = Message.Type.PIECE;
		Message message = new Message(type, payload);
		Message test = new Message(message.toBytes());
		
		assertEquals("Payload must be " + payload, payload, test.payloadToInt());
		assertEquals("Type must be " + type.val, type.val, test.getProtocolID());
		assertEquals("Length must be 5", 5, test.getLength());
	} /* end toBytesTest */
	
	@Theory
	public void toBytesTest2()
	{
		Message.Type type = Message.Type.UNCHOKE;
		Message message = new Message(type);
		Message test = new Message(message.toBytes());
		
		assertEquals("Payload must be empty", null, test.payload);
		assertEquals("Type must be " + type.val, type.val, test.getProtocolID());
		assertEquals("Length must be 1", 1, test.getLength());
	} /* end toBytesTest */
	
	@Test
	public void toBytesTest3()
	{
		Message.Type type = Message.Type.BITFIELD;
		BitSet payload = new BitSet(1200);
		Random rand = new Random();
		for(int i = 0; i < 1200/2; i++)
		{
			payload.flip(Math.abs(rand.nextInt()) % 1200);
		} /* end loop */
		
		Message message = new Message(type, payload);
		Message test = new Message(message.toBytes());

		assertEquals("Verifying payload", true, payload.equals(test.payloadToBitSet()));
		assertEquals("Type must be " + type.val, type.val, test.getProtocolID());
		assertEquals("Length must be " + message.length, message.getLength(), test.getLength());
	} /* end toBytesTest */

} /* end MessageTest class */
