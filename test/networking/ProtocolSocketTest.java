package networking;

import static org.junit.Assert.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the ProtocolSocket class. This test does not test thread-safety
 * @author Carlos Vasquez
 *
 */
public class ProtocolSocketTest 
{
	public static class TestMessage implements ProtocolMessage
	{
		private static final long serialVersionUID = 1L;
		String message;
		Integer stuff;
		int id;
		
		public TestMessage(String message, Integer stuff, int id)
		{
			this.message = message;
			this.stuff = stuff;
			this.id = id;
			
		} /* end constructor */
		
		@Override
		public Integer getProtocolID() 
		{
			return id;
		} /* end getProtocolID */
	} /* end TestMessage class */
	
	public static class TestSocket extends ProtocolSocket
	{
		ArrayList<String> messages;
		boolean initial;
		boolean end;
		
		public TestSocket(Socket socket) throws IOException 
		{
			super(socket);
			messages = new ArrayList<String>();
			initial = false;
			end = false;
		} /* end constructor */

		@Override
		public void initialProcess() {initial = true;}

		@Override
		public void endProcess() {end = true;}

		@Override
		protected ProtocolMessage definedGetMessage() {
			ProtocolMessage message = null;
			try {
				message = (ProtocolMessage) this.getPacket();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return message;
		} /* end definedGetMessage */

		@Override
		protected void definedSendMessage(ProtocolMessage message) {
			try {
				this.sendPacket(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} /* end definedSendMessage */
	} /* end TestSocket */
	
	public static class TestServer implements Runnable
	{
		public ServerSocket serverSocket;
		public TestSocket socket;
		Thread thread;
		
		public TestServer(int port) throws IOException
		{
			serverSocket = new ServerSocket(port);
		} /* end TorrentServer method */

		@Override
		public void run() 
		{
			try 
			{
				socket = new TestSocket(serverSocket.accept());

			} /* end try */
			catch (Exception e) 
			{
				e.printStackTrace();
			} /* end catch */
			
		} /* end run method */
		
		public void start()
		{
			thread = new Thread(this);
			thread.start();
		}
	} /* end TestServer */
	
	public static class R1 extends Protocol
	{

		@Override
		public void sendProtocol(ProtocolPackage protocols) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void receiveProtocol(ProtocolPackage protocols,
				ProtocolMessage message) {
			ProtocolSocket socket = protocols.socket;
			TestMessage m = (TestMessage) message;
			int i;
			if(m.stuff != 1) i = 1/0;
			
		}
	} /* end R1 */
	public static class S1 extends Protocol
	{

		@Override
		public void sendProtocol(ProtocolPackage protocols) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void receiveProtocol(ProtocolPackage protocols,
				ProtocolMessage message) {
			// TODO Auto-generated method stub
			
		} /* end receiveProtocol method */
	} /* end  */
	
	public TestServer server;
	public TestSocket sender;
	public TestSocket receiver;
	public int port;
	
	@Before
	public void before()
	{
		port = 6020;
		try
		{
			this.server = new TestServer(port);
			this.server.start();
			Thread.sleep(300);
			Socket socket = new Socket("localhost", port);
			this.sender = new TestSocket(socket);
			this.receiver = server.socket;
		} /* end try */
		catch (Exception e)
		{
			e.printStackTrace();
		} /* end catch */
	} /* end before */
	
	@After
	public void after()
	{
		try
		{
			sender.socket.close();
			receiver.socket.close();
		} /* end try */
		catch (Exception e)
		{
			e.printStackTrace();
		} /* end catch */
	} /* end after */
	@Test
	public void test() 
	{
		fail("Not yet implemented");
	} /* end test method */

} /* end ProtocolSocketTest class */
