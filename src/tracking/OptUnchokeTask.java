package tracking;

import java.util.TimerTask;

/**
 * The OptUnchokeTask class is used in the OptUnchokeTimer.  It spins up a thread
 * that will update the optimistically unchoked peer for a user to unchoke.
 * 
 * @author Terek
 *
 */
public class OptUnchokeTask extends TimerTask {

	@Override
    public void run() 
	{
		Tracker.findOptimisticPeer();
		cancel();
	}
}