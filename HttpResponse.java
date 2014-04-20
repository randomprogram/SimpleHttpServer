package mySimpleHttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/** Http Response object, set proper fields of the response message and send it through socket
 * @author Deqiang Qiu
 *
 */
public class HttpResponse {
	private ByteArrayOutputStream msg;//we use string builder so that this can be garbage collected.
	private PrintWriter writer;
	private String contentType ="text/html";
	private int status = 200;
	private Socket sc;
	private SimpleDateFormat dateFormater;
	
	/** Status map between status code and message
	 * 
	 */
	public static final HashMap<Integer, String> statusMap;
	static
	{
		statusMap= new HashMap<Integer, String>();
		statusMap.put(200, "OK");
		statusMap.put(400, "Bad Request");
		statusMap.put(404, "Not Found");

	}



	/** The default constructor Constructor
	 * @param message message byte array
	 * @param contentType contentType in String
	 * @param status status code
	 * @param sk associated socket
	 */
	public HttpResponse(byte[] message, String contentType, int status, Socket sk)
	{
		sc = sk;
		msg =new ByteArrayOutputStream();
		if(message!=null)
		{
			try {
				msg.write(message);
				msg.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(contentType!=null)
		{
			this.contentType = contentType;
		}
		
		if(status>0)
		{
			this.status = status;
		}
		//create a PrintWriter object around ByteArrayOutputStream so that we could use print command
		writer = new PrintWriter(msg);
		writer.flush();
		dateFormater = new SimpleDateFormat("EEE, MMM dd YYY HH:mm:ss zzz");
		dateFormater.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	/** Constructor with and associate it with socket
	 * @param sk
	 */
	public HttpResponse(Socket sk)
	{
		//call the default constructor
		this((byte[])null, (String) null,(int) -1, sk);
	}
	
	/** Constructor
	 * @param message message String 
	 * @param contentType contentType in String
	 * @param status status code
	 * @param sk associated socket
	 */
	public HttpResponse(String message, String contentType, int status, Socket sk)
	{
		this(message!=null? message.getBytes():null, contentType, status,sk);
	}
	
	/** Get byte array output stream of the array
	 */
	public ByteArrayOutputStream getMessageContent()
	{
		return msg;
	}
	
	/**Set Message content with byte array
	 * @param msgInput
	 */
	public void setMessageContent(byte[] msgInput)
	{
		try {
			msg.reset();
			msg.write(msgInput);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** Set Message Content with String
	 * @param msgInput
	 */
	public void setMessageContent(String msgInput)
	{
		try {
			msg.reset();
			msg.write(msgInput.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** Get PrintWriter Object
	 */
	public PrintWriter getWriter()
	{
		return writer;
	}

	/** Get Status code of response, e.g. 200, 404
	 */

	public void setStatus(int st)
	{
	 status = st;
	}
	
	/** Get Status code of response, e.g. 200, 404
	 */
	public int getStatus()
	{
		return status;
	}
	
	/** Set Content Type of the HTTP Response
	 * @param ct
	 */
	public void setContentType(String ct)
	{
		contentType = ct;
	}
	
	/** Get Content Type of the HTTP Response
	 */
	public String getContentType()
	{
		return contentType;
	}
	
	/** Get bound Socket
	 */
	public Socket getSocket()
	{
		return sc;
	}

	/** Return OutputStream of socket
	 * @throws IOException
	 */
	public OutputStream getOutputStream() throws IOException
	{
		return sc.getOutputStream();
	}
	
	/** Send Http Response to Socket
	 * @throws IOException
	 */
	public void sendHttpResponse() throws IOException
	{
		HttpResponse response = this;
		OutputStream os = response.getOutputStream();
		int status = response.getStatus();
		if(status==404)
			response.setMessageContent("");
		
		byte[] fileContent = response.getMessageContent().toByteArray();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter strWriter = new PrintWriter(baos);
		String statusMsg = statusMap.get(status);
		if(statusMsg ==null) statusMsg = "";
		//Per RFC 2616 (http1.1), I need to end each line with CR/LF. 
		//Automatic newline from println is system dependent. ;
		//But all modern browsers will accept it if println is used.
		strWriter.print("HTTP/1.1 " + status + " " + statusMsg + "\r\n");
		
		strWriter.print("Date: " + dateFormater.format(new Date()) + "\r\n");//Formate date time according to RFC 2616 3.3.1
		strWriter.print("Content-Type: " + response.getContentType() + "\r\n");
		strWriter.print("Content-Length: " + fileContent.length + "\r\n");
		strWriter.print("\r\n"); // end of the header
		strWriter.flush();//flush header to baos

		//log header of response to console
		System.out.println("RESPONSE:");
		System.out.println(baos.toString());
		
		/* write the content part to baos and flush*/
		baos.write(fileContent);
		baos.flush();
		strWriter.print("\r\n");
		
		strWriter.flush();
		
		/* write to socket*/
		os.write(baos.toByteArray());
		os.flush();

	}

}