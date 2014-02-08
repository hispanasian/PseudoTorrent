package pseudoTorrent;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import pseudoTorrent.networking.Message;
import pseudoTorrent.networking.PseudoTorrentNetworkingTest;

/**
 * Runs all the tests in the pseudoTorrent package
 * @author Carlos Vasqiez
 *
 */

@RunWith(Suite.class)
@SuiteClasses({PseudoTorrentNetworkingTest.class, PeerProcessTest.class})
public class pseudoTorrentTest {

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
