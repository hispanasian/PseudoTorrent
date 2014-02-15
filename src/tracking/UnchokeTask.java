package tracking;

import java.util.TimerTask;

/**
 * The UnchokeTask class is used in the UnchokeTimer.  It spins up a thread
 * that will update the TopK peers for a user to unchoke.
 * 
 * @author Terek
 *
 */
public class UnchokeTask extends TimerTask {

	@Override
    public void run() 
	{
		Tracker.updateTopK();
		System.out.println("updated TopK");
	}
}
