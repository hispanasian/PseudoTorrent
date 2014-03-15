package networking;

/**
 * Interface that should be used exclusively by the Protocol class. The purpose
 * is to allow each Protocol call it's own socket and bypass some restrictions, 
 * that would otherwise break the code, while still securely sending or 
 * getting messages. If a Protocol were to use another socket (a socket that is
 * not the same as the socket that called the Protocol) then it should use the
 * SocketInterface.
 * 
 * @author Carlos Vasquez
 *
 */
public interface ProtocolSocketInterface 
{
	public ProtocolMessage protocolGetMessage();
	public void protocolSendMessage(ProtocolMessage message);
	public void terminate();
	
} /* end ProtocolInterface */
