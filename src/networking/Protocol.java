package networking;


public abstract class Protocol 
{
	/******************* Class Attributes *******************/
	protected final ProtocolPackage protocols;
	
	/******************* Class Abstracts *******************/
	/**
	 * Processes the message and performs the expected function as defined by
	 * the user.
	 * @param message
	 */
	protected abstract void process(ProtocolMessage message);
	
	/******************* Class Methods *******************/
	public Protocol(ProtocolPackage protocols)
	{
		this.protocols = protocols;
	} /* end constructor */
	
} /* end Protocol class */
