package tracking;

import java.util.TimerTask;

public class OptUnchokeTask extends TimerTask {

	@Override
    public void run() 
	{
		Tracker.findOptimisticPeer();
		cancel();
	}
}