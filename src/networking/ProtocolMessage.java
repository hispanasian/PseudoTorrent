package networking;

import java.io.Serializable;

/**
 * A Message that is to be interpreted by a ProtocolPackage.
 * @author Carlos Vasquez
 *
 */
public interface ProtocolMessage extends Serializable
{
	/**
	 * This method will be used by ProtocolPackage as the index to the Protocol
	 * that should be called. Hence, the user should have the Protocol return 
	 * the appropriate and legal index location.
	 * @return	the position of the Protocol to be called
	 */
	public int getID();
} /* end ProtocolMessage */