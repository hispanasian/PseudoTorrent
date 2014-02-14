package tracking;

import java.util.Timer;

/**
 * The OptUnchokeTimer class will used to start a timer to keep
 * track of the optimistically chosen unchoked peer every m seconds.
 * 
 * @author Terek
 *
 */
public class OptUnchokeTimer {
	
	/******************* Class Attributes *******************/
	Timer timer;

	/******************* Class Methods *******************/
	/**
	 * Use at the start of the main: new OptUnchokeTimer();
	 */
	public OptUnchokeTimer () {
		this.timer = new Timer();		
		timer.schedule(new UnchokeTask(),
		               0,        				//initial delay
		               Tracker.optimisticUnchokeInterval*1000);  //subsequent rate
	} /* end Constructor */
}
