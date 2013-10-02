package org.openrosa.client.library;

import java.util.ArrayList;

import org.openrosa.client.util.FileIOUtil;
import org.openrosa.client.util.IFileIOCallback;

public class LibraryFileIOHelper implements IFileIOCallback{

	
	/**
	 * Keep track of what operation is going on
	 */
	protected FileOperations operation = null;
	
	/**
	 * Keep track of the search params, if need be
	 */
	protected ArrayList<String> searchParams = null;
	
	/**
	 * Used to record if the search is and or or
	 */
	protected boolean isAnd;
	
	/**
	 * The path of the given file operation
	 */
	protected String path = null;
	
	/**
	 * What kinds of items are we looking for
	 * Used in a search.
	 */
	protected ArrayList<KoboItemTypes> types = null;
	
	/**
	 * The constructor when you're searching for stuff
	 * @param searchParams the search params
	 * @param path the path you're searching for
	 * @param isAnd is the search OR or AND
	 */
	public LibraryFileIOHelper(ArrayList<String> searchParams, String path, boolean isAnd, ArrayList<KoboItemTypes> types, FileOperations operation) 
	{
		this.operation = operation;
		this.path = path;
		this.searchParams = searchParams;
		this.types = types;
	}
	
	
	/**
	 * given the data that was set in the constructor
	 * this runs the given operation
	 */
	public void execute() throws Exception
	{
		switch(operation)
		{
			case SEARCH:
				FileIOUtil.searchDirectory(path, this);
				break;
			case READ:
				FileIOUtil.readFromFile(path, this);
				break;
		}
	}
	
	
	/**
	 * Called when done reading in a file's contents
	 */
	public void fileOperationComplete(String data, String error) 
	{
		if(this.operation == FileOperations.READ && searchParams != null)
		{
			LibraryHelper.search(searchParams, isAnd, data, error, types, path);
		}
		
	}

	/**
	 * Called when a search is complete
	 */
	public void fileSearchComplete(String[] data, String error) 
	{
		LibraryHelper.search(searchParams, isAnd, data, types, path);
	}
	
	

}
