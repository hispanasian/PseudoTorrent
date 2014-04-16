package networking;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

import pseudoTorrent.networking.TorrentSocket;

/**
 * A ThreadedSocket that defines a thread-safe way to deal with protocols as 
 * specified by the ProtocolPackage. This class, by default, starts all sockets
 * with a timeout of 1000ms. The socket handles sent messages byt putting them
 * in a FIFO queue and processing all the queued messages before blocking to 
 * wait for a message.
 * @author Carlos Vasquez
 *
 */
public abstract class ProtocolSocket extends ThreadedSocket implements SocketInterface, ProtocolSocketInterface
{	
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
	public ProtocolSocket(Socket socket, ProtocolPackage protocols) throws IOException 
	{
		super(socket);
		this.protocols = protocols;
		this.protocols.setSocket(this);
		this.done = false;
	} /* end Constructor */
	
	public ProtocolSocket(Socket socket) throws IOException
	{
		this(socket, null);
	} /* end Constructor */
	
	/**
	 * After initialProcess, waits to receive a message
	 */
	@Override
	public final void run() 
	{
		this.initialProcess();
		
		/* The main body that will deal with incoming messages and process 
		 * them */
		ProtocolMessage message = null;
		while(!done)
		{
			message = null;

			/* Now we can atomically get the message and process it so as to
			 * prevent the following case: we get a message and the sender is
			 * expecting a specific response. However, another thread comes in 
			 * and sends a message before or during the process that conflicts
			 * with what should be sent.  */
			try
			{
				message = this.getMessage();
			} /* end try */
			catch(Exception e)
			{
				/* Do nothing */
				message = null;
			} /* end catch */
			
			if(message != null)
			{
				try 
				{
					this.protocols.process(message, Protocol.Stance.RECEIVING);
				} /* end try */
				catch (InstantiationException e)
				{
					// TODO Determine what to do
					e.printStackTrace();
				} /* end catch */
				catch( IllegalAccessException e)
				{
					// TODO Determine what to do
					e.printStackTrace();
				} /* end catch */
			} /* end if */
			
		} /* end loop */
		
		this.endProcess();

		try 
		{
			this.closeStreams();
			this.socket.close();
		} /* end try */
		catch (IOException e) {/* Do nothing */}
		
	} /* end run method */
	
	/**
	 * Sends a user-defined object that is derived from ProtocolMessage in a 
	 * thread-safe way. The user will define exactly how ProtocolSocket will
	 * send this message in the definedSendMessage method. This should only be
	 * used by non Protocol objects. 
	 * @param message	the message to be sent
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public final void sendMessage(ProtocolMessage message) throws InstantiationException, IllegalAccessException
	{
		try 
		{
			this.protocols.process(message, Protocol.Stance.SENDING);
		} /* end try */
		catch (InstantiationException e)
		{
			// TODO Determine what to do
			e.printStackTrace();
		} /* end catch */
		catch( IllegalAccessException e)
		{
			// TODO Determine what to do
			e.printStackTrace();
		} /* end catch */
		
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
		return this.definedGetMessage();
		
	} /* end getMesage method */
	
	/**
	 * This method is provided explicitly for Protocol objects. This allows the
	 * process method to bypass the queue in sendMessage. This way, the 
	 * the message that needs to be sent can be sent in the correct order. Note,
	 * this should only be used by the Protocol object that is called by this
	 * objects process method. 
	 * @param message	the message to be sent
	 */
	public final void protocolSendMessage(ProtocolMessage message)
	{
		this.definedSendMessage(message);

	} /* end protocolSendMessage method */
	
	/**
	 * This method is provided explicitly for Protocol objects. It simply 
	 * returns getMessage.
	 * @return	the ProtocolMessage from getMessage
	 */
	public final ProtocolMessage protocolGetMessage()
	{
		return this.getMessage();
	} /* end protocolGetMessage method */
	
	/**
	 * Sets the flag that the thread should terminate.
	 */
	public final void terminate()
	{
		System.out.println("Terminate called.");
		this.done = true;
		this.thread.interrupt();
		System.out.println("Terminate done.");
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