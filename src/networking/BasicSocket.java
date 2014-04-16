package networking;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;

/**
 * The BasicSocket class will be used to provide some basic functionality that
 * will be used by derived classes.
 * 
 * @author Carlos Vasquez
 *
 */
public abstract class BasicSocket 
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
		this.output.flush();
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
	
	/**
	 * Sends a Serialized object over the socket
	 * @param message	the Serialized object to be sent
	 * @throws IOException
	 */
	protected void sendPacket(Serializable message) throws IOException
	{
		this.output.writeObject(message);
		this.output.flush();

	} /* end sendSocketMessage method */
	
	/**
	 * Gets a Serialized object from the socket
	 * @return	the Serialized object from the socket
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	protected Serializable getPacket() throws ClassNotFoundException, IOException, EOFException, SocketException
	{
		
		try
		{
			Object message = null;
			message = this.input.readObject();
			return (Serializable) (message);
		}
		catch(ClassNotFoundException e) 
		{
			throw e;
		}
		
		catch(EOFException e) 
		{
			throw e;
		}
		catch(SocketException e) 
		{
			throw e;
		}
		catch(IOException e) 
		{
			throw e;
		}
	} /* end getSocketMessage method */
	
	/**
	 * Sends a Byte array over the socket
	 * @param message	the Byte object to be sent
	 * @throws IOException
	 */
	protected void sendByte(byte message) throws IOException
	{
		this.output.write(message);
		this.output.flush();
	} /* end sendByte method */
	
	/**
	 * Gets a Byte array from the socket
	 * @return	the Byte object from the socket
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	protected byte getByte() throws ClassNotFoundException, IOException
	{
		Byte message = null;
		message = this.input.readByte();
		return message;
	} /* end getByte method */

} /* end TorrentSocket class */
