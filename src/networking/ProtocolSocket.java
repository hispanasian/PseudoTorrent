package networking;

import java.io.IOException;
import java.net.Socket;


/**
 * A ThreadedSocket that defines a thread-safe way to deal with protocols as 
 * specified by the ProtocolPackage.  
 * @author Carlos Vasquez
 *
 */
public abstract class ProtocolSocket extends ThreadedSocket
{
	/******************* Class Constants *******************/
	public static final int TIMEOUT = 1000;
	
	/******************* Class Attributes *******************/
	protected ProtocolPackage protocols;
	protected volatile boolean done;
	
	/******************* Class Abstracts *******************/
	/**
	 * Does some user-defined instructions before looping to accept and deal 
	 * with messages. This method is run in a synchronized block to avoid 
	 * synchronicity issues
	 */
	abstract public void initialProcess();
	
	/**
	 * Does some user-defined instructions after main loop when the process is
	 * terminated. This method is run in a synchronized block to avoid 
	 * synchronicity issues
	 */
	abstract public void endProcess();
	
	/**
	 * The definition for how an object derived from ProtocolMessage should be
	 * retrieved over the socket.
	 * 
	 * Should an exception be thrown, this method should return a null.
	 * @return	the message received over the socket
	 */
	abstract protected ProtocolMessage definedGetMessage();
	
	/**
	 * The definition for how an object derived from ProtocolMessage should be
	 * sent over the socket.
	 * @param message	the object sent over the socket
	 */
	abstract protected void definedSendMessage(ProtocolMessage message);
	
	/******************* Class Methods *******************/
	public ProtocolSocket(Socket socket, ProtocolPackage protocols, int timeout) throws IOException 
	{
		super(socket);
		this.socket.setSoTimeout(timeout);
		this.protocols = protocols;
		this.protocols.setSocket(this);
		this.done = false;
	} /* end Constructor */
	
	public ProtocolSocket(Socket socket, ProtocolPackage protocols) throws IOException 
	{
		this(socket, protocols, ProtocolSocket.TIMEOUT);
	} /* end Constructor */
	
	public ProtocolSocket(Socket socket) throws IOException
	{
		this(socket, null, ProtocolSocket.TIMEOUT);
	} /* end Constructor */
	
	public ProtocolSocket(Socket socket, int timeout) throws IOException
	{
		this(socket, null, timeout);
	} /* end Constructor */
	
	@Override
	public final void run() 
	{
		synchronized(this.LOCK)
		{
			this.initialProcess();
		} /* end synchronized block */
		
		/* The main body that will deal with incoming messages and process 
		 * them */
		ProtocolMessage message = null;
		while(!done)
		{
			message = null;
			synchronized(this.LOCK)
			{
				message = this.getMessage();
				if(message != null) this.protocols.process(message);
			} /* end synchronized block */
			
		} /* end loop */
		
		synchronized(this.LOCK)
		{
			this.endProcess();
		} /* end synchronized block */
		
		try 
		{
			this.closeStreams();
		} /* end try */
		catch (IOException e) {/* Do nothing */}
		
	} /* end run method */
	
	/**
	 * Sends a user-defined object that is derived from ProtocolMessage in a 
	 * thread-safe way. The user will define exactly how ProtocolSocket will
	 * send this message in the definedSendMessage method.
	 * with this method.
	 * @param message	the message to be sent over the socket
	 */
	public final void sendMessage(ProtocolMessage message)
	{
		synchronized(this.LOCK)
		{
			this.definedSendMessage(message);
		} /* end synchronized block */
	} /* end sendMessage method */
	
	/**
	 * Returns a user-defined object that is derived from ProtocolMessage in a
	 * thread-safe way as defined by the definedGetMesage method.
	 * 
	 * Should an exception occur and the user wishes to continue the loop, the
	 * user should return a null ProtocolMessage.
	 * @return	a message received by the socket
	 */
	public final ProtocolMessage getMessage()
	{
		synchronized(this.LOCK)
		{
			return this.definedGetMessage();
		} /* end synchronized block */
	} /* end getMesage method */
	
	/**
	 * Sets the flag that the thread should terminate.
	 */
	public final void terminate()
	{
		this.done = true;
	} /* end terminate method */
	
	/**
	 * Sets the ProtocolPackage to be used by this ProtocolSocket
	 * @param protocols	the ProtocolPackage to be used by this ProtocolSocket
	 */
	public void setProtocolPackage(ProtocolPackage protocols)
	{
		this.protocols = protocols;
	} /* end setProtocolPackage */

} /* end ProtocolSocket class */
