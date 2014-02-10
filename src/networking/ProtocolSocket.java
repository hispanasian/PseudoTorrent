package networking;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;


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
	private AtomicInteger messagesToSend;
	private int messagesReceived;
	
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
		this.messagesToSend = new AtomicInteger(0);
		this.messagesReceived = 0;
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
	
	/**
	 * After initialProcess, waits to receive a message
	 */
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
				/* Before receiving a message, make sure there are no messages that
				 * need to be sent. If there are, wait until all messages are sent.
				 * This is done because we would like the getMessage and process
				 * methods to be atomic but getMessage blocks and thus locks out
				 * the sendMessage method. This potentially wastes much time and 
				 * has the potential to starve the sendMessage method. However, 
				 * because a timeout is implemented, we can make sure that all 
				 * messages are sent within the interval of the timeout. */
				while(this.messagesToSend.get() > 0)
				{
					try 
					{
						this.LOCK.wait();
					} /* end try */
					catch (InterruptedException e) 
					{
						// TODO Determine what to do for exception
						e.printStackTrace();
					} /* end catch */
					
					/* Prevent senders from sending */
					this.messagesReceived++;
				} /* end while loop */
				
				/* Now we can atomically get the message and process it so as to
				 * prevent the following case: we get a message and the sender is
				 * expecting a specific response. However, another thread comes in 
				 * and sends a message before or during the process that conflicts
				 * with what should be sent.  */
				message = this.getMessage();
				if(message != null)
				{
					try 
					{
						this.protocols.process(message, Protocol.Stance.RECEIVING);
					} /* end try */
					catch (InstantiationException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} /* end catch */
					catch( IllegalAccessException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} /* end catch */
				} /* end if */
				
				/* Finally, let the other threads know that they can send a 
				 * message */
				this.messagesReceived--;
				this.LOCK.notifyAll();
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
<<<<<<< HEAD
	 * send this message in the definedSendMessage method.
	 * with this method.
=======
	 * send this message in the definedSendMessage method. This should only be
	 * used by non Protocol objects
>>>>>>> 6c41786accbf1030d5f0075cd3a346174b2a93f6
	 * @param message	the message to be sent over the socket
	 */
	public final void sendMessage(ProtocolMessage message)
	{
		/* Prevent a receiver from receiving */
		this.messagesToSend.getAndIncrement();
		synchronized(this.LOCK)
		{
			/* Attempt to send a message only if no message was received */
			while(this.messagesReceived > 0) 
			{
				try 
				{
					this.LOCK.wait();
				} /* end try */
				catch (InterruptedException e) 
				{
					// TODO Determine what to do during exception
					e.printStackTrace();
				} /* end catch */
			} /* end while loop */
			
			/* Send the message */
			this.definedSendMessage(message);
			
			/* Potentially let the receive through */
			this.messagesToSend.getAndDecrement();
			this.LOCK.notifyAll();
		} /* end synchronized block */
		
	} /* end sendMessage method */
	
	/**
	 * This method is provided explicitly for Protocol objects. This allows the
	 * process method to bypass the lock on sendMessage while there is a message
	 * being processed. This is because the messagesReceived will still be 
	 * marked greater than 0 in order to atomize the getMessage and process 
	 * methods. Note, this should only be used by the Protocol object that is
	 * called by this objects process method
	 * @param message	the message to be sent
	 */
	protected final void protocolSendMessage(ProtocolMessage message)
	{
		synchronized(this.LOCK)
		{
			this.definedSendMessage(message);
		} /* end synchronized block */
	} /* end protocolSendMessage method */
	
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
