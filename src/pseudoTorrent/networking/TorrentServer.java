package pseudoTorrent.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import pseudoTorrent.PseudoTorrent;

/**
 * TorrentServer will act as the server for the PseudoTorrent. It will listen 
 * on a specified port and spawn TorrentSockets as necessarry. 
 * @author Carlos Vasquez
 *
 */

public class TorrentServer implements Runnable
{
	/******************* Class Attributes *******************/
	private final PseudoTorrent torrent;
	private final ServerSocket server;
	private boolean done;
	
	/******************* Class Methods 
	 * @throws IOException *******************/
	public TorrentServer(final PseudoTorrent torrent, int port) throws IOException
	{
		this.torrent = torrent;
		this.server = new ServerSocket(port);
		this.done = false;
	} /* end TorrentServer method */

	@Override
	public void run() 
	{
		while(!done)
		{
			try 
			{
				Socket socket = null;
				socket = server.accept();
				if(socket != null)
				{
					new Thread(new TorrentSocket(torrent, socket)).start();
				} /* end if */
			} 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} /* end catch */

		} /* end while loop */
		
	} /* end run method */
	
	public void close()
	{
		this.done = true;
		try 
		{
			this.server.close();
		} 
		catch (IOException e) {/* ignore */}
	} /* end close method */
	
} /* end TorrentServer */
