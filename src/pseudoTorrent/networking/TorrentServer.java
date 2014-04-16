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
	private boolean done;
	
	/******************* Class Methods *******************/
	public TorrentServer(int port) throws IOException
	{
		this.server = new ServerSocket(port);
		this.done = false;
	} /* end TorrentServer method */

	@Override
	public void run() 
	{
		Socket socket;
		ProtocolPackage protocols;
		while(!done)
		{
			try 
			{
				socket = null;
				protocols = new ProtocolPackage();
				socket = server.accept();
				if(socket != null) new TorrentSocket(socket, protocols, false).start();
			} /* end try */
			catch (Exception e) 
			{
				/* terminate */
				done = true;
			} /* end catch */

		} /* end while loop */
		System.out.println("TorrentServer.run: done");
	} /* end run method */
	
	public void terminate()
	{
		try 
		{
			this.server.close();
		} /* end try */ 
		catch (IOException e) 
		{
			// ignore 
		} /* end catch */
		
		this.done = true;
	} /* end terminate method */
	
} /* end TorrentServer */
