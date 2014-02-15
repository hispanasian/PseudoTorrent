package pseudoTorrent.networking;

import static org.junit.Assert.*;

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
import tracking.Tracker;
import tracking.UnchokeTask;
import tracking.UnchokeTimer;

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
			Tracker.setup(3, 6, 10, 1000, 98);

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
		Tracker.add(1001, server.socket);
		Tracker.add(1002, server.socket);
		Tracker.add(1003, server.socket);
		Tracker.add(1004, server.socket);
		Tracker.add(1005, server.socket);
		Tracker.add(1006, server.socket);
		
		System.out.println(Tracker.lookup.get(1001).socket);
		System.out.println(Tracker.lookup.size());
		Tracker.setInterested(1001, true);
		Tracker.setInterested(1002, true);
		Tracker.setInterested(1003, true);
		Tracker.setInterested(1004, true);
		Tracker.setInterested(1005, true);
		Tracker.setInterested(1006, false);
		
		System.out.println(Tracker.getInterested(1004));
		
		new UnchokeTimer();
		
		try {
			Thread.sleep(1000*7);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Printing top k");
		for (int i = 0; i < Tracker.UnchokedTopK.size(); i++) {
			System.out.println(Tracker.UnchokedTopK.get(i));
		}
		
		try {
			Thread.sleep(1000*7);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

} /* end TorrentSocketTest class */