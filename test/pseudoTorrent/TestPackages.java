package pseudoTorrent;

import networking.NetworkingTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Runs all the tests in all the packages
 * @author Carlos Vasqiez
 *
 */

@RunWith(Suite.class)
@SuiteClasses({NetworkingTest.class, PseudoTorrentTest.class, PseudoTorrentTest.class})
public class TestPackages {

}
