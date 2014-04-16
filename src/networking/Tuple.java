package networking;

/**
 * A Data Structure that keeps a tuple: An index and a type
 * @author Carlos Vasquez
 *
 */
public class Tuple
{
	/******************* Class Attributes *******************/
	public final int index;
	public final int type;
	
	/******************* Class Constants *******************/
	/**
	 * Creates a tuple with the given index and type
	 * @param index	the index of the tuple
	 * @param type	the type of the tuple
	 */
	public Tuple(int index, int type)
	{
		this.index = index;
		this.type = type;
		
	} /* end Constructor */
	
} /* end Tuple class */
