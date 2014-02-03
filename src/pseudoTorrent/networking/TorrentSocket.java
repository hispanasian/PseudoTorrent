package pseudoTorrent.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import DD.Message.NetworkMessage;
import pseudoTorrent.PseudoTorrent;
import pseudoTorrent.messages.Message;

/**
 * The TorrentSocket class will perform the socket functionality necessary for
 * the PseudoTorrent program. It will be used to send messages and receive and 
 * interpret messages by performing the necessary protocol as defined by
 * the Protocol class. 
 * 
 * @author Carlos Vasquez
 *
 */
public class TorrentSocket implements Runnable
{
	/******************* Class Methods *******************/
	private final PseudoTorrent torrent;
	private final Socket socket;
	private ObjectInputStream input = null;
	protected ObjectOutputStream output = null;
	
	/******************* Class Methods *******************/
	public TorrentSocket(final PseudoTorrent torrent, final Socket socket)
	{
		this.torrent = torrent;
		this.socket = socket;
		this.createStreams();
		
	} /* end constructor */
	
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		
		this.closeStreams();
	} /* end run method */
	
	/**
	 * Creates the output and input streams
	 */
	protected void createStreams()
	{
		try 
		{
			this.output = new ObjectOutputStream(this.socket.getOutputStream());
			this.input = new ObjectInputStream(this.socket.getInputStream());
		} /* end try */ 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /* end catch */
	} /* end createStreams method */
	
	/**
	 * Closes the output and input streams
	 */
	protected void closeStreams()
	{
		try 
		{
			this.output.close();
			this.input.close();
		} /* end try */ 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /* end catch */
		
	} /* end closeServerSocket method */
	
	protected void sendMessage(byte message)
	{// TODO Change to correct input
		try 
		{
			this.output.writeObject(message);
			this.output.flush();
		} /* end try */
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /* end catch */
		
	} /* end sendSocketMessage method */
	
	protected byte getSocketMessage()
	{// TODO Change to correct return
		byte message = 0;
		
		try
		{
			message = (byte) this.input.readObject();
		} /* end try */
		catch(IOException e)
		{
			e.printStackTrace();
		} /* end catch */
		
		return(message);
	} /* end getSocketMessage method */

} /* end TorrentSocket class */
