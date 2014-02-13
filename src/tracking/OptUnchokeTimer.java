package tracking;

import java.util.Timer;

public class OptUnchokeTimer {
	
	Timer timer;

	public OptUnchokeTimer () {
		this.timer = new Timer();		
		timer.schedule(new UnchokeTask(),
		               0,        				//initial delay
		               Tracker.optimisticUnchokeInterval*1000);  //subsequent rate
	}
}
