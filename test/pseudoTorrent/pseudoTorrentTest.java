package pseudoTorrent;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import pseudoTorrent.networking.PseudoTorrentNetworkingTest;

/**
 * Runs all the tests in the pseudoTorrent package
 * @author Carlos Vasquez
 *
 */

@RunWith(Suite.class)
@SuiteClasses({PseudoTorrentNetworkingTest.class, PeerProcessTest.class})
public class PseudoTorrentTest {

}
