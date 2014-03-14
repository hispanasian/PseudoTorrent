package pseudoTorrent.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import networking.ProtocolPackage;



/**
 * TorrentServer will act as the server for the PseudoTorrent. It will listen 
 * on a specified port and spawn TorrentSockets as necessary. 
 * @author Carlos Vasquez
 *
 */

public class TorrentServer implements Runnable
{
	/******************* Class Attributes *******************/
	private final ServerSocket server;
	
	/******************* Class Methods *******************/
	public TorrentServer(int port) throws IOException
	{
		this.server = new ServerSocket(port);
	} /* end TorrentServer method */

	@Override
	public void run() 
	{
		Socket socket;
		ProtocolPackage protocols;
		while(!Thread.interrupted())
		{
			try 
			{
				socket = null;
				protocols = new ProtocolPackage();
				socket = server.accept();
				if(socket != null) new TorrentSocket(socket, protocols, true).start();
			} /* end try */
			catch (Exception e) 
			{
				// TODO Decide what to do for exception
				e.printStackTrace();
			} /* end catch */

		} /* end while loop */
		
	} /* end run method */
	
} /* end TorrentServer */
