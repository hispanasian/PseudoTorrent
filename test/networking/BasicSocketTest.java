package networking;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the BasicSocket class
 * @author Carlos Vasquez
 *
 */
public class BasicSocketTest {
	public static class TestSocket extends BasicSocket
	{

		public TestSocket(Socket socket) throws IOException 
		{
			super(socket);
			// TODO Auto-generated constructor stub
		}
		
	} 
	
	public TestServer server;
	public TestSocket sender;
	public TestSocket receiver;
	public int port;
	
	@Before
	public void before()
	{
		port = 6000;
		try
		{
			TestServer server = new TestServer(port);
			server.start();
			Thread.sleep(300);
			Socket socket = new Socket("localhost", port);
			sender = new TestSocket(socket);
			receiver = server.socket;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@After
	public void after()
	{
		try
		{
			sender.closeStreams();
			receiver.closeStreams();
			sender.socket.close();
			receiver.socket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Test
	public void test() 
	{
		try {
			String s1 = "This is a test";
			sender.sendPacket(s1);
			String r2 = (String) receiver.getPacket();
			assertEquals("r1 is " + s1, s1, r2);
			
			Byte sb = new Byte("50");
			sender.sendPacket(sb);
			Byte rb = (Byte) receiver.getPacket();
			assertEquals("rb is " + sb.intValue(), sb.intValue(), rb.intValue());
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
