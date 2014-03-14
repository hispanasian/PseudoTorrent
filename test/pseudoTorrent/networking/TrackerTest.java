package pseudoTorrent.networking;

import static org.junit.Assert.*;

import host.Host;
import host.UnchokeTask;
import host.UnchokeTimer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;

import networking.ProtocolPackage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pseudoTorrent.PseudoTorrent;
import pseudoTorrent.networking.Message;
import pseudoTorrent.networking.TorrentSocket;

/**
 * Tests for TorrentSocket
 * @author Carlos
 *
 */
public class TrackerTest 
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
				socket = new TorrentSocket(torrent, serverSocket.accept(), pr, false);

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
	public static PseudoTorrent torrent;
	
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
			this.sender = new TorrentSocket(torrent, socket, ps, true);
			this.receiver = server.socket;
			Host.setup(3, 6, 10, 1000, 98);

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
			if (receiver != null) {
				receiver.terminate();
			}

			sender.thread.join();
			if (receiver != null) {
				receiver.thread.join();
			}

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
	public void testTrackerAdd() {
		Host.add(1001, server.socket);
		Host.add(1002, server.socket);
		Host.add(1003, server.socket);
		Host.add(1004, server.socket);
		Host.add(1005, server.socket);
		Host.add(1006, server.socket);
		
		System.out.println(Host.lookup.get(1001).socket);
		System.out.println(Host.lookup.size());
		Host.setInterested(1001, true);
		Host.setInterested(1002, true);
		Host.setInterested(1003, true);
		Host.setInterested(1004, true);
		Host.setInterested(1005, true);
		Host.setInterested(1006, false);
		
		System.out.println(Host.getInterested(1004));
		
		new UnchokeTimer();
		
		try {
			Thread.sleep(1000*7);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Printing top k");
		for (int i = 0; i < Host.UnchokedTopK.size(); i++) {
			System.out.println(Host.UnchokedTopK.get(i));
		}
		
		try {
			Thread.sleep(1000*7);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

} /* end TorrentSocketTest class */