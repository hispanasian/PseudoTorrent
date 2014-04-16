package pseudoTorrent.networking;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import networking.ProtocolPackage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests for TorrentSocket
 * @author Carlos
 *
 */
public class TorrentSocketTest 
{
	public static class TestServer implements Runnable
	{
		public ServerSocket serverSocket;
		public TorrentSocket socket;
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
				ProtocolPackage pr = new ProtocolPackage(3);
				socket = new TorrentSocket(serverSocket.accept(), pr, false);

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
	public TorrentSocket sender;
	public TorrentSocket receiver;
	public int port;
	public ProtocolPackage ps;

	
	@Before
	public void before()
	{
		port = 6030;
		try
		{
			ps = new ProtocolPackage(3);
			this.server = new TestServer(port);
			this.server.start();
			Thread.sleep(300);
			Socket socket = new Socket("localhost", port);
			this.sender = new TorrentSocket(socket, ps, true);
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
			sender.terminate();
			receiver.terminate();
			sender.thread.join();
			receiver.thread.join();
		} /* end try */
		catch (Exception e)
		{
			e.printStackTrace();
		} /* end catch */
	} /* end after */
	
	/**
	 * Tests the definedGetMessage and definedSendMessage methods
	 */
	@Test 
	public void testDefinedGetandSet()
	{
		Message message = new Message(Message.Type.HAVE, 512);
		Message received;
		
		sender.definedSendMessage(message);
		received = (Message) receiver.definedGetMessage();
		
		assertEquals("Recieved has type " + message.type, message.type, received.type);
		assertEquals("received has length " + message.getLength(), message.getLength(), received.getLength());
		assertEquals("Received has payload " + message.payloadToInt(), message.payloadToInt(), received.payloadToInt());
		
		System.out.println("Finished TorrentSocketTest");
	} /* end definedGetMessageTest method */
	

} /* end TorrentSocketTest class */
