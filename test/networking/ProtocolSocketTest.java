package networking;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import networking.ThreadedSocketTest.TestServer;
import networking.ThreadedSocketTest.TestSocket;

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
	public static class TestSocket extends ProtocolSocket
	{
		ArrayList<String> messages;
		
		public TestSocket(Socket socket) throws IOException 
		{
			super(socket);
			messages = new ArrayList<String>();
		}

		@Override
		public void initialProcess() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void endProcess() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected ProtocolMessage definedGetMessage() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void definedSendMessage(ProtocolMessage message) {
			// TODO Auto-generated method stub
			
		}
		
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
