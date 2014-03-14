package filechunk;

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
	
	
	public ArrayList<String> readAndFragment ( String SourceFileName, int CHUNK_SIZE ) throws IOException
	 {
		
	  //log ("File Is Reading "+ SourceFileName );
	  File willBeRead = new File ( "Richie.mp3" );
	  int FILE_SIZE = (int) willBeRead.length();
	  ArrayList<String> nameList = new ArrayList<String> ();
	  
	//  System.out.println("Total File Size: "+FILE_SIZE);
	  
	  int NUMBER_OF_CHUNKS = 0;
	  byte[] temporary = null;
	  
	  try {
	   InputStream inStream = null;
	   int totalBytesRead = 0;
	   
	   try {
	    inStream = new BufferedInputStream ( new FileInputStream( "Richie.mp3" ));
	    
	    while ( totalBytesRead < FILE_SIZE )
	    {
	     String PART_NAME =NUMBER_OF_CHUNKS+"";
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
	     try {
	       OutputStream output = null;
	       try {
	         output = new BufferedOutputStream(new FileOutputStream(DestinationFileName));
	         output.write( DataByteArray );
	     //    System.out.println("Writing Process Was Performed");
	       }
	       finally {
	         output.close();
	       }
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
	 public byte[] getChunk(int chunkId)
	 {
		 //check if the file exist if not then throw exception
		 //if file exist copy the whole file into temporary array
		 //return the temp array.
		 String filepath=chunkId+"";
		 File file= new File(filepath);
		 byte [] temp= null;
		 try {
			 InputStream istream=new BufferedInputStream(new FileInputStream(file));
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
	 public boolean giveChunk(int chunkId,byte [] chunk)
	 {
		 String filepath=chunkId+"";
		write(chunk, filepath); 
		 return true;
	 }
	 
	public static void main(String[] args) {
		try {
		ByteReadAndWrite b= new ByteReadAndWrite();
		//ArrayList<String> str=	new ByteReadAndWrite().readAndFragment("Richie.mp3", 1000000);
		//System.out.println(str.get(0));
			byte[] arr= b.getChunk(0);
			System.out.println("my length is"+arr.length);
			b.giveChunk(4, arr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 
}
	 
	