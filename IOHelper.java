package mySimpleHttpServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/** This class implements some static helper methods for reading files from local harddrive
 * @author Deqiang Qiu
 *
 */
public class IOHelper{
	
	
	/** Read entire file into byte array in blocking mode
	 * @param filename
	 * @return byte[], containing content of the file
	 * @throws IOException
	 */
	static byte[] readFile(String fileName) throws IOException
	{
		File oFile;
		long fileLength = 0;
		byte [] fileContent;

		//create a input stream from file, IOException will pass through here
		oFile = new File(fileName);
		FileInputStream is = new FileInputStream(oFile);

		//get the file length and read the entire file into a byte array;
		fileLength = oFile.length();
		fileContent = readStream(is, (int) fileLength);

		return fileContent;

	}
	
	/** Reads specified number of bytes from InputStream
	 * @param inputStream, InputStream to read bytes from
	 * @param length, number of bytes to be read
	 * @return byte[], output byte array
	 * @throws IOException
	 */
	static byte[] readStream(InputStream inputStream, int length) throws IOException
	{

		byte[] buff;
		buff = new byte[(int) length];

		int numCharRead=0;
		//keep looping until we read enough number of bytes or there is an IO error;
		while(numCharRead<length)
		{
			int nRead = inputStream.read(buff, numCharRead, (int) (length-numCharRead));
			if(nRead<0)
				break;
			numCharRead+=nRead;
		}
		// if not enough bytes read, there must be any error, throw exception
		if(numCharRead<length)
			throw new IOException();

		return buff;
		
	}

	/**Read number of chars from the specified buffer in blocking mode, 
	 * in Java a char is made of variable number of bytes (1-2 16-bit). 
	 * @param bufferReader, a BufferedReader
	 * @param length, number of chars to be read
	 * @return char[], final char array
	 * @throws IOException
	 */

	static char[] ReadChar(BufferedReader bufferReader, int length) throws IOException
	{

		char[] buff;
		buff = new char[(int) length];
		int numCharRead=0;


		//keep looping until we read enough number of bytes or there is an IO error;
		while(numCharRead<length)
		{
			int nRead = bufferReader.read(buff, numCharRead, (int) (length-numCharRead));
			if(nRead<0)
				break;
			numCharRead+=nRead;
		}

		// if not enough bytes read, there must be any error, throw exception
		if(numCharRead<length)
			throw new IOException();

		return buff;

	}
}