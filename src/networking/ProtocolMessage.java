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
	 * This method will be used by ProtocolPackage as a unique identifier for a
	 * Protocol being queried. Each Protocol ID should be unique.
	 * @return	the ID of the Protocol to be called
	 */
	public Integer getProtocolID();
} /* end ProtocolMessage */
