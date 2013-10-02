package org.openrosa.client.util;

public interface IFileIOCallback {
	
	//defines a call back for the completion of a file IO operation.
	public void fileOperationComplete(String data, String error);
	
	public void fileSearchComplete(String[]data, String error);

}
