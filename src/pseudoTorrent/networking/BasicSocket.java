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
	public BasicSocket(final Socket socket) throws IOException
	{
		this.socket = socket;
		this.createStreams();
		
	} /* end constructor */
	
	/**
	 * Creates the output and input streams
	 */
	protected final void createStreams() throws IOException
	{
		this.output = new ObjectOutputStream(this.socket.getOutputStream());
		this.input = new ObjectInputStream(this.socket.getInputStream());
	} /* end createStreams method */
	
	/**
	 * Closes the output and input streams
	 */
	protected final void closeStreams() throws IOException
	{
		this.output.close();
		this.input.close();
		
	} /* end closeServerSocket method */
	
	protected void sendSocketMessage(Serializable message) throws IOException
	{// TODO Change to correct input
		this.output.writeObject(message);
		this.output.flush();

	} /* end sendSocketMessage method */
	
	protected Serializable getSocketMessage() throws ClassNotFoundException, IOException
	{// TODO Change to correct return
		Serializable message = null;
		message = (byte) this.input.readObject();
		return(message);
	} /* end getSocketMessage method */

} /* end TorrentSocket class */
