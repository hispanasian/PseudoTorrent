package networking;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import pseudoTorrent.networking.*;

/**
 * JUnit test for ProtocolPackage class
 * @author Carlos Vasquez
 *
 */
public class ProtocolPackageTest 
{
	public class PackageTest extends ProtocolPackage 
	{
		public PackageTest(ProtocolSocket socket, int i)
		{
			super(socket, i);
		}
	} /* end PackageTest class */

	public class TestProtocol implements Protocol
	{
		@Override
		public void process(ProtocolPackage protocols, ProtocolMessage message)
		{
			int i = 5/0;
		}
	} /* end TestProtocol class */
	
	public class TestMessage implements ProtocolMessage
	{
		int id;
		public TestMessage(int id) {this.id = id;}
		@Override
		public Integer getProtocolID() {return this.id;}
		
	} /* end TestMessage class */
	
	/**
	 * Resets the static states of ProtocolPackage
	 */
	@Before
	public void resetStaticStates()
	{
		ProtocolPackage.staticProtocols = new ArrayList<Triple>();
		ProtocolPackage.packages = new ArrayList<ProtocolPackage>();
	}
	
	/**
	 * Tests process method
	 */
	@Test(expected=ArithmeticException.class)
	public void processTest1() 
	{
		PackageTest test = new PackageTest(null, 10);
		test.addProtocol(new TestProtocol(), 5);
		try {
			test.process(new TestMessage(5));
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	/**
	 * Tests process method lazy load. If no null point exception is thrown, 
	 * it worked
	 */
	@Test
	public void processTest2() 
	{
		PackageTest test = new PackageTest(null, 10);
		ProtocolPackage.lazyAddStaticProtocol(ChokeProtocol.class, 6);	
		
		try {
			test.process(new TestMessage(6));
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals("protocol should have size 0", 0, test.protocols.size());
		assertEquals("staticProtocols should have size 1", 1, PackageTest.staticProtocols.size());
		assertEquals("lookup should have size 1", 1, test.lookup.size());
		assertEquals("lookup with key 6 is static", PackageTest.STATIC, test.lookup.get(6).type);
		assertEquals("lookup with key 6 has class ChokeProtocol", ChokeProtocol.class, ProtocolPackage.staticProtocols.get(test.lookup.get(6).index).protocolClass);
		assertEquals("lookup with key 6 has protocol of class ChokeProtocol", ChokeProtocol.class, test.staticProtocols.get(test.lookup.get(6).index).protocol.getClass());
	}
	
	/**
	 * Tests the addPrtocol method
	 */
	@Test
	public void addProtocolTest()
	{
		PackageTest test = new PackageTest(null, 7);
		test.addProtocol(new ChokeProtocol(), 10);
		test.addProtocol(new UnchokeProtocol(), 3);
		test.addProtocol(new BitfieldProtocol(), 15);
		
		assertEquals("protocol should have size 3", 3, test.protocols.size());
		assertEquals("staticProtocols should have size 0", 0, PackageTest.staticProtocols.size());
		assertEquals("lookup should have size 3", 3, test.lookup.size());
		assertEquals("lookup with key 10 is non static", PackageTest.NON_STATIC, test.lookup.get(10).type);
		assertEquals("lookup with key 10 has class ChokeProtocol", ChokeProtocol.class, test.protocols.get(test.lookup.get(10).index).protocolClass);
		assertEquals("lookup with key 10 has protocol of class ChokeProtocol", ChokeProtocol.class, test.protocols.get(test.lookup.get(10).index).protocol.getClass());
		
		assertEquals("lookup with key 3 is non static", PackageTest.NON_STATIC, test.lookup.get(3).type);
		assertEquals("lookup with key 3 has class UnchokeProtocol", UnchokeProtocol.class, test.protocols.get(test.lookup.get(3).index).protocolClass);
		assertEquals("lookup with key 3 has protocol of class UnchokeProtocol", UnchokeProtocol.class, test.protocols.get(test.lookup.get(3).index).protocol.getClass());
		
		assertEquals("lookup with key 15 is non static", PackageTest.NON_STATIC, test.lookup.get(10).type);
		assertEquals("lookup with key 15 has class BitfieldProtocol", BitfieldProtocol.class, test.protocols.get(test.lookup.get(15).index).protocolClass);
		assertEquals("lookup with key 15 has protocol of class BitfieldProtocol", BitfieldProtocol.class, test.protocols.get(test.lookup.get(15).index).protocol.getClass());
	} 
	
	/**
	 * Tests the addLazyProtocol method
	 */
	@Test
	public void addLazyProtocolTest()
	{
		PackageTest test = new PackageTest(null, 7);
		test.lazyAddProtocol(ChokeProtocol.class, 10);
		test.lazyAddProtocol(UnchokeProtocol.class, 3);
		test.lazyAddProtocol(BitfieldProtocol.class, 15);
		
		assertEquals("protocol should have size 3", 3, test.protocols.size());
		assertEquals("staticProtocols should have size 0", 0, PackageTest.staticProtocols.size());
		assertEquals("lookup should have size 3", 3, test.lookup.size());
		
		assertEquals("lookup with key 10 is non static", PackageTest.NON_STATIC, test.lookup.get(10).type);
		assertEquals("lookup with key 10 has class ChokeProtocol", ChokeProtocol.class, test.protocols.get(test.lookup.get(10).index).protocolClass);
		assertEquals("lookup with key 10 has protocol of null", null, test.protocols.get(test.lookup.get(10).index).protocol);
		
		assertEquals("lookup with key 3 is non static", PackageTest.NON_STATIC, test.lookup.get(3).type);
		assertEquals("lookup with key 3 has class UnchokeProtocol", UnchokeProtocol.class, test.protocols.get(test.lookup.get(3).index).protocolClass);
		assertEquals("lookup with key 3 has protocol of null", null, test.protocols.get(test.lookup.get(3).index).protocol);
		
		assertEquals("lookup with key 15 is non static", PackageTest.NON_STATIC, test.lookup.get(10).type);
		assertEquals("lookup with key 15 has class BitfieldProtocol", BitfieldProtocol.class, test.protocols.get(test.lookup.get(15).index).protocolClass);
		assertEquals("lookup with key 15 has protocol of null", null, test.protocols.get(test.lookup.get(15).index).protocol);
	}
	
	/**
	 * Tests the addStaticProtocol method
	 */
	@Test
	public void addStaticProtocol1()
	{
		PackageTest test = new PackageTest(null, 7);
		ProtocolPackage.addStaticProtocol(new HaveProtocol(), 0);
		ProtocolPackage.addStaticProtocol(new InterestedProtocol(), -4);
		ProtocolPackage.addStaticProtocol(new NotInterestedProtocol(), 6546);
		ProtocolPackage.addStaticProtocol(new PieceProtocol(), -50);
		ProtocolPackage.addStaticProtocol(new RequestProtocol(), 1);
		
		assertEquals("protocol should have size 0", 0, test.protocols.size());
		assertEquals("staticProtocols should have size 5", 5, PackageTest.staticProtocols.size());
		assertEquals("lookup should have size 5", 5, test.lookup.size());
		
		assertEquals("lookup with key 0 is static", PackageTest.STATIC, test.lookup.get(0).type);
		assertEquals("lookup with key 0 has class HaveProtocol", HaveProtocol.class, test.staticProtocols.get(test.lookup.get(0).index).protocolClass);
		assertEquals("lookup with key 0 has protocol of class HaveProtocol", HaveProtocol.class, test.staticProtocols.get(test.lookup.get(0).index).protocol.getClass());
		
		assertEquals("lookup with key -4 is static", PackageTest.STATIC, test.lookup.get(-4).type);
		assertEquals("lookup with key -4 has class InterestedProtocol", InterestedProtocol.class, test.staticProtocols.get(test.lookup.get(-4).index).protocolClass);
		assertEquals("lookup with key -4 has protocol of class InterestedProtocol", InterestedProtocol.class, test.staticProtocols.get(test.lookup.get(-4).index).protocol.getClass());
		
		assertEquals("lookup with key 6546 is static", PackageTest.STATIC, test.lookup.get(6546).type);
		assertEquals("lookup with key 6546 has class NotInterestedProtocol", NotInterestedProtocol.class, test.staticProtocols.get(test.lookup.get(6546).index).protocolClass);
		assertEquals("lookup with key 6546 has protocol of class NotInterestedProtocol", NotInterestedProtocol.class, test.staticProtocols.get(test.lookup.get(6546).index).protocol.getClass());
	
		assertEquals("lookup with key -50 is static", PackageTest.STATIC, test.lookup.get(-50).type);
		assertEquals("lookup with key -50 has class PieceProtocol", PieceProtocol.class, test.staticProtocols.get(test.lookup.get(-50).index).protocolClass);
		assertEquals("lookup with key -50 has protocol of class PieceProtocol", PieceProtocol.class, test.staticProtocols.get(test.lookup.get(-50).index).protocol.getClass());
	
		assertEquals("lookup with key 1 is static", PackageTest.STATIC, test.lookup.get(-4).type);
		assertEquals("lookup with key 1 has class RequestProtocol", RequestProtocol.class, test.staticProtocols.get(test.lookup.get(1).index).protocolClass);
		assertEquals("lookup with key 1 has protocol of class RequestProtocol", RequestProtocol.class, test.staticProtocols.get(test.lookup.get(1).index).protocol.getClass());
	}
	
	/**
	 * Esnure all ProtocolPackage objects obtain the static protocol
	 */
	@Test
	public void addStaticProtocol2()
	{
		PackageTest test1 = new PackageTest(null, 7);
		ProtocolPackage.addStaticProtocol(new HaveProtocol(), 0);
		ProtocolPackage.addStaticProtocol(new InterestedProtocol(), -4);

		PackageTest test2 = new PackageTest(null, 10);
		ProtocolPackage.addStaticProtocol(new PieceProtocol(), -50);
		
		assertEquals("test1 should have protocol size 0", 0, test1.protocols.size());
		assertEquals("test1 should have staticProtocol size 3", 3, test1.staticProtocols.size());
		assertEquals("test1 should have lookup size 3", 3, test1.lookup.size());
		
		assertEquals("test2 should have protocol size 0", 0, test2.protocols.size());
		assertEquals("test2 should have staticProtocol size 3", 3, test2.staticProtocols.size());
		assertEquals("test2 should have lookup size 3", 3, test2.lookup.size());
		
		assertEquals("test1: lookup with key 0 is static", PackageTest.STATIC, test1.lookup.get(0).type);
		assertEquals("test1: lookup with key 0 has class HaveProtocol", HaveProtocol.class, test1.staticProtocols.get(test1.lookup.get(0).index).protocolClass);
		assertEquals("test1: lookup with key 0 has protocol of class HaveProtocol", HaveProtocol.class, test1.staticProtocols.get(test1.lookup.get(0).index).protocol.getClass());
		
		assertEquals("test1: lookup with key -4 is static", PackageTest.STATIC, test1.lookup.get(-4).type);
		assertEquals("test1: lookup with key -4 has class InterestedProtocol", InterestedProtocol.class, test1.staticProtocols.get(test1.lookup.get(-4).index).protocolClass);
		assertEquals("test1: lookup with key -4 has protocol of class InterestedProtocol", InterestedProtocol.class, test1.staticProtocols.get(test1.lookup.get(-4).index).protocol.getClass());
		
		assertEquals("test1: lookup with key -50 is static", PackageTest.STATIC, test1.lookup.get(-50).type);
		assertEquals("test1: lookup with key -50 has class PieceProtocol", PieceProtocol.class, test1.staticProtocols.get(test1.lookup.get(-50).index).protocolClass);
		assertEquals("test1: lookup with key -50 has protocol of class PieceProtocol", PieceProtocol.class, test1.staticProtocols.get(test1.lookup.get(-50).index).protocol.getClass());
	
		assertEquals("test2: lookup with key 0 is static", PackageTest.STATIC, test2.lookup.get(0).type);
		assertEquals("test2: lookup with key 0 has class HaveProtocol", HaveProtocol.class, test2.staticProtocols.get(test2.lookup.get(0).index).protocolClass);
		assertEquals("test2: lookup with key 0 has protocol of class HaveProtocol", HaveProtocol.class, test2.staticProtocols.get(test2.lookup.get(0).index).protocol.getClass());
		
		assertEquals("test2: lookup with key -4 is static", PackageTest.STATIC, test2.lookup.get(-4).type);
		assertEquals("test2: lookup with key -4 has class InterestedProtocol", InterestedProtocol.class, test2.staticProtocols.get(test2.lookup.get(-4).index).protocolClass);
		assertEquals("test2: lookup with key -4 has protocol of class InterestedProtocol", InterestedProtocol.class, test2.staticProtocols.get(test2.lookup.get(-4).index).protocol.getClass());
		
		assertEquals("test2: lookup with key -50 is static", PackageTest.STATIC, test2.lookup.get(-50).type);
		assertEquals("test2: lookup with key -50 has class PieceProtocol", PieceProtocol.class, test2.staticProtocols.get(test2.lookup.get(-50).index).protocolClass);
		assertEquals("test2: lookup with key -50 has protocol of class PieceProtocol", PieceProtocol.class, test2.staticProtocols.get(test2.lookup.get(-50).index).protocol.getClass());
	}
	
	/**
	 * Tests the addLazyStaticProtocol method
	 */
	@Test
	public void addLazyStaticProtocol()
	{
		PackageTest test1 = new PackageTest(null, 7);
		ProtocolPackage.lazyAddStaticProtocol(HaveProtocol.class, 0);
		ProtocolPackage.lazyAddStaticProtocol(InterestedProtocol.class, -4);

		PackageTest test2 = new PackageTest(null, 10);
		ProtocolPackage.lazyAddStaticProtocol(PieceProtocol.class, -50);
		
		assertEquals("test1 should have protocol size 0", 0, test1.protocols.size());
		assertEquals("test1 should have staticProtocol size 3", 3, test1.staticProtocols.size());
		assertEquals("test1 should have lookup size 3", 3, test1.lookup.size());
		
		assertEquals("test2 should have protocol size 0", 0, test2.protocols.size());
		assertEquals("test2 should have staticProtocol size 3", 3, test2.staticProtocols.size());
		assertEquals("test2 should have lookup size 3", 3, test2.lookup.size());
		
		assertEquals("test1: lookup with key 0 is static", PackageTest.STATIC, test1.lookup.get(0).type);
		assertEquals("test1: lookup with key 0 has class HaveProtocol", HaveProtocol.class, test1.staticProtocols.get(test1.lookup.get(0).index).protocolClass);
		assertEquals("test1: lookup with key 0 has protocol of null", null, test1.staticProtocols.get(test1.lookup.get(0).index).protocol);
		
		assertEquals("test1: lookup with key -4 is static", PackageTest.STATIC, test1.lookup.get(-4).type);
		assertEquals("test1: lookup with key -4 has class InterestedProtocol", InterestedProtocol.class, test1.staticProtocols.get(test1.lookup.get(-4).index).protocolClass);
		assertEquals("test1: lookup with key -4 has protocol of null", null, test1.staticProtocols.get(test1.lookup.get(-4).index).protocol);
		
		assertEquals("test1: lookup with key -50 is static", PackageTest.STATIC, test1.lookup.get(-50).type);
		assertEquals("test1: lookup with key -50 has class PieceProtocol", PieceProtocol.class, test1.staticProtocols.get(test1.lookup.get(-50).index).protocolClass);
		assertEquals("test1: lookup with key -50 has protocol of null", null, test1.staticProtocols.get(test1.lookup.get(-50).index).protocol);
	
		assertEquals("test2: lookup with key 0 is static", PackageTest.STATIC, test2.lookup.get(0).type);
		assertEquals("test2: lookup with key 0 has class HaveProtocol", HaveProtocol.class, test2.staticProtocols.get(test2.lookup.get(0).index).protocolClass);
		assertEquals("test2: lookup with key 0 has protocol of null", null, test2.staticProtocols.get(test2.lookup.get(0).index).protocol);
		
		assertEquals("test2: lookup with key -4 is static", PackageTest.STATIC, test2.lookup.get(-4).type);
		assertEquals("test2: lookup with key -4 has class InterestedProtocol", InterestedProtocol.class, test2.staticProtocols.get(test2.lookup.get(-4).index).protocolClass);
		assertEquals("test2: lookup with key -4 has protocol of null", null, test2.staticProtocols.get(test2.lookup.get(-4).index).protocol);
		
		assertEquals("test2: lookup with key -50 is static", PackageTest.STATIC, test2.lookup.get(-50).type);
		assertEquals("test2: lookup with key -50 has class PieceProtocol", PieceProtocol.class, test2.staticProtocols.get(test2.lookup.get(-50).index).protocolClass);
		assertEquals("test2: lookup with key -50 has protocol of null", null, test2.staticProtocols.get(test2.lookup.get(-50).index).protocol);
	}

}
