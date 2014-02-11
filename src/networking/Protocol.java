package networking;

/**
 * The Protocol is an abstract class used by ProtocolPackage to decide what
 * should be done. 
 * 
 * @author Carlos Vasquez
 *
 */
public abstract class Protocol 
{
	public static enum Stance
	{
		SENDING,
		RECEIVING;
	} /* end Stance */
	/******************* Class Abstracts *******************/
	/**
	 * Processes the message and performs the expected function as defined by
	 * the user.
	 * @param protocols	the ProtocolPackage that called this method
	 * @param message	the message that is to be processed
	 */
	public void process(ProtocolPackage protocols, ProtocolMessage message, Stance stance)
	{
		switch(stance)
		{
			case SENDING: this.startSend(protocols, message);
				break;
			case RECEIVING: this.receiveProtocol(protocols, message);
				break;
			default: /* Do nothing */
				break;
		} /* end switch */
		
	} /* end process method */
	
	/**
	 * Sends the message and then calls sendProtocol
	 * @param protocols	the ProtocolPackage
	 * @param message	the message to be sent
	 */
	protected void startSend(ProtocolPackage protocols, ProtocolMessage message)
	{
		protocols.socket.protocolSendMessage(message);
		this.sendProtocol(protocols, message);
	} /* end startSend method */
	
	/**
	 * Defines the protocol that should be followed when this message is being
	 * sent.The purpose of sendProtocol is to atomize the process that occurs 
	 * after sending. The issue happens when multiple independent messages are 
	 * sent. The buffer on the receiver gets filled with multiple independent 
	 * messages. If one of the messages in the buffer is processed, there is an
	 * issue if the protocol that processes it expects a particular message. 
	 * When getMessage is called, it will get the next message (a random 
	 * independent message) as opposed to the expected message. By providing 
	 * the user the ability to define the protocol to be followed by 
	 * sendMessage, the user can avoid this issue. If the user knows that no 
	 * message is expected to follow the sent message, then the user can just
	 * leave this method blank. If the user expects a particular protocol to be
	 * called following this message, the user should have the following code:
	 * 
	 * ProtocolMessage = protocols.socket.getMessage();
	 * protocols.process(message, Protocol.Stance.RECEIVING);
	 * 
	 * If the user expects any other behavior, the user can define it here.
	 * @param protocols	the ProtocolPackage
	 */
	public abstract void sendProtocol(ProtocolPackage protocols, ProtocolMessage message);
	
	/**
	 * Defines the protocol that should be followed after the message is sent.
	 * @param protocols	the ProtocolPackage
	 * @param message	the message received
	 */
	public abstract void receiveProtocol(ProtocolPackage protocols, ProtocolMessage message);
	
} /* end Protocol class */
