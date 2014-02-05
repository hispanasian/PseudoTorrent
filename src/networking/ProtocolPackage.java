package networking;

public abstract class ProtocolPackage 
{
	/******************* Class Attributes *******************/
	protected ProtocolSocket socket;
	protected Protocol[] protocols;
	
	/******************* Class Methods *******************/
	/**
	 * Although this class contain a constructor, it is up to the user to 
	 * define the protocols array.
	 * @param socket	the socket that owns this object
	 */
	protected void setSocket(ProtocolSocket socket)
	{
		this.socket = socket;
	} /* end setSocket method */
	
	/**
	 * Calls process() on the Protocol in the index in the protocols array as 
	 * chosen by the message
	 * @param message
	 */
	protected void process(ProtocolMessage message)
	{
		
	} /* end process method */
	
	/**
	 * Returns the ProtocolSocket that owns this object.
	 * @return	the ProtocolSocket that owns this object
	 */
	public ProtocolSocket getSocket()
	{
		return this.socket;
	} /* end getSocket method */
	
} /* end ProtocolPackage class */
