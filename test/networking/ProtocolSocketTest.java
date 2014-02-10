package networking;

import static org.junit.Assert.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Random;

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
		
		public TestSocket(Socket socket, ProtocolPackage ppack) throws IOException 
		{
			super(socket, ppack);
			messages = new ArrayList<String>();
			initial = false;
			end = false;
			this.socket.setSoTimeout(2500);
		} /* end constructor */

		@Override
		public void initialProcess() {initial = true;}

		@Override
		public void endProcess() {end = true;}

		@Override
		protected ProtocolMessage definedGetMessage() {
			ProtocolMessage message = null;
			boolean again = true;
			while(again)
			{
				try {
					message = (ProtocolMessage) this.getPacket();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					again = true;
				}
				again = false;
			} /* end while loop */
			
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
				ProtocolPackage pr = new ProtocolPackage(3);
				socket = new TestSocket(serverSocket.accept(), pr);

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
	
	public static class P1 extends Protocol
	{

		@Override
		public void sendProtocol(ProtocolPackage protocols) {
			int i;
			String hello = new String("hello");
			TestMessage m = new TestMessage(null, 2, 1);
			protocols.socket.protocolSendMessage(m);
			m = new TestMessage(null, 3, 1);
			protocols.socket.protocolSendMessage(m);
			m = new TestMessage(null, 4, 1);
			protocols.socket.protocolSendMessage(m);
			m = new TestMessage(null, 5, 1);
			protocols.socket.protocolSendMessage(m);
			m = (TestMessage) protocols.socket.definedGetMessage();
			if(!hello.equals(m.message)) i = 1/0;
			
		}

		@Override
		public void receiveProtocol(ProtocolPackage protocols,
				ProtocolMessage message) {
			ProtocolSocket socket = protocols.socket;
			TestMessage m = (TestMessage) message;
			int i;
			if(m.stuff != 1) i = 1/0;
			m = (TestMessage) protocols.socket.definedGetMessage();
			if(m.stuff != 2) i = 1/0;
			m = (TestMessage) protocols.socket.definedGetMessage();
			if(m.stuff != 3) i = 1/0;
			m = (TestMessage) protocols.socket.definedGetMessage();
			if(m.stuff != 4) i = 1/0;
			m = (TestMessage) protocols.socket.definedGetMessage();
			if(m.stuff != 5) i = 1/0;
			protocols.socket.sendMessage(new TestMessage("hello", null,1));
			System.out.println("End receiveProtocol P1");
		}
	} /* end P1 */
	public static class P2 extends Protocol
	{

		@Override
		public void sendProtocol(ProtocolPackage protocols) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void receiveProtocol(ProtocolPackage protocols,
				ProtocolMessage message) {
			ProtocolSocket socket = protocols.socket;
			int i;
			TestMessage m = (TestMessage) message;
			if(m.stuff != 10) i = 1/0;
			System.out.println("End receiveProtocol P2");
		} /* end receiveProtocol method */
	} /* end P2 */
	
	public static class P3 extends Protocol
	{

		@Override
		public void sendProtocol(ProtocolPackage protocols) {
			ProtocolSocket socket = protocols.socket;
			int i;
			TestMessage m = (TestMessage) protocols.socket.definedGetMessage();
			try {
				protocols.process(m, Protocol.Stance.RECEIVING);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		@Override
		public void receiveProtocol(ProtocolPackage protocols,
				ProtocolMessage message) {
			ProtocolSocket socket = protocols.socket;
			int i;
			TestMessage m = (TestMessage) message;
			if(m.stuff != 20) i = 1/0;
			socket.protocolSendMessage(new TestMessage(null , 10, 2));
			System.out.println("End receiveProtocol P3");
		} /* end receiveProtocol method */
	} /* end  */
	
	public TestServer server;
	public TestSocket sender;
	public TestSocket receiver;
	public int port;
	public ProtocolPackage ps;
	
	@Before
	public void before()
	{
		port = 6025;
		try
		{
			ps = new ProtocolPackage(3);
			this.server = new TestServer(port);
			this.server.start();
			Thread.sleep(300);
			Socket socket = new Socket("localhost", port);
			this.sender = new TestSocket(socket, ps);
			this.receiver = server.socket;
			
			ProtocolPackage.addStaticProtocol(new P1(), 1);
			ProtocolPackage.addStaticProtocol(new P2(), 2);
			ProtocolPackage.addStaticProtocol(new P3(), 3);
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
	
	/* Test succeeds if no exceptions are thrown */
	@Test
	public void test() 
	{
		TestMessage[] messages= new TestMessage[3];
		messages[0] = new TestMessage(null, 1, 1);
		messages[1] = new TestMessage(null, 10, 2);
		messages[2] = new TestMessage(null, 20, 3);
		
		TestSocket[] sockets = new TestSocket[2];
		sockets[0] = receiver;
		sockets[1] = sender;
		receiver.start();
		sender.start();
		
		Random rand = new Random();
		int index;
		for(int i = 0; i < 200; i++)
		{
			index = Math.abs(rand.nextInt());
			sockets[index%2].sendMessage(messages[index%3]);
		} /* end loop */
		
		receiver.terminate();
		sender.terminate();
		try {
			receiver.thread.join();
			sender.thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("ProtocolSocketTest done");
	} /* end test method */

} /* end ProtocolSocketTest class */
