package org.openrosa.client.view;

import org.openrosa.client.controller.FormDesignerController;
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
public class SaveToFileDialog extends DialogBox {


	/** The widget for organizing widgets in a table format. */
	private FlexTable table = new FlexTable();
	
	/** lets us know how things are going**/
	private Label statusLabel;
	
	/** The XML of the form*/
	private String formXmlStr;
	
	public void setFormXmlStr(String formXmlStr) {
		this.formXmlStr = formXmlStr;
		formXmlToSave(formXmlStr);
	}


	/** Name of the form*/
	private String formName;
	
	private HTML appletHtml;
	
	private static SaveToFileDialog instance = null;
	
	/**
	 * Used to create the file save dialog
	 * @param formXmlStr the xml to save to disk
	 * @param formName the name of the form to save
	 * @return
	 */
	public static SaveToFileDialog getInstnace(String formXmlStr, String formName)
	{
		if(instance != null)
		{
			instance.setFormXmlStr(formXmlStr);
			return instance;
		}
		
		instance = new SaveToFileDialog(formXmlStr, formName);
		return instance;
	}
	
	/**
	 * Used to close the file save dialog
	 */
	public static void closeInstance()
	{
		String isSaved = getXmlToSave();
		if(isSaved != null && isSaved.equals("saved"))
		{
			//clear dirty flags
			FormDesignerController.clearIsDirty();
		}

		
		if(instance != null)
		{
			instance.cancel();
		}
	}
	
		
	/**
	 * Pulls the XML from java script that the applet left waiting for us.
	 * @return
	 */
	private native static String getXmlToSave()
	/*-{
		return $wnd.formXmlToSave;
	}-*/;
	
	/**
	 * Creates a new instance of the login dialog box.
	 */
	private SaveToFileDialog(String formXmlStr, String formName){
		
		this.formXmlStr = formXmlStr;
		this.formName = formName;
		SaveToFileDialog.formXmlToSave(formXmlStr);
		setup();
		
		
	}

	
	/**
	 * Sets up the login widget.
	 */
	private void setup(){
		
		setText(LocaleText.get("saveFormToFileTitle"));
		statusLabel = new Label();
		
				
		Button btnCancel = new Button(LocaleText.get("close"), new ClickHandler(){
			public void onClick(ClickEvent event){
				cancel();
			}
		});
		
		
		
		appletHtml = new HTML();

		String saveTitle = LocaleText.get("save");
		//TODO use relative paths in production
		appletHtml.setHTML("<APPLET codebase=\"fileioapplets/\"+" +
				" archive=\"kobo_fileIOApplets.jar, plugin.jar\" "+
				" code=\"org.oyrm.kobo.fileIOApplets.ui.FileSaveApplet.class\" "+
				" width=\"25\" HEIGHT=\"25\" MAYSCRIPT>  "+
				"<param name=\"formName\" value=\""+formName+".xml\"/>"+
				"<param name=\"save\" value=\""+saveTitle+"\"/>"+
				"</APPLET>");
		
		
		table.setWidget(2, 0, statusLabel);
		table.setWidget(3, 0, appletHtml);
		table.setWidget(3, 1, btnCancel);
		
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
		
		setWidth("100px");		
	}
	
	
	
	
	public static native void formXmlToSave(String xml)/*-{
		$wnd.formXmlToSave = xml;
	}-*/;
	
	
	
	
	
	/**
	 * Called when the user selects the CANCEL button.
	 */
	private void cancel(){
		SaveToFileDialog.instance = null;
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
