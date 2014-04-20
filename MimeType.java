package mySimpleHttpServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/** Mine Type class that return mine type using extension
 * @author Deqiang Qiu
 *
 */
class MimeType
{
	protected HashMap<String, String> mimeTypeMap;
	
	/** Constructor with input file name
	 * @param mimeTypeFileName
	 */
	MimeType(String mimeTypeFileName)
	{
		mimeTypeMap = new HashMap<String,String>();
		try{
			File file = new File(mimeTypeFileName);
			FileReader fileReader = new FileReader(file);
			BufferedReader bReader = new BufferedReader(fileReader);
			String line;
			String[] lineParts;
			while((line=bReader.readLine())!=null)
			{
				lineParts = line.split(" ");
				mimeTypeMap.put(lineParts[0].toLowerCase(),lineParts[1].trim());
			}
			bReader.close();
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	/** Return mine type for given file extension
	 * @param ext
	 * @return
	 */
	public	String getMimeTypeByExtension(String ext)
	{
		String fileType;
		if(ext.equals("html") ||ext.equals("txt") || ext.equals("htm") )
		{
			fileType = "text/html";
		}

		else if(ext.equals("png"))
		{
			fileType = "image/png";
		}

		else if(ext.equals("jpeg"))
		{
			fileType = "image/jpg";
		}

		else if(ext.equals("pdf"))
		{
			fileType = "application/pdf";
		}
		else if(ext.equals("css"))
		{
			fileType = "text/css";
		}
		else if(ext.equals("zip"))
		{
			fileType = "application/zip";
		}
		else
		{
			fileType = mimeTypeMap.get(ext);
		}
		return fileType;
	}
}