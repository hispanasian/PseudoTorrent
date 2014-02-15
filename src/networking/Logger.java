package networking;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Logger will be used by the torrent program to write events to a file. 
 * @author Carlos Vasquez
 *
 */
public class Logger 
{
	/******************* Class Attributes *******************/
	public final String path;
	private Date date;
	private Timestamp time; 
	private File file;
	private FileWriter writer;
	private BufferedWriter buffer;
	
	/******************* Class Methods 
	 * @throws IOException *******************/
	public Logger(String path) throws IOException
	{
		this.path = path;
		this.date = new Date();
		this.time = new Timestamp(date.getTime());
		
		/* Create the file and overwrite existing file */
		file = new File(path);
		if(file.exists()) file.delete();
		file.createNewFile();
		
		/* Create the streams */
		try 
		{
			writer = new FileWriter(file.getAbsoluteFile());
			buffer = new BufferedWriter(writer);
		} /* remove try */
		catch (IOException e) 
		{
			// TODO nothing?
		} /* end catch */
		
	} /* end constructor */
	
	/**
	 * Thread safe method that allows the Logger to write to the log.
	 * @param content	the content being written to the log
	 */
	public final synchronized void writeToFile(String content)
	{
		try 
		{
			buffer.write(content);
			buffer.flush();
		} /* end try */ 
		catch (IOException e) 
		{
			// TODO Do nothing?
		} /* end catch */
		
	} /* end writeToFile */
	
	/**
	 * Closes the streams used to write to the file.
	 */
	public final synchronized void close()
	{
		try 
		{
			buffer.close();
			writer.close();
		} /* end try */ 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /* end catch */
		
	} /* end close method */
	
	public final synchronized String getTime()
	{
		this.time.setTime(this.date.getTime());
		return time.toString();
	} /* end getTime method */
	
} /* end Logger class */
