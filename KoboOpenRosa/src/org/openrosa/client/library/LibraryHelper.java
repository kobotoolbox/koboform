package org.openrosa.client.library;

import java.util.ArrayList;

import org.openrosa.client.controller.FormDesignerController;
import org.openrosa.client.util.UserInfo;
import org.openrosa.client.view.SearchPanel;
import org.openrosa.client.view.SearchResultsPanel;
import org.openrosa.client.view.SearchResultsTable;

import com.google.gwt.user.client.Window;

public class LibraryHelper 
{
	
	/** The controller for adding things to the form */
	private static FormDesignerController controller;
	
	/** The panel for displaying search results */
	private static SearchResultsTable searchResultsPanel;

	/** Keeps track of how many files we're waiting to read in*/
	private static ArrayList<String> filesToReadIn;
	
	/** the library that stores all the info on all the files */
	private static KoboFormItemLibrary library = null;
	
	/**The search panel so we can update the status bar*/
	private static SearchPanel searchPanel = null;
	
	/**
	 * Get the current formDesignerController
	 * @return the formDesignerController we're using
	 */
	public static FormDesignerController getController() {
		return controller;
	}

	/**
	 * Sets the formDesignerController
	 * @param controller the formDesignerController we want to use
	 */
	public static void setController(FormDesignerController controller) {
		LibraryHelper.controller = controller;
	}
	
	/**
	 * Forces the library to re parse the library files.
	 * Useful for those times that a new file has been added
	 * and needs to be rescanned.
	 */
	public static void resetLibrary()
	{
		library = null;
	}
	
	/**
	 * This function performs searching. Takes in a list of terms and the given boolean operator 
	 * performs the search and then notifies the library search result handlers
	 * @param terms ArrayList<String> of search terms
	 * @param isAnd true if AND false if OR
	 */
	public static void search(ArrayList<String> terms, boolean isAnd, ArrayList<KoboItemTypes> types, SearchPanel sp)
	{
		searchPanel = sp;
		
		//can't have no place to send the results too
		if(searchResultsPanel == null)
		{
			return;
		}
		
		//make sure there's a current user
		if(UserInfo.getCurrentUser() == null)
		{
			Window.alert("Sorry, but you must be logged in to use the Library");
			return;
		}
		
		//if the library has been initialized then skip to that
		if(library != null)
		{
			searchLibrary(terms, isAnd, types);
			return;
		}
		
		//get the library folder
		String libraryFolder = UserInfo.getSetting(UserInfo.getCurrentUser(), "PROPKEY_LIBRARY_DIR");
		//can't have a null folder either
		if(libraryFolder == null)
		{
			Window.alert("Sorry, but you're library folder hasn't been set. Please use the 'Settings' button to set where the library is");
			return;
		}
		
		//so now we're all cool.
		//lets see what's in the folder
		try
		{
			LibraryFileIOHelper ioHelper = new LibraryFileIOHelper(terms, libraryFolder, isAnd, types, FileOperations.SEARCH);
			ioHelper.execute();
		}
		catch(Exception e)
		{
			Window.alert(e.getMessage());
		}
		
		
	}//end search()
	
	/**
	 * this is called by the library file io helper when the file operation has compleated
	 * @param terms the search terms
	 * @param isAnd are the search terms AND or OR together
	 * @param data the returned data
	 * @param path the path to the file
	 */
	public static void search(ArrayList<String> terms, boolean isAnd, String[]  data, ArrayList<KoboItemTypes> types, String path)
	{
		//loop over the returned files to figure out which ones are the right file type
		filesToReadIn = new ArrayList<String>();
		for(String file : data)
		{
			//filter out all the non *.kfb files
			if(!file.substring(file.length()-4).equals(".kfb"))
			{
				continue;
			}
			filesToReadIn.add(path + "/" + file);
		}
		searchPanel.setupProgressBar(filesToReadIn.size());

		//now read in the first file
		LibraryFileIOHelper helper = new LibraryFileIOHelper(terms, filesToReadIn.get(0), isAnd, types, FileOperations.READ);
		try
		{

			helper.execute();
		}
		catch(Exception e)
		{
			Window.alert("Error reading in file: " + filesToReadIn.get(0) + "\r\n"+e.getMessage());
		}
		
		
	}//end search(ArrayList<String> terms, boolean isAnd, String[]  data, ArrayList<KoboItemTypes> types)
	
	/**
	 * Called as files are read in from the search
	 * @param terms the search terms
	 * @param isAnd are we anding or oring
	 * @param data the file data
	 * @param error the error
	 * @param types the types we're looking at
	 */
	public static void search(ArrayList<String> terms, boolean isAnd, String  data, String error, ArrayList<KoboItemTypes> types, String path)
	{
		//make sure there's no error, if there is tell the user
		if(!error.equals(""))
		{
			Window.alert(error);
		
		}
		else
		{
			//make a kobo form item
			KoboFormItem kfi = null;
			try
			{
				kfi = new KoboFormItem(data);

				//make sure the library is initialized
				if(library == null)
				{
					library = new KoboFormItemLibrary();
				}
				library.addItem(kfi);
						
				//etherton, add status bar

			}
			catch(Exception e)
			{
				Window.alert("Error in file: " + path + "\r\n" + e.getMessage());				
			}
		}
		searchPanel.updateProgressBar();
		//remove a file from the filesToReadInLIst
		filesToReadIn.remove(0);
		if(filesToReadIn.size()!=0) //keep reading
		{
			//now read in the first file
			LibraryFileIOHelper helper = new LibraryFileIOHelper(terms, filesToReadIn.get(0), isAnd, types, FileOperations.READ);
			try
			{

				helper.execute();
			}
			catch(Exception e)
			{
				Window.alert("Error reading in file: " + filesToReadIn.get(0) + "\r\n"+e.getMessage());
			}
			
			return;
		}
		else
		{
			searchLibrary(terms, isAnd, types);					
		}
	}
	
	/**
	 * Called when the library is filled
	 * @param terms search terms
	 * @param isAnd are we anding or oring
	 * @param types the types we're looking at
	 */
	private static void searchLibrary(ArrayList<String> terms, boolean isAnd, ArrayList<KoboItemTypes> types)
	{
		searchPanel.resetProgressBar();
		ArrayList<KoboFormItem> results = library.Query(terms, types, isAnd);
		searchResultsPanel.displayResults(results);
	}

	/**
	 * gets the current UI element for displaying search results
	 * @return
	 */
	public static SearchResultsTable getSearchResultsPanel() {
		return searchResultsPanel;
	}

	/**
	 * sets the current UI element for displaying search results
	 * @param searchResultsPanel
	 */
	public static void setSearchResultsPanel(SearchResultsTable searchResultsPanel) {
		LibraryHelper.searchResultsPanel = searchResultsPanel;
	}

}
