package networking;


public interface Protocol 
{
	/******************* Class Abstracts *******************/
	/**
	 * Processes the message and performs the expected function as defined by
	 * the user.
	 * @param protocols	the ProtocolPackage that called this method
	 * @param message	the message that is to be processed
	 */
	public void process(ProtocolPackage protocols, ProtocolMessage message);
	
} /* end Protocol class */
