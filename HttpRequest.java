package mySimpleHttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.net.URLDecoder;

/** HttpRequest represents, when created it will be given a socket object.
 * After creation, readHttpRequest, which is responsible for reading data from the socket and parsing the request.
 * Methods are provided to access property fields of the request: including type of request (i.e. GET, POST),  
 * @author Deqiang Qiu
 *
 */
public class HttpRequest
{

	// I would really like C++ type protected permission here	
	protected Socket sc;
//	protected String method = "";
	protected HashMap<String, String> parameters;
	protected HashMap<String, String> header;
	protected BufferedReader bufferReader;

	public HttpRequest(Socket newSocket) throws IOException
	{
		sc = newSocket;
		header = new HashMap<String, String>();
		parameters = new HashMap<String, String>();
		bufferReader = new BufferedReader(new InputStreamReader(sc.getInputStream()));

	}

	public void setSocket(Socket newSocket)
	{
		sc = newSocket;
	}

	public Socket getSocket()
	{
		return sc;
	}

	/** Get fields of the first line in http request, possible values include "method", "url", "http-ver" 
	 * @param key key value possible values include "method", "url", "http-ver"
	 * @return value of the key
	 */
	public String getHeaderField(String key)
	{
		/*
		if(key.equals("method"))
			return method;
		else
		*/
			return header.get(key);
	}
	
	/** Since method is also requently used, we give it a direct method
	 * @return request method, GET, POST, etc
	 */
	public String getHttpRequestMethod()
	{
		return getHeaderField("method");
	}


	/** Return parameter fields of the http method.
	 * This could be either url decoration for GET method, or in the request content in the POST method;
	 * @param key
	 * @return url request parameter value
	 */
	public String getParameter(String key)
	{
		return parameters.get(key);
	}

	/** Returns all the available parameter keys set
	 * @return parameter name set
	 */
	public String [] getParameterKeys(){
		return (String[]) parameters.keySet().toArray();
	}



	/** Read in data from socket, and perform http parsing
	 * @throws IOException
	 * @throws HttpHeaderException
	 */
	public void readHttpRequest() throws IOException, HttpHeaderException
	{
		readHttpHeader();
		readHttpContent();		

	}
	/** Read in and parse http header 
	 * @throws IOException
	 * @throws HttpHeaderException
	 */
	public void readHttpHeader() throws IOException, HttpHeaderException
	{
		String curLine;

        //Keep reading untill we get a line
		while(true)
		{
			if((curLine = bufferReader.readLine())==null)
				throw new IOException();
			System.out.println(curLine);

			if(curLine.length()==0)//continue trying to read if length==0
				continue;
			else // OK we get a command line
			{
				break;
			}


		}

		
		String[] strSplit = curLine.split(" ");
		String method;

		method = strSplit[0].toUpperCase();
		header.put("method", method);
	
		if(method.equals("GET")||method.equals("POST"))
		{
			if(strSplit.length<3)
			{//wrong syntacs, we need three field, method, url, and http version
				throw new HttpHeaderException();
			}
			header.put("http-ver", strSplit[2]);

        // parse url, handle  url rewrite for parameters in GET method
			String[] urlParts = strSplit[1].split("\\?");

			header.put("url", urlParts[0]);
			if(urlParts.length>1)
			{
				String[] paraParts = urlParts[1].split("&");
				for(String str: paraParts)
				{
					String []keyValues = str.split("=");
					parameters.put(URLDecoder.decode(keyValues[0].trim(),"UTF-8"), URLDecoder.decode(keyValues[1].trim(), "UTF-8"));
				}
			}
		}
		else
		{
			throw new HttpHeaderException();
		}

        //perform parsing for remaining lines of header
		while((curLine = bufferReader.readLine())!=null)
		{
			System.out.println(curLine);
			if(curLine.equals(""))
			{
				return; // see a blank line, end of header
			}
			strSplit = curLine.split(":");
			header.put(strSplit[0].trim().toLowerCase(), strSplit[1].trim());

		}

	}

	/* read http post content */
	public void readHttpContent() throws IOException
	{

		if(getHttpRequestMethod().equals("POST"))
		{
			//	InputStream bufferedReader = sc.getInputStream();
			if(header.get("content-length")==null)
				return;
			int length = Integer.parseInt(header.get("content-length"));
			if(length<1)
				return;

			//HashMap<String, String> result = new HashMap<String, String>();
			char[] content = IOHelper.ReadChar(bufferReader, length);
			System.out.println(content);
			String []pairs = new String(content).split("&");
			for(String str: pairs)
			{
				String []keyValues = str.split("=");
                parameters.put(URLDecoder.decode(keyValues[0].trim(),"UTF-8"), URLDecoder.decode(keyValues[1].trim(), "UTF-8"));
			}
		}
	}

}