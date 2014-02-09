package pseudoTorrent.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import pseudoTorrent.PseudoTorrent;


/**
 * TorrentServer will act as the server for the PseudoTorrent. It will listen 
 * on a specified port and spawn TorrentSockets as necessary. 
 * @author Carlos Vasquez
 *
 */

public class TorrentServer implements Runnable
{
	/******************* Class Attributes *******************/
	private final PseudoTorrent torrent;
	private final ServerSocket server;
	
	/******************* Class Methods *******************/
	public TorrentServer(final PseudoTorrent torrent, int port) throws IOException
	{
		this.torrent = torrent;
		this.server = new ServerSocket(port);
	} /* end TorrentServer method */

	@Override
	public void run() 
	{
		while(!Thread.interrupted())
		{
			try 
			{
				Socket socket = null;
				socket = server.accept();
				if(socket != null) new TorrentSocket(torrent, socket, true).start();
			} /* end try */
			catch (Exception e) 
			{
				// TODO Decide what to do for exception
				e.printStackTrace();
			} /* end catch */

		} /* end while loop */
		
	} /* end run method */
	
} /* end TorrentServer */
