package mySimpleHttpServer;
import java.io.*;
import java.net.*;

/**Server Runnable for thread.
 * Whenever this is a new request, a thread and an instance of this class will be created and run.
 * @author Deqiang Qiu
 *
 */
public class ServletRunnable implements Runnable{
	Socket sc;
	private String rootPath;
	private static MimeType mineTypeMap;
	
	/**Set root path
	 * @param path
	 */
	void setRootPath(String path)
	{
		rootPath = path;
	}

	/** Set mine type, dependency injection
	 * @param mt
	 */
	void setMineType(MimeType mt)
	{
		mineTypeMap = mt;
	}

	/**Dispatch Http request based on method
	 * @param request
	 * @param response
	 */
	void httpDispatch(HttpRequest request, HttpResponse response)
	{
		if(request.getHeaderField("method").equals("GET")){
			/* could move to a doGET method*/
			doGet(request,response);
		}
		
		else if(request.getHeaderField("method").equals("POST"))
		{
			doPost(request, response);
		}
		
		
		else if(request.getHeaderField("method").equals("CLOSE"))
		{
			doClose(request,response);

		}
		else
		{
			doUnknownMethod(request, response);
		}
	}

	/** This function is called when the method is unknown
	 * @param request
	 * @param response
	 */
	void doUnknownMethod(HttpRequest request, HttpResponse response)
	{
		response.setMessageContent("Bad Header- Unknown method");
		response.setStatus(400);
		response.setContentType("text/html");
		return;
	}

	/** This function is called when the method is GET
	 * @param request
	 * @param response
	 */
	void doGet(HttpRequest request, HttpResponse response) 
	{
		byte[] fileContent ;
			try{
			fileContent = IOHelper.readFile(rootPath+request.getHeaderField("url"));
			}catch (IOException e)
			{
				response.setMessageContent(new String(request.getHeaderField("url") + " not found").getBytes());
				response.setStatus(404);
				response.setContentType("text/html");
	//			response.sendHttpResponse();
				return;
			}
			
			
			String fileType;
			String[] fileName = request.getHeaderField("url").split("/");
			String[] fileparts = fileName[(fileName.length)-1].split("\\.");
			String ext = fileparts[fileparts.length-1].toLowerCase();
			
			fileType = mineTypeMap.getMimeTypeByExtension(ext);
			
			response.setContentType(fileType);
			response.setStatus(200);
			response.setMessageContent(fileContent);
		
		
			
		
	}

	/** This function is called when the method is POST
	 * @param request
	 * @param response
	 */
	void doPost(HttpRequest request, HttpResponse response)
	{
		response.setMessageContent("POST method not yet supported");
		response.setStatus(200);
		response.setContentType("text/html");
	}

	/** This function is called when the method is CLOSE
	 * @param request
	 * @param response
	 */
	void doClose(HttpRequest request, HttpResponse response)
	{
		try{
			request.getSocket().close();	
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}

	@Override
	public void run()
	{
		System.out.println("Socket connected\n");
		try{
			

		while(!sc.isClosed())
		{
			HttpRequest request;
			HttpResponse response;
			try
			{
				//read http request
				request = new HttpRequest(sc);
				request.readHttpRequest();
				
				response = new HttpResponse(sc);
				httpDispatch(request, response);
				if(request.getHeaderField("method").equals("CLOSE"))//special custom command
				 	sc.close();
				 else
					 response.sendHttpResponse();
				//do the response
			}
			catch (IOException e)
			{
			//	System.out.println("IO Excetion read any nothing");
				sc.close();
				continue;
			}
			catch (HttpHeaderException e )
			{
				new HttpResponse("Bad Header Request".getBytes(),"text/html",400,sc).sendHttpResponse();
				continue;
			}

		
		}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
	}
	
}
