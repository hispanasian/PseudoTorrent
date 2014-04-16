package networking;

import java.io.IOException;
import java.net.ServerSocket;
import networking.BasicSocketTest.TestSocket;

/**
 * Basic Server that helps implement socket based tests(Used in BasicSocketTest)
 * @author Carlos Vasquez
 *
 */
public class TestServer implements Runnable
{
	public ServerSocket server;
	public TestSocket socket;
	Thread thread;
	
	public TestServer(int port) throws IOException
	{
		this.server = new ServerSocket(port);
	} /* end TorrentServer method */

	@Override
	public void run() 
	{
		try 
		{
			socket = new TestSocket(server.accept());
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
