package org.openrosa.client.view;

import org.openrosa.client.controller.FormDesignerController;
import org.openrosa.client.controller.IFileListener;
import org.openrosa.client.model.FormDef;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is ripped off from org.purc.purcforms.client.view.LoginDialog
 * This login dialog will let our users log themselves in with out
 * having to be online by using HTML5 storage. Pretty cool huh
 * 
 * @author daniel
 *
 */
public class AddBlockFromLibraryDialog extends DialogBox {

	

	/** The HTML for the applet*/
	public HTML appletHtml = new HTML();
	
	/** The widget for organizing widgets in a table format. */
	private FlexTable table = new FlexTable();
	
	/** an int used as a bit mask to select which types are shown*/
	private int bitMask = 255;
	
	/** The file listener, where put the XML that the user wants to load*/
	private FormDesignerController controller;
	
	private static AddBlockFromLibraryDialog instance = null;
	
	public static AddBlockFromLibraryDialog getInstance(FormDesignerController controller, int bitMask)
	{
		if(instance == null)
		{
			instance = new AddBlockFromLibraryDialog(controller, bitMask);
		}
		else
		{
			instance.setController(controller);
			instance.setBitMask(bitMask);
		}
		return instance;
	}
	
	public void setController(FormDesignerController controller)
	{
		this.controller = controller;
	}
	
	public FormDesignerController getController()
	{
		return this.controller;
	}
	
	public void setBitMask(int bitMask)
	{
		this.bitMask = bitMask;
		//reset the html
		appletHtml.setHTML("<APPLET codebase=\"fileioapplets/\"+" +
		" archive=\"kobo_fileIOApplets.jar, plugin.jar\" "+
		" code=\"org.oyrm.kobo.fileIOApplets.ui.BlockSearchApplet.class\" "+
		" width=\"500\" HEIGHT=\"600\" MAYSCRIPT>  "+
		"<param name=\"typesToShow\" value=\""+bitMask+"\"/> "+
		"</APPLET>");
	}
	
	/**
	 * You know, in case I want the bit mask
	 * @return
	 */
	public int getBitMask()
	{
		return this.bitMask;
	}
	
	/**
	 * Creates a new instance of the login dialog box.
	 */
	private AddBlockFromLibraryDialog(FormDesignerController controller, int bitMask){
		this.controller = controller;
		this.bitMask = bitMask;
		setItemXmlToLoad("**NULL**");
		setup();
	}

	
	/**
	 * Sets up the login widget.
	 */
	private void setup(){
		

		
		setText(LocaleText.get("addQuestionsFromLibrary"));
					
						
		//span the applet by adding it to the dialog
		appletHtml.setHTML("<APPLET codebase=\"fileioapplets/\"+" +
		" archive=\"kobo_fileIOApplets.jar, plugin.jar\" "+
		" code=\"org.oyrm.kobo.fileIOApplets.ui.BlockSearchApplet.class\" "+
		" width=\"500\" HEIGHT=\"600\" MAYSCRIPT>  "+
		"<param name=\"typesToShow\" value=\""+bitMask+"\"/> "+
		"</APPLET>");
		
		table.setWidget(0, 0, appletHtml);
		
		//setup the overall placement of panels and things
		VerticalPanel panel = new VerticalPanel();
		FormUtil.maximizeWidget(panel);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.add(table);
		
		setWidget(panel);
		
		//setWidth("600px");
		
		//check if they're logged in, if not g

	}
	
	public static void closeInstance()
	{
		
		if(instance != null)			
		{
			//if this was to look for just a template, then we load the template here
			if(instance.getBitMask() == 8)
			{
				//get the data from the 
				String xmlStr = getItemXmlToLoad();
				if(!xmlStr.equals("**NULL**"))
				{	
					
					try
					{
						FormDef form = instance.getController().loadNewForm(xmlStr);
					}
					catch (Exception e)
					{
						Window.alert(LocaleText.get("error") + ":\n\r" + e.getMessage());
						return;
					}
				}
			}
			
			instance.cancel();
		}
	}
	
	/**
	 * The static method that's called when he 
	 * applet is trying to add in a piece of form
	 */
	public static void addFromLibrary()
	{
		if(instance != null && instance.getBitMask() != 8)
		{
			instance.addToForm();
		}
	}
	
	/**
	 * Called when one selects the save button.
	 */
	private void addToForm(){
		try
		{
			//get the data from the 
			String xmlStr = getItemXmlToLoad();
			if(!xmlStr.equals("**NULL**"))
			{			
				//parse and insert
				controller.insertBlockQuestions(xmlStr);
			}
		}
		catch(Exception e) // in case there's an error with storage, or anything else for that matter
		{
			Window.alert(e.getMessage());
		}
		
		
	}
	
	
	
	/**
	 * Pulls the XML from java script that the applet left waiting for us.
	 * @return
	 */
	private native static String getItemXmlToLoad()
	/*-{
		return $wnd.itemXmlToLoad;
	}-*/;
	
	/**
	 * Pulls the XML from java script that the applet left waiting for us.
	 * @return
	 */
	private native static String setItemXmlToLoad(String val)
	/*-{
		$wnd.itemXmlToLoad = val;
	}-*/;
	
	
	
	/**
	 * Called when the user selects the CANCEL button.
	 */
	private void cancel(){
		hide();
		instance = null;
	}
	
		
	/**
	 * Displays the dialog box at the center of the browser window.
	 */
	public void center(){
		
		//If there is any progress dialog box, close it.
		FormUtil.dlg.hide();
		
		//Let the base GWT implementation of centering take control.
		super.center();		
	}
}
