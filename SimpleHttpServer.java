package mySimpleHttpServer;
import java.io.*;
import java.net.*;
import java.util.*;

/**Application class of the server, singleton pattern is used here;
 * This defines some global variables for the application, and is responsible for start the server
 * @author Deqiang Qiu
 *
 */
public class SimpleHttpServer {
	private String rootPath = "/Users/dqiu3/Library/Tomcat/Home/webapps";
	private static String mineTypeFileName = "/Users/dqiu3/Library/Tomcat/Home/webapps/mineType.txt";

	ServerSocket serverSocket;
	ArrayList<Socket> socketList;
	private int portNumber =5002;
	private static MimeType mineTypeMap;

	private static SimpleHttpServer theSingleServer;

	/** Constructor
	 * 
	 */
	private SimpleHttpServer()
	{
		socketList = new ArrayList<Socket>();
		mineTypeMap = new MimeType(mineTypeFileName);
	}
	
	/**Return singleton instance
	 */
	public static SimpleHttpServer getServerInstance()
	{
		if(theSingleServer ==null)
		{
			theSingleServer = new SimpleHttpServer();
		}
		
		return theSingleServer;
		
	}

	/** Set root path of the web server
	 * @param rootPath
	 */
	public void setRootPath(String rootPath)
	{
		this.rootPath = rootPath;
	}

	/**Set port number of the server
	 * @param pNumber
	 */
	public void setPortNumber(int pNumber)
	{
		portNumber = pNumber;
	}

	/**Start the server
	 * 
	 */
	public void startServer()
	{
		try{
			serverSocket = new ServerSocket(portNumber);
			while(true)
			{
				Socket sc = serverSocket.accept();
				synchronized(socketList)
				{
					socketList.add(sc);
				}
				ServletRunnable trd = new ServletRunnable();

				//set properties for runnable
				trd.setMineType(mineTypeMap);
				trd.setRootPath(rootPath);
				trd.sc = sc;
				new Thread(trd).start();

			}

		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/** Entry point of the program
	 * @param args
	 */
	public static void main(String[] args) {
		SimpleHttpServer srv = SimpleHttpServer.getServerInstance();
		if(args.length>0)
		{
			srv.setRootPath(args[0]);

		}
		if(args.length>1)
		{
			srv.setPortNumber(Integer.parseInt(args[1]));
		}
		srv.startServer();
	}

}

