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
 * Tests the functionality of the ThreadedSocket. Note that this test does not
 * test the thread-safeness of the class. 
 * @author Carlos Vasquez
 *
 */
public class ThreadedSocketTest 
{
	public static class TestSocket extends ThreadedSocket
	{
		ArrayList<String> messages;
		
		public TestSocket(Socket socket) throws IOException 
		{
			super(socket);
			messages = new ArrayList<String>();
		}

		@Override
		public void run() 
		{
			try {
				this.sendPacket("message1");
				this.sendPacket("message2");
				this.sendPacket("message3");
				this.messages.add((String) this.getPacket());
				this.messages.add((String) this.getPacket());
				this.messages.add((String) this.getPacket());
				this.sendPacket("Stupid tests");
				this.sendPacket("they take forever");
				this.sendPacket("so boring");
				this.messages.add((String) this.getPacket());
				this.messages.add((String) this.getPacket());
				this.messages.add((String) this.getPacket());
				this.sendPacket("but so necessarry");
				this.sendPacket("well, not necessarry");
				this.sendPacket("if you use proofs");
				this.messages.add((String) this.getPacket());
				this.messages.add((String) this.getPacket());
				this.messages.add((String) this.getPacket());
			} catch (Exception e) {
				e.printStackTrace();
			}
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
				// TODO Decide what to do for exception
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
		port = 6010;
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
		server.socket.start();
		sender.start();
		
		try {
			server.socket.thread.join();
			sender.thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally
		{
			assertEquals("Server should have 9 messages", 9, server.socket.messages.size());
			assertEquals("Sender should have 9 messages", 9, sender.messages.size());
			for(int i = 0; i < sender.messages.size(); i++)
			{
				assertEquals("Message " + i + " is " + sender.messages.get(i), sender.messages.get(i), server.socket.messages.get(i));
			} /* end for loop */
		} /* end finally */
		
	} /* end test method */

} /* end TjreadedSpcletTest c;ass */
