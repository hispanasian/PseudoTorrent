package pseudoTorrent;

import java.io.IOException;

import networking.Logger;

/**
 * The logger that will be used by the Torrent to log the following events:
 * TCP Connection
 * Change of preferred Neighbors
 * Change of optimistically-unchoke neighbors
 * unchoking
 * choking
 * receiving 'have' message
 * receiving 'interested' message
 * receiving 'not interested' message
 * downloading a piece
 * completion of download
 * @author Carlos Vasquez
 *
 */
public class TorrentLogger extends Logger
{
	/******************* Class Attributes *******************/
	private int peerID;
	
	/******************* Class Methods *******************/
	public TorrentLogger(int peerID, String path) throws IOException 
	{
		super(path + "log_peer_" + peerID + ".log");
		this.peerID = peerID;
	} /* end constructor */
	
	/**
	 * Returns a string of the form [Time]: Peer [peer_ID]
	 * @return	a string of the form [Time]: Peer [peer_ID]
	 */
	private String preamble()
	{
		return(this.getTime() + ": Peer " + this.peerID);
	} /* end preamble */
	
	/**
	 * Logs the connection to peer
	 * @param peer				the peerID of the connected peer
	 * @param madeConnection	flag on whether or not this Peer established the
	 * 							connection
	 */
	public void logTCPConnection(int peer, boolean madeConnection)
	{
		
		String content;
		if(madeConnection)
		{/* This Peer made the connection */
			content = preamble() + " makes a connection to Peer " + peer + ". \n";
		} /* end if */
		else
		{ /* This Peer was connected to by peer */
			content = preamble() + " is connected from Peer " + peer + ". \n";
		} /* end else */
		
		this.writeToFile(content);
	} /* end logTCPConnection method */
	
	/**
	 * Logs the change of preferred neighbors
	 * @param integers	an array with the neighbors that became the	proffered 
	 * 					neighbors
	 */
	public void logChangePreferredNeighbors(Integer[] integers)
	{
		StringBuffer content = new StringBuffer();
		content.append(preamble() + " has the preferred neighbors ");
		
		/* Append the neighbors */
		for(int i = 0; i < (integers.length - 1); i++)
		{
			content.append(integers[i] + ", ");
		} /* end for loop */
		
		/* Append the last neighbor */
		content.append(integers[integers.length - 1] + ". \n");
		
		this.writeToFile(content.toString());
	} /* end logChangePreferredNeighbors method */
	
	/**
	 * Logs the change of the optimistically unchoked neighbor
	 * @param peer	the peerID of the optimistically unchoked neighbor
	 */
	public void logChangeOptimisticallyUnchokedNeighbor(int peer)
	{
		String content = preamble() + " has the optimistically-unchoked neighbor " + peer + ". \n";
		this.writeToFile(content);
	} /* end logChangeOptimisticallyUnchokedNeighbor method */
	
	/**
	 * Log the unchoking of this Peer by peer
	 * @param peer	the peer who has unchoked this Peer
	 */
	public void logUnchoking(int peer)
	{
		String content = preamble() + " is unchoked by " + peer + ". \n";
		this.writeToFile(content);
	} /* end logUnchoking method */
	
	/**
	 * Log the choking of this Peer by peer
	 * @param peer	the peer who has choked this peer
	 */
	public void logChoking(int peer)
	{
		String content = preamble() + " is choked by " + peer + ". \n";
		this.writeToFile(content);
	} /* end logChoking method */
	
	/**
	 * Log the arrival of a 'have' message from peer with piece index of index
	 * @param peer	the peer who sent the message
	 * @param index	the piece index
	 */
	public void logReceivedHave(int peer, int index)
	{
		String content = preamble() + " received a 'have' message from " + peer + " for the piece " + index + ". \n";
		this.writeToFile(content);
	} /* end logReceivedHave method */
	
	/**
	 * Log the arrival of a 'interested' message from peer
	 * @param peer	the peer who sent the message
	 */
	public void logReceivedInterested(int peer)
	{
		String content = preamble() + " received an 'interested' message from " + peer + ". \n";
		this.writeToFile(content);
	} /* end logReceivedInterested method */
	
	/**
	 * Log the arrival of a 'not interested' message from peer
	 * @param peer	the peer who sent the message
	 */
	public void logReceivedNotInterested(int peer)
	{
		String content = preamble() + " received a 'not interested' message from " + peer + ". \n";
		this.writeToFile(content);
	} /* end logReceivedNotInterested method */

	/**
	 * Logs the downloading of a piece of the file
	 * @param peer	the peer from whom the piece was obtained
	 * @param index	the index of the piece obtained
	 * @param has 	the number of pieces this peer has of the file
	 */
	public void logDownloadingPiece(int peer, int index, int has)
	{
		String content = preamble() + " has downloaded the piece " + index + " from " + peer + ". \nNow the number of pieces it has is " + has + ".\n";
		this.writeToFile(content);
	} /* end logDownloadingPiece method */
	
	/**
	 * Logs the completion of the file
	 */
	public void logCompletion()
	{
		String content = preamble() + " has downloaded the complete file.\n";
		this.writeToFile(content);
	} /* end logCompletion method */
	
} /* end TorrentLogger */
