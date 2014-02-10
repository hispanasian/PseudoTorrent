package networking;

public class Triple 
{
	/******************* Class Attributes *******************/
	public final int id;
	public Protocol protocol;
	public final Class<? extends Protocol> protocolClass;
	
	/******************* Class Constants *******************/
	/**
	 * A triple with the given id, protocol, and class
	 * @param id			the ID of the Protocol
	 * @param protocol		the Protocol
	 * @param protocolClass	the class of the Protocol
	 */
	public Triple(int id, Protocol protocol, Class<? extends Protocol> protocolClass)
	{
		this.id = id;
		this.protocol = protocol;
		this.protocolClass = protocolClass;
		
	} /* end Constructor */
	
} /* end Triple class */
