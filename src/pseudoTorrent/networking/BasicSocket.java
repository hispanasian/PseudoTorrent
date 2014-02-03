package pseudoTorrent.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * The BasicSocket class will be used to provide some basic functionality that
 * will be used by derived classes.
 * 
 * @author Carlos Vasquez
 *
 */
public abstract class BasicSocket implements Runnable
{
	/******************* Class Methods *******************/
	protected final Socket socket;
	private ObjectInputStream input = null;
	private ObjectOutputStream output = null;
	
	/******************* Class Methods *******************/
	public BasicSocket(final Socket socket)
	{
		this.socket = socket;
		this.createStreams();
		
	} /* end constructor */
	
	/**
	 * Creates the output and input streams
	 */
	protected final void createStreams()
	{
		try 
		{
			this.output = new ObjectOutputStream(this.socket.getOutputStream());
			this.input = new ObjectInputStream(this.socket.getInputStream());
		} /* end try */ 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /* end catch */
	} /* end createStreams method */
	
	/**
	 * Closes the output and input streams
	 */
	protected final void closeStreams()
	{
		try 
		{
			this.output.close();
			this.input.close();
		} /* end try */ 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /* end catch */
		
	} /* end closeServerSocket method */
	
	protected void sendSocketMessage(Serializable message)
	{// TODO Change to correct input
		try 
		{
			this.output.writeObject(message);
			this.output.flush();
		} /* end try */
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /* end catch */
		
	} /* end sendSocketMessage method */
	
	protected Serializable getSocketMessage()
	{// TODO Change to correct return
		Serializable message = null;
		
		try
		{
			message = (byte) this.input.readObject();
		} /* end try */
		catch(IOException | ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /* end catch */
		
		return(message);
	} /* end getSocketMessage method */

} /* end TorrentSocket class */
