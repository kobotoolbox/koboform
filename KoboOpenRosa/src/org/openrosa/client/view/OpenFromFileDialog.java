package org.openrosa.client.view;

import org.openrosa.client.controller.FormDesignerController;
import org.openrosa.client.controller.IFileListener;
import org.openrosa.client.model.FormDef;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
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
public class OpenFromFileDialog extends DialogBox {


	/** The widget for organizing widgets in a table format. */
	private FlexTable table = new FlexTable();
	
	/** lets us know how things are going**/
	private Label statusLabel;
		
	private HTML appletHtml;
	
	private FormDesignerController controller;
	
	public FormDesignerController getController() {
		return controller;
	}


	private static OpenFromFileDialog instance = null;
	
	/**
	 * Used to create the file save dialog
	 * @param formXmlStr the xml to save to disk
	 * @param formName the name of the form to save
	 * @return
	 */
	public static OpenFromFileDialog getInstnace(FormDesignerController controller)
	{
		if(instance != null)
		{
			return instance;
		}
		
		instance = new OpenFromFileDialog(controller);
		return instance;
	}
	
	/**
	 * Used to close the file save dialog
	 */
	public static void closeInstance()
	{
		if(instance != null)
		{
			String xmlStr = getXmlToLoad();
			if(!xmlStr.equals("**NULL**"))
			{
				try
				{
					FormDef form = instance.getController().loadNewForm(xmlStr);
				}
				catch (Exception e)
				{
					for(StackTraceElement ste: e.getStackTrace())
					{
						System.out.println(ste);
					}
					Window.alert(LocaleText.get("error") + ":\n\r" + e.getMessage());
				}
			}
			instance.cancel();
		}
	}
	
	/**
	 * Pulls the XML from java script that the applet left waiting for us.
	 * @return
	 */
	private native static String getXmlToLoad()
	/*-{
		return $wnd.formXmlToLoad;
	}-*/;
	
	/**
	 * Creates a new instance of the login dialog box.
	 */
	private OpenFromFileDialog(FormDesignerController controller){
		
		this.controller = controller;
		setup();
		
		
	}

	
	/**
	 * Sets up the login widget.
	 */
	private void setup(){
		
		setText(LocaleText.get("openFormFromFileTitle"));
		statusLabel = new Label();
		
				
		Button btnCancel = new Button(LocaleText.get("close"), new ClickHandler(){
			public void onClick(ClickEvent event){
				cancel();
			}
		});
		
		
		
		appletHtml = new HTML();

		String openTitle = LocaleText.get("open");
		//TODO use relative paths in production
		appletHtml.setHTML("<APPLET codebase=\"fileioapplets/\"+" +
				" archive=\"kobo_fileIOApplets.jar, plugin.jar\" "+
				" code=\"org.oyrm.kobo.fileIOApplets.ui.FileOpenApplet.class\" "+
				" width=\"25\" HEIGHT=\"25\" MAYSCRIPT> "+
				"<param name=\"open\" value=\""+openTitle+"\"/> "+
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
