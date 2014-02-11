package networking;

/**
 * Basic Interface for interactions with ProtocolSocket or other supported 
 * sockets.
 * @author Carlos Vasquez
 *
 */
public interface SocketInterface 
{
	public ProtocolMessage getMessage();
	public void sendMessage(ProtocolMessage message) throws InstantiationException, IllegalAccessException;
	public void terminate();
	
} /* end SocketInterface interface */
