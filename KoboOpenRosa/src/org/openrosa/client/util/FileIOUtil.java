package org.openrosa.client.util;

import com.google.gwt.user.client.ui.HTML;

/**
 * This class is used to interface with Java applets to provide
 * file IO.
 * @author etherton
 *
 */
public class FileIOUtil {
	
	/**
	 * isBusy is used as crude form of thread safety
	 */
	private static boolean isBusy = false;
	
	/**
	 * The HTML that lets us add or remove applets
	 */
	private static HTML appletHtml = null;
	
	/**
	 * What should be called back when the file has operation is finished
	 */
	private static IFileIOCallback callback = null;
	
	/**
	 * Name of the operation we're performing
	 */
	private static String operation = null;
	
	/**
	 * Used to initialize the utility so it can turn applets on and off
	 * @param appletHtml
	 */
	public static void init(HTML appletHtml)
	{
		FileIOUtil.appletHtml = appletHtml;
		publishJSMethods();
	}
	
	// Set up the JS-callable signature as a global JS function.
	private static native void publishJSMethods() 
	/*-{
		$wnd.fileOperationComplete = @org.openrosa.client.util.FileIOUtil::fileOperationComplete();
	}-*/;

	
	/**
	 * Used to see if we're busy performing a file IO operation.
	 * If we are busy, then don't peform another one. This does
	 * not support multiple concurrent file operations.
	 * @return
	 */
	public static boolean getIsBusy()
	{
		return isBusy;
	}
	
	/**
	 * Check to make sure things are initialized
	 * @throws Exception
	 */
	private static void checkIfInitialized() throws Exception
	{
		if(appletHtml == null)
		{
			Exception e = new Exception("Sorry, you must initialize FileIOUtil before you can use it");
			throw e;
		}
		
		//check if we're busy
		if(isBusy)
		{
			Exception e = new Exception("Already performing a file operation, please wait your turn");
			throw e;
		}		
		//mark us as busy
		isBusy = true;
	}
	
	/**
	 * Call this to read the contents of a file
	 * @param fileName the name of the file
	 * @throws Exception - If there's an error it'll throw an error
	 */
	public static void readFromFile(String fileName, IFileIOCallback callback) throws Exception
	{
		//check if we're inited
		checkIfInitialized();
		
		//what are we doing?
		operation = "read";
				
		//set callback
		FileIOUtil.callback = callback;
	
		//set the error message field to no JSO connection
		setErrorMsg("Couldn't connect to JavaScript");
		
		//open the file
		readFileJS(fileName);
				
	}
	
	
	
	/**
	 * Call this to write to a file
	 * @param fileName the name of the file
	 * @throws Exception - If there's an error it'll throw an error
	 */
	public static void writeToFile(String fileName, String fileData, IFileIOCallback callback) throws Exception
	{
		//check if we're inited or busy
		checkIfInitialized();
		
		//what are we doing?
		operation = "write";
				
		//set callback
		FileIOUtil.callback = callback;
	
		//set the error message field to no JSO connection
		setErrorMsg("Couldn't connect to JavaScript");
		
		//set the file data to write
		setFileData(fileData);
		
		//open the file
		writeFileJS(fileName);
				
	}
	
	/**
	 * Call this to search for all the files and folders in directory
	 * @param directoryName the name of the directory
	 * @throws Exception - If there's an error it'll throw an error
	 */
	public static void searchDirectory(String directoryName, IFileIOCallback callback) throws Exception
	{
		//check if we're inited or busy
		checkIfInitialized();
		
		//what are we doing?
		operation = "search";
				
		//set callback
		FileIOUtil.callback = callback;
	
		//set the error message field to no JSO connection
		setErrorMsg("Couldn't connect to JavaScript");
		
		//open the file
		searchJS(directoryName);
				
	}

	

	/**
	 * What's called when the file operation has finished
	 */
	private static void fileOperationComplete()
	{
		//get the data
		String errorMsg = getErrorMsg();
		String fileData = getFileData();
		
		//remove the applet
		appletHtml.setHTML("");
		isBusy = false;
		if(callback != null)
		{
			//call the users class back
			if(operation.equals("search"))
			{
				String[] data = fileData.split("\n");
				callback.fileSearchComplete(data, errorMsg);
			}
			else
			{
				callback.fileOperationComplete(fileData, errorMsg);
			}
		}
		
		
	}
	
	
	
	/**
	 * Used to set the error message variable
	 * @param errorMsg what you want to set the error message to
	 */
	private static native void setErrorMsg(String errorMsg)
	/*-{
		$wnd.fileIOErrorMsg = errorMsg;
	}-*/;
	
	/**
	 * Used to get the error message variable
	 * @return what you want to set the error message to
	 */
	private static native String getErrorMsg()
	/*-{
		return $wnd.fileIOErrorMsg;
	}-*/;
	
	/**
	 * Used to get the file data from reading a file or scanning a directory
	 * @return contents of a file or directory
	 */
	private static native String getFileData()
	/*-{
		return $wnd.fileIOFileData;
	}-*/;
	
	/**
	 * Used to set the file data for writing to a file 
	 * @return contents of a file or directory
	 */
	private static native String setFileData(String fileData)
	/*-{
		$wnd.fileIOFileData = fileData;
	}-*/;
	

	/**
	 * Use this method to call the JavaScript that calls the applet
	 * to open a file and read it's contents
	 */
	private static void readFileJS(String fileName)
	{
		String html = "<applet id=\"ioApplet\" name=\"ioApplet\" width=\"1\" height=\"1\"" + 
				"code=\"org.oyrm.kobo.fileIOApplets.library.ReadFromFile.class\""+ 
				"archive=\"kobo_fileIOApplets.jar, plugin.jar\""+ 
				"codebase=\"fileioapplets/\"> "+
				"<param name=\"fileName\" value=\""+fileName.replace("\\", "\\\\")+"\"/>"+
			"</applet>"; 
				
		appletHtml.setHTML(html);
	}
	
	/**
	 * Use this method to call the JavaScript that calls the applet
	 * to open a file and write to it
	 */
	private static void writeFileJS(String fileName)
	{
		String html = "<applet id=\"ioApplet\" name=\"ioApplet\" width=\"1\" height=\"1\"" + 
				"code=\"org.oyrm.kobo.fileIOApplets.library.WriteToFile.class\""+ 
				"archive=\"kobo_fileIOApplets.jar, plugin.jar\""+ 
				"codebase=\"fileioapplets/\"> "+
				"<param name=\"fileName\" value=\""+fileName.replace("\\", "\\\\")+"\"/>"+
			"</applet>"; 
				
		appletHtml.setHTML(html);
	}
	
	/**
	 * Use this method to call the JavaScript that calls the applet
	 * to search for files in a directory
	 */
	private static void searchJS(String fileName)
	{
		String html = "<applet id=\"ioApplet\" name=\"ioApplet\" width=\"1\" height=\"1\"" + 
				"code=\"org.oyrm.kobo.fileIOApplets.library.SearchDirectory.class\""+ 
				"archive=\"kobo_fileIOApplets.jar, plugin.jar\""+ 
				"codebase=\"fileioapplets/\"> "+
				"<param name=\"fileName\" value=\""+fileName.replace("\\", "\\\\")+"\"/>"+
			"</applet>"; 
				
		appletHtml.setHTML(html);
	}
}
