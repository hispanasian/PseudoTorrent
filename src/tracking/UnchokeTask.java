package tracking;

import java.util.TimerTask;


public class UnchokeTask extends TimerTask {

	@Override
    public void run() 
	{
		Tracker.updateTopK();
		cancel();
	}
}
