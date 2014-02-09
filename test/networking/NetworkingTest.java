package networking;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Runs all the tests in the Networking Package
 * @author Carlos Vasquez
 *
 */
@RunWith(Suite.class)
@SuiteClasses({BasicSocketTest.class, ThreadedSocketTest.class})
public class NetworkingTest 
{

}
