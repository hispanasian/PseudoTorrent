package networking;

import java.util.ArrayList;
import java.util.Hashtable;


public abstract class ProtocolPackage 
{
	/******************* Class Constants *******************/
	protected static final int STATIC = 1;
	protected static final int NON_STATIC = 0;
	protected static final int MAP_SIZE = 7;
	
	/******************* Class Attributes *******************/
	protected ProtocolSocket socket;
	protected final ArrayList<Triple>[] protocolList;
	protected Hashtable<Integer, Tuple> lookup;
	protected ArrayList<Triple> protocols;
	protected static ArrayList<Triple> staticProtocols = new ArrayList<Triple>();
	
	/******************* Class Methods *******************/
	/**
	 * Creates a ProtocolPackage that utilizes the provided ProtocolSocket
	 * @param socket	the ProtocolSocket used by the object
	 */
	public ProtocolPackage(ProtocolSocket socket, int minNumOfProtocols)
	{
		this.socket = socket;
		this.protocolList = new ArrayList[2];
		this.protocols = new ArrayList<Triple>();
		this.protocolList[ProtocolPackage.STATIC] = staticProtocols;
		this.protocolList[ProtocolPackage.NON_STATIC] = protocols;
		this.lookup = new Hashtable<Integer, Tuple>(minNumOfProtocols);
		
	} /* end Constructor */
	
	public ProtocolPackage(int minNumOfProtocols)
	{
		this(null, minNumOfProtocols);
	} /* end Cosntructor */
	
	public ProtocolPackage(ProtocolSocket socket)
	{
		this(socket, ProtocolPackage.MAP_SIZE);
	} /* end Constructor */
	
	/**
	 * Creates a ProtocolPackage with no ProtocolSocket
	 */
	public ProtocolPackage()
	{
		this(null, ProtocolPackage.MAP_SIZE);
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
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	protected void process(ProtocolMessage message) throws InstantiationException, IllegalAccessException
	{
		Tuple tuple = lookup.get(message.getProtocolID());
		Triple triple = this.protocolList[tuple.type].get(tuple.index);
		Protocol protocol = triple.protocol;
		
		/* Lazy loading and copy on write for static */
		if(protocol == null)
		{
			protocol = triple.protocolClass.newInstance();
			this.protocolList[tuple.type].set(tuple.index, new Triple(triple.id, protocol, triple.protocolClass));
		} /* end if */
		
		/* Process the message */
		protocol.process(this, message);
		
	} /* end process method */
	
	/**
	 * Returns the ProtocolSocket that owns this object.
	 * @return	the ProtocolSocket that owns this object
	 */
	public ProtocolSocket getSocket()
	{
		return this.socket;
	} /* end getSocket method */
	
	/**
	 * Adds a Protocol to the ProtocolPackage
	 * @param protocol		the Protocol added
	 * @param protocolID	the ID of the Protocol added
	 */
	public void addProtocol(Protocol protocol, int protocolID)
	{
		/* Add protocol to List */
		Triple triple = new Triple(protocolID, protocol, protocol.getClass());
		this.protocols.add(triple);
		
		/* Add to lookup */
		Tuple tuple = new Tuple(this.protocols.indexOf(triple), ProtocolPackage.NON_STATIC);
		this.lookup.put(protocolID, tuple);
		
	} /* end addProtocol method */
	
	/**
	 * Adds a Protocol to the ProtocolPackage that will be lazy loaded
	 * @param protocolClass	the Class of the Protocol that will be lazy loaded
	 * @param protocolID	the ID of the Protocol added
	 */
	public void lazyAddProtocol(Class<? extends Protocol> protocolClass, int protocolID)
	{
		/* Add protocol to List */
		Triple triple = new Triple(protocolID, null, protocolClass);
		this.protocols.add(triple);
		
		/* Add to lookup */
		Tuple tuple = new Tuple(this.protocols.indexOf(triple), ProtocolPackage.NON_STATIC);
		this.lookup.put(protocolID, tuple);
		
	} /* end lazyAddProtocol method */
	
	/**
	 * Adds a Protocol that will be used by all ProtocolPackages
	 * @param protocol		the Protocol added
	 * @param protocolID	the ID of the Protocol added
	 */
	public static void addStaticProtocol(Protocol protocol, int protocolID)
	{
		ProtocolPackage.staticProtocols.add(new Triple(protocolID, protocol, protocol.getClass()));
	} /* end addStaticProtocol method */
	
	/**
	 * Adds a Protocol that will be used by all ProtocolPackages but will be 
	 * lazy loaded
	 * @param protocolClass	the Class of the Protocol that will be lazy loaded
	 * @param protocolID	the ID of the Protocol added
	 */
	public static void lazyAddStaticProtocol(Class<? extends Protocol> protocolClass, int protocolID)
	{
		ProtocolPackage.staticProtocols.add(new Triple(protocolID, null, protocolClass));
	} /* end lazyAddStaticProtocol method */
	
	/**
	 * Creates the lookup table that will be used to quickly find the Protocols. 
	 * Note that while all local Protocols are added automatically to the
	 * lookup table, Static Protocols are not. Hence, this method should be 
	 * called before the process() method is called whenever a static variable
	 * is added.
	 */
	public void makeMap()
	{
		int count = protocols.size();
		count += ProtocolPackage.staticProtocols.size();
		this.lookup = new Hashtable<Integer, Tuple>(count);
		
		Triple triple;
		
		for(int i = 0; i < this.protocols.size(); i++)
		{
			triple = this.protocols.get(i);
			this.lookup.put(triple.id, new Tuple(triple.id, ProtocolPackage.NON_STATIC));
		} /* end for loop */
		
		for(int i = 0; i < ProtocolPackage.staticProtocols.size(); i++)
		{
			triple = ProtocolPackage.staticProtocols.get(i);
			this.lookup.put(triple.id, new Tuple(triple.id, ProtocolPackage.NON_STATIC));
		} /* end for loop */
		
	} /* end makeMap method */
	
} /* end ProtocolPackage class */
