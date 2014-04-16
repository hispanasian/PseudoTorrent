package host;

import java.util.Timer;

/**
 * The UnchokeTimer class will used to start a timer to keep
 * track of the chosen unchoked peers every k seconds.
 * 
 * @author Terek
 *
 */
public class UnchokeTimer {
	
	/******************* Class Attributes *******************/
	Timer timer;

	/******************* Class Methods *******************/
	/**
	 * Use at the start of the main: new UnchokeTimer();
	 */
	public UnchokeTimer () {
		this.timer = new Timer();		
		timer.schedule(new UnchokeTask(),
	               0,        				//initial delay
	               Host.unchokeInterval*1000);  //subsequent rate
	} /* end Constructor */
	
	public void stop() {
		timer.cancel();
		timer.purge();
	}
	
}
