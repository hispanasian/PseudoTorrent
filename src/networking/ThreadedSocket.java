package networking;

import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ThreadedSocket provides the necessary functionality to use and maintain a
 * thread-safe BasicSocket
 * @author Carlos Vasquez
 *
 */
public abstract class ThreadedSocket extends BasicSocket implements Runnable
{
	/******************* Class Constants *******************/
	public final ReentrantLock WRITE_LOCK;	/* The lock on write */
	public final ReentrantLock READ_LOCK;		/* The lock on read */
	
	/******************* Class Attributes *******************/
	public final Thread thread;
	
	/******************* Class Methods *******************/
	public ThreadedSocket(Socket socket) throws IOException 
	{
		super(socket);
		this.WRITE_LOCK = new ReentrantLock();
		this.READ_LOCK = new ReentrantLock();
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
	protected final Serializable getPacket() throws ClassNotFoundException, IOException, EOFException, SocketException
	{
		synchronized(this.READ_LOCK)
		{
			try
			{
				return super.getPacket();
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
		} /* end synchronized block */
		
	} /* end getSocketMessage method */
	
	/**
	 * A thread-safe way to send a packet through the socket
	 */
	protected final void sendPacket(Serializable message) throws IOException
	{
		synchronized(this.WRITE_LOCK)
		{
			super.sendPacket(message);
		} /* end synchronized block */
		
	} /* end sendSocketMessage method */
	
	/**
	 * A thread-safe way to get a byte through the socket
	 */
	protected final byte getByte() throws ClassNotFoundException, IOException
	{
		synchronized(this.READ_LOCK)
		{
			return super.getByte();
		} /* end synchronized block */
		
	} /* end getByte method */
	
	/**
	 * A thread-safe way to send a Byte through the socket
	 */
	protected synchronized final void sendByte(Byte message) throws IOException
	{
		synchronized(this.WRITE_LOCK)
		{
			super.sendByte(message);
		} /* end synchronized block */
		
	} /* end sendByte method */
	
} /* end ThreadedSocket class */
