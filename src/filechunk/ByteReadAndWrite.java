package filechunk;

import host.Host;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/*
 * @author KiranRohankar
 * @description: this class is responsible for file reading and fragmenting it into number of chunks depending on the 
 * chunk size.
 * 
 */

public class ByteReadAndWrite {
	
	/*
	 * @param: File path as String
	 * @param: chunk size
	 * @return:Arraylist of chunk files.
	 * 
	 */
	private String filename;
	private int CHUNK_SIZE;
	
	
	

	public ByteReadAndWrite(String filename, int cHUNK_SIZE) {
		super();
		this.filename = "peer_" + Host.getID() + "/" + filename;
		CHUNK_SIZE = cHUNK_SIZE;
	}

	public ArrayList<String> readAndFragment () throws IOException
	 {
		
	  //log ("File Is Reading "+ SourceFileName );
		 String filedir= this.filename;
	  File willBeRead = new File ( filedir );
	  int FILE_SIZE = (int) willBeRead.length();
	  System.out.println("filesize is:" +FILE_SIZE);
	  ArrayList<String> nameList = new ArrayList<String> ();
	  
	//  System.out.println("Total File Size: "+FILE_SIZE);
	  
	  int NUMBER_OF_CHUNKS = 0;
	  byte[] temporary = null;
	  
	  try {
	   InputStream inStream = null;
	   int totalBytesRead = 0;
	   
	   try {
		   System.out.println("ByteReadAndWrite filename: " + filename);
	    inStream = new BufferedInputStream ( new FileInputStream( this.filename ));
	    
	    while ( totalBytesRead < FILE_SIZE )
	    {
	     String PART_NAME = "peer_" + Host.getID() + "/" + NUMBER_OF_CHUNKS+"";
	     int bytesRemaining = FILE_SIZE-totalBytesRead;
	     if ( bytesRemaining < CHUNK_SIZE ) // Remaining Data Part is Smaller Than CHUNK_SIZE
	                // CHUNK_SIZE is assigned to remain volume
	     {
	      CHUNK_SIZE = bytesRemaining;
	    //  System.out.println("CHUNK_SIZE: "+CHUNK_SIZE);
	     }
	     temporary = new byte[CHUNK_SIZE]; //Temporary Byte Array
	     int bytesRead = inStream.read(temporary, 0, CHUNK_SIZE);
	     
	     if ( bytesRead > 0) // If bytes read is not empty
	     {
	      totalBytesRead += bytesRead;
	      NUMBER_OF_CHUNKS++;
	     }
	     
	     write(temporary, PART_NAME);
	     nameList.add(PART_NAME);
	  //   System.out.println("Total Bytes Read: "+totalBytesRead);
	    }
	    
	   }
	   finally {
	    inStream.close();
	   }
	  }
	  catch (FileNotFoundException ex)
	  {
	   ex.printStackTrace();
	  }
	  catch (IOException ex)
	  {
	   ex.printStackTrace();
	  }
	  return nameList;
	 }
	 
	 void write(byte[] DataByteArray, String DestinationFileName){
	     System.out.println("ByteReadAndWrite.write started");
		 try {
	       OutputStream output = null;
	       System.out.println("ByteReadAndWrite.write make buffered output");
	         output = new BufferedOutputStream(new FileOutputStream(DestinationFileName));
	         System.out.println("ByteReadAndWrite.write start write");
	         output.write( DataByteArray );
	     //    System.out.println("Writing Process Was Performed");
	         System.out.println("ByteReadAndWrite.write start close");
	         output.close();
	         System.out.println("ByteReadAndWrite.write closed");
	     }
	     catch(FileNotFoundException ex){
	      ex.printStackTrace();
	     }
	     catch(IOException ex){
	      ex.printStackTrace();
	     }
	 }
	 
	 /*
		 * @param: Arralist of chunk
		 * @param: Destination merged file path to store
		 * 
		 * 
		 */
	 
	 public void mergeParts ( ArrayList<String> nameList, String DESTINATION_PATH )
	 {
		 
	System.out.println("size of namelist is:"+nameList.size());
	  File[] file = new File[nameList.size()];
	  byte AllFilesContent[] = null;
	  
	  int TOTAL_SIZE = 0;
	  int FILE_NUMBER = nameList.size();
	  int FILE_LENGTH = 0;
	  int CURRENT_LENGTH=0;
	  
	  for ( int i=0; i<FILE_NUMBER; i++)
	  {
	   file[i] = new File (nameList.get(i));
	   TOTAL_SIZE+=file[i].length();
	  }
	  
	 
	  try {
	   AllFilesContent= new byte[TOTAL_SIZE]; // Length of All Files, Total Size
	   InputStream inStream = null;
	   
	   for ( int j=0; j<FILE_NUMBER; j++)
	   {
	    inStream = new BufferedInputStream ( new FileInputStream( file[j] ));
	    FILE_LENGTH = (int) file[j].length();
	    inStream.read(AllFilesContent, CURRENT_LENGTH, FILE_LENGTH);
	    CURRENT_LENGTH+=FILE_LENGTH;
	    inStream.close();
	   }
	   
	  }
	  catch (FileNotFoundException e)
	  {
	   System.out.println("File not found " + e);
	  }
	  catch (IOException ioe)
	  {
	    System.out.println("Exception while reading the file " + ioe);
	  }
	  finally 
	  {
	   write (AllFilesContent,DESTINATION_PATH);
	  }
	  
	  System.out.println("Merge was executed successfully.!");
	  
	 }
	 
	 /*
	  * 
	  */
	 public synchronized byte[] getChunk(int chunkId)
	 {
		 //check if the file exist if not then throw exception
		 //if file exist copy the whole file into temporary array
		 //return the temp array.
		 String filedir= "peer_"+Host.getID()+"/"+chunkId+"";
		// String filepath=filedir+chunkId+"";
		 File file= new File(filedir);
		 byte [] temp= null;
		 try {
			 InputStream istream=new BufferedInputStream(new FileInputStream(filedir));
			 int filesize= (int)file.length();
			 temp=new byte[filesize];
			 istream.read(temp);
			 istream.close();
			 
		} catch (FileNotFoundException e) {
			System.out.println("File is not found");
			e.printStackTrace();
		}
		 catch (IOException e) {
			System.out.println("Some kind of IO exeption occured");
			e.printStackTrace();
		}
		 
		 
		 
		 
		
		 
		 return temp;
	 }
	 public synchronized void giveChunk(int chunkId,byte [] chunk)
	 {
		 String filedir= "peer_"+Host.getID()+"/"+chunkId+"";
				 String filepath=chunkId+"";
		 System.out.println("ByteReadAndWrite.giveChunk: start write ");
		write(chunk, filedir); 
		System.out.println("ByteReadAndWrite.giveChunk: end write ");
	 }
	 
	 public static boolean makePeerDir(int id)
	 {
		 boolean res=new File("peer_"+id).mkdirs();
		 return res;
		 
	 }
	 
	public static void main(String[] args) {
		try {
	//	ByteReadAndWrite b= new ByteReadAndWrite("TheFile.dat", 32768);
		//b.readAndFragment();
		
		boolean res=new File("peer_0").mkdirs();
		if(res)
		{
			System.out.println("dir is created");
		}
		else
		{
			System.out.println("unable to make");
		}
	//	b.readAndFragment();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 
}
	 
	