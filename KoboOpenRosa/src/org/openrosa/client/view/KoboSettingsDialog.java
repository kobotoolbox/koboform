package org.openrosa.client.view;

import org.openrosa.client.controller.IFileListener;
import org.openrosa.client.util.UserInfo;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is ripped off from org.purc.purcforms.client.view.LoginDialog
 * This login dialog will let our users log themselves in with out
 * having to be online by using HTML5 storage. Pretty cool huh
 * 
 * @author daniel
 *
 */
public class KoboSettingsDialog extends DialogBox {


	/** The widget for organizing widgets in a table format. */
	private FlexTable table = new FlexTable();
	
	/** lets us know how things are going**/
	private Label statusLabel;
		
	private HTML appletHtml;
	

	private static KoboSettingsDialog instance = null;
	
	/**
	 * Used to create the file save dialog
	 * @param formXmlStr the xml to save to disk
	 * @param formName the name of the form to save
	 * @return
	 */
	public static KoboSettingsDialog getInstnace()
	{
		if(instance != null)
		{
			return instance;
		}
		
		instance = new KoboSettingsDialog();
		return instance;
	}
	
	/**
	 * Used to close the file save dialog
	 */
	public static void closeInstance()
	{
		if(instance != null)
		{
			instance.cancel();
		}
		//zero out the current UI
		instance = null;
		
		String userName = UserInfo.getCurrentUser();		
		String settingsStr = getSettingsFromJS();
		if(settingsStr != null && settingsStr.length() != 0)
		{
			String[] settingPairs = settingsStr.split("\n");
			for(String pair : settingPairs)
			{
				String[] keyValue = pair.split("\t");
				String key = keyValue[0];
				String value = keyValue[1];
				
				UserInfo.setSetting(userName, key, value);
			}
		}
		
	}
	
	/**
	 * Pulls the XML from java script that the applet left waiting for us.
	 * @return
	 */
	private native static String getSettingsFromJS()
	/*-{
		return $wnd.updateSettings;
	}-*/;
	
	/**
	 * Creates a new instance of the login dialog box.
	 */
	private KoboSettingsDialog(){
		
		setup();
	}

	
	/**
	 * Sets up the login widget.
	 */
	private void setup(){
		
		setText(LocaleText.get("settingsTitle"));
		statusLabel = new Label();
		
		
		
		
		appletHtml = new HTML();

		String openTitle = LocaleText.get("open");
		//TODO use relative paths in production
		appletHtml.setHTML("<APPLET codebase=\"fileioapplets/\"+" +
				" archive=\"kobo_fileIOApplets.jar, plugin.jar\" "+
				" code=\"org.oyrm.kobo.fileIOApplets.ui.FileSettingsApplet.class\" "+
				" width=\"600\" HEIGHT=\"340\" MAYSCRIPT> "+
				"</APPLET>");
		
		
		table.setWidget(2, 0, statusLabel);
		table.setWidget(3, 0, appletHtml);
		
		FlexCellFormatter formatter = table.getFlexCellFormatter();
		formatter.setColSpan(2, 0, 3);
		formatter.setColSpan(3, 0, 3);
		formatter.setColSpan(4, 0, 2);
		formatter.setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		formatter.setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);
		
		formatter.setWidth(1, 1, "50%");
		
		

		
		VerticalPanel panel = new VerticalPanel();
		FormUtil.maximizeWidget(panel);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.add(table);
		
		setWidget(panel);
		
		setWidth("400px");		
	}
	
	
		
	/**
	 * Called when the user selects the CANCEL button.
	 */
	private void cancel(){
		hide();
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
