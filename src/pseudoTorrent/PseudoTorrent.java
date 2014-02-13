package pseudoTorrent;

import tracking.Tracker;

/**
 * The PseudoTorrent class will contain and provide the necessary meta-data 
 * and data structures for the program to work. It will be used by various
 * classes to perform their duties.
 * @author Carlos Vasquez
 *
 */

public class PseudoTorrent
{
	Tracker t;
	
	public void start() {
		t = new Tracker(0, 0, 0, 0, 0);
	}

} /* end PseudoTorrent class */
