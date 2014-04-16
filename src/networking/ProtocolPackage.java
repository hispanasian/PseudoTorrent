package networking;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Provides the ability to add protocols (statically or non statically) to the
 * object and utilize the protocols that were added through the process method.
 * Each protocol must be given a unique integer value that is unique regardless
 * of being static or non static. The process method will be called by 
 * providing providing the message to be processed which will implement the
 * ProtocolMessage and provide the unique ID. The class also supports lazy 
 * loading. One must simply pass in the class of the object that should be
 * loaded.
 * @author Carlos Vasquez
 *
 */
public class ProtocolPackage 
{
	/******************* Class Constants *******************/
	protected static final int STATIC = 1;
	protected static final int NON_STATIC = 0;
	protected static final int MAP_SIZE = 7;
	
	/******************* Class Attributes *******************/
	protected ProtocolSocketInterface socket;
	protected final ArrayList<Triple>[] protocolList;
	protected Hashtable<Integer, Tuple> lookup;
	protected ArrayList<Triple> protocols;
	protected static ArrayList<Triple> staticProtocols = new ArrayList<Triple>();
	protected static ArrayList<ProtocolPackage> packages = new ArrayList<ProtocolPackage>();
	
	/******************* Class Methods *******************/
	/**
	 * Creates a ProtocolPackage that utilizes the provided 
	 * ProtocolSocketInterface
	 * @param socket	the ProtocolSocketInterface used by the object
	 */
	public ProtocolPackage(ProtocolSocketInterface socket, int minNumOfProtocols)
	{
		this.socket = socket;
		this.protocolList = new ArrayList[2];
		this.protocols = new ArrayList<Triple>();
		this.protocolList[ProtocolPackage.STATIC] = staticProtocols;
		this.protocolList[ProtocolPackage.NON_STATIC] = protocols;
		
		/* Map the static protocols */
		synchronized(ProtocolPackage.class)
		{
			this.lookup = new Hashtable<Integer, Tuple>(minNumOfProtocols);
			ProtocolPackage.packages.add(this);
			Triple triple = null;
			for(int i = 0; i < staticProtocols.size(); i++)
			{
				triple = ProtocolPackage.staticProtocols.get(i);
				this.lookup.put(triple.id, new Tuple(i, ProtocolPackage.STATIC));
			} /* end for loop */
			
		} /* end synchronized block */
		
	} /* end Constructor */
	
	public ProtocolPackage(int minNumOfProtocols)
	{
		this(null, minNumOfProtocols);
	} /* end Cosntructor */
	
	public ProtocolPackage(ProtocolSocketInterface socket)
	{
		this(socket, ProtocolPackage.MAP_SIZE);
	} /* end Constructor */
	
	/**
	 * Creates a ProtocolPackage with no ProtocolSocketInterface
	 */
	public ProtocolPackage()
	{
		this(null, ProtocolPackage.MAP_SIZE);
	} /* end Constructor */
	
	/**
	 * Returns the ProtocolSocketInterface that owns this object.
	 * @return	the ProtocolSocketInterface that owns this object
	 */
	public ProtocolSocketInterface getSocket()
	{
		return this.socket;
	} /* end getSocket method */
	
	/**
	 * Although this class contain a constructor, it is up to the user to 
	 * define the protocols array.
	 * @param socket	the socket that owns this object
	 */
	protected void setSocket(ProtocolSocketInterface socket)
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
	public void process(ProtocolMessage message, Protocol.Stance stance) throws InstantiationException, IllegalAccessException 
	{
		System.out.println("ProtocolPackage. Entering process: " + Thread.currentThread());
		Tuple tuple = lookup.get(message.getProtocolID());
		Triple triple = this.protocolList[tuple.type].get(tuple.index);
		Protocol protocol = triple.protocol;
		
		/* Lazy loading and copy on write for static */
		if(protocol == null)
		{
			protocol = triple.protocolClass.newInstance();
			synchronized(this)
			{
				this.protocolList[tuple.type].set(tuple.index, new Triple(triple.id, protocol, triple.protocolClass));
			} /* end synchronized */
			
		} /* end if */
		
		/* Process the message */
		protocol.process(this, message, stance);
		
		System.out.println("ProtocolPackage. Exiting process: " );
	} /* end process method */
	
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
		synchronized(this)
		{
			this.lookup.put(protocolID, tuple);
		} /* end synchronized block */
		
		
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
		synchronized(this)
		{
			this.lookup.put(protocolID, tuple);
		} /* end synchronized block */
		
	} /* end lazyAddProtocol method */
	
	/**
	 * Adds a Protocol that will be used by all ProtocolPackages
	 * @param protocol		the Protocol added
	 * @param protocolID	the ID of the Protocol added
	 */
	public static void addStaticProtocol(Protocol protocol, int protocolID)
	{
		/* Updates the lookup table for every ProtocolPackage object */
		synchronized(ProtocolPackage.class)
		{
			Triple triple = new Triple(protocolID, protocol, protocol.getClass());
			ProtocolPackage.staticProtocols.add(triple);
			Tuple tuple = new Tuple(ProtocolPackage.staticProtocols.indexOf(triple), ProtocolPackage.STATIC);
			
			for(ProtocolPackage pack : packages)
			{
				synchronized(pack)
				{
					pack.lookup.put(protocolID, tuple);
				} /* end synchronized */
				
			} /* end for loop */
			
		} /* end synchronized block */
		
	} /* end addStaticProtocol method */
	
	/**
	 * Adds a Protocol that will be used by all ProtocolPackages but will be 
	 * lazy loaded
	 * @param protocolClass	the Class of the Protocol that will be lazy loaded
	 * @param protocolID	the ID of the Protocol added
	 */
	public static void lazyAddStaticProtocol(Class<? extends Protocol> protocolClass, int protocolID)
	{
		/* Updates the lookup table for every ProtocolPackage object */
		synchronized(ProtocolPackage.class)
		{
			Triple triple = new Triple(protocolID, null, protocolClass);
			ProtocolPackage.staticProtocols.add(triple);
			Tuple tuple = new Tuple(ProtocolPackage.staticProtocols.indexOf(triple), ProtocolPackage.STATIC);
			
			for(ProtocolPackage pack : packages)
			{
				synchronized(pack)
				{
					pack.lookup.put(protocolID, tuple);
				} /* end synchronized */
				
			} /* end for loop */
			
		} /* end synchronized block */
	} /* end lazyAddStaticProtocol method */
	
} /* end ProtocolPackage class */
