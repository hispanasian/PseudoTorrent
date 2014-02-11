package networking;

/**
 * Interface that should be used by the Protocol class
 * @author Carlos Vasquez
 *
 */
public interface ProtocolSocketInterface 
{
	public ProtocolMessage protocolGetMessage();
	public void protocolSendMessage(ProtocolMessage message);
	public void terminate();
	
} /* end ProtocolInterface */
