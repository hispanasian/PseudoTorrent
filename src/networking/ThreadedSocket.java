package networking;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

/**
 * ThreadedSocket provides the necessary functionality to use and maintain a
 * thread-safe BasicSocket
 * @author Carlos Vasquez
 *
 */
public abstract class ThreadedSocket extends BasicSocket
{
	/******************* Class Constants *******************/
	public final Object LOCK;	/* The lock to be used to stay thread-safe */
	
	/******************* Class Attributes *******************/
	protected final Thread thread;
	
	/******************* Class Methods *******************/
	public ThreadedSocket(Socket socket) throws IOException 
	{
		super(socket);
		this.LOCK = this;
		this.thread = new Thread(this);
	} /* end Constructor */
	
	/**
	 * Starts this object in a thread.
	 */
	public final void start()
	{
		this.thread.start();
	} /* end start method */
	
	/**
	 * A thread-safe way to get a packet through the socket
	 */
	protected synchronized final Serializable getPacket() throws ClassNotFoundException, IOException
	{
		return super.getPacket();
	} /* end getSocketMessage method */
	
	/**
	 * A thread-safe way to send a packet through the socket
	 */
	protected synchronized final void sendPacket(Serializable message) throws IOException
	{
		super.sendPacket(message);
	} /* end sendSocketMessage method */
	
} /* end ThreadedSocket class */
