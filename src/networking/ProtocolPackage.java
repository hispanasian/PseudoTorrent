package networking;

import java.util.ArrayList;
import java.util.Hashtable;


public abstract class ProtocolPackage 
{
	/******************* Class Constants *******************/
	protected static final int STATIC = 1;
	protected static final int NON_STATIC = 0;
	
	/******************* Class Attributes *******************/
	protected ProtocolSocket socket;
	protected final ArrayList<Protocol>[] protocolList;
	protected ArrayList<Protocol> protocols;
	protected static ArrayList<Protocol> staticProtocols = new ArrayList<Protocol>();
	protected Hashtable<Integer, Tuple> lookup;
	
	/******************* Class Methods *******************/
	public ProtocolPackage(ProtocolSocket socket)
	{
		this.socket = socket;
		this.protocolList = new ArrayList[2];
		this.protocols = new ArrayList<Protocol>();
		this.protocolList[ProtocolPackage.STATIC] = staticProtocols;
		this.protocolList[ProtocolPackage.NON_STATIC] = protocols;
		this.lookup = null;
	} /* end Constructor */
	
	public ProtocolPackage()
	{
		this(null);
	} /* end Constructor */
	
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
	
	public void addProtocol(Protocol protocol)
	{
		
	} /* end addProtocol method */
	
	public static void addStaticProtocl(Protocol protocol)
	{
		
	} /* end addStaticProtocol */
	
	/**
	 * Creates the lookup table that will be used to quickly find the Protocols. 
	 * This method must always be called before the process() is called.
	 */
	public void makeMap()
	{
		if(this.lookup == null) 
		{
			int count = protocols.size();
			count += ProtocolPackage.staticProtocols.size();
			
			this.lookup = new Hashtable<Integer, Tuple>(count);
		} /* end if */
		
	} /* end makeMap method */
	
} /* end ProtocolPackage class */
