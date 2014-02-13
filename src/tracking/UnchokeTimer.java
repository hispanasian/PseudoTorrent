package tracking;

import java.util.Timer;


public class UnchokeTimer {
	Timer timer;

	public UnchokeTimer () {
		this.timer = new Timer();		
		timer.schedule(new UnchokeTask(),
		               0,        				//initial delay
		               Tracker.unchokeInterval*1000);  //subsequent rate
	}
}
