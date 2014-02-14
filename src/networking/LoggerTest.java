package networking;

import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for the Logger class
 * @author Carlos Vasquez
 *
 */
public class LoggerTest 
{
	public static String dir;
	public Logger logger;
	
	@BeforeClass
	public static void start()
	{
		//URL location = LoggerTest.class.getProtectionDomain().getCodeSource().getLocation();
		dir = "test.txt";
	} /* end start */
	
	@Before
	public void before()
	{
		try {
			this.logger = new Logger(dir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} /* end before method */
	
	@After
	public void after()
	{
		this.logger.close();
	} /* end after method */
	
	@Test
	public void getTimeTest() 
	{
		/* Can't really junit test, but check syso */
		System.out.println(logger.getTime());
	} /* end getFile test */
	
	@Test
	public void writeToFileTest()
	{
		/* First, write to file */
		logger.writeToFile("test");
		
		/* Next, check if written data is there */
		File file = new File(dir);

		assertEquals("test.txt exists", true, file.exists());
		if(file.exists())
		{
			try {
				BufferedReader buffer = new BufferedReader(new FileReader(dir));
				String test = buffer.readLine();
				assertEquals("test found in test.txt", true, test.equals("test"));
				buffer.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} /* end if */
		
	} /* end writeToFile test */

} /* end LoggerTest class */
