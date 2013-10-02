package org.openrosa.client.view;

import java.util.HashMap;
import java.util.List;

import org.openrosa.client.library.LibraryHelper;
import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.IFormElement;
import org.openrosa.client.model.QuestionDef;
import org.openrosa.client.util.FormHandler;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is ripped off from org.purc.purcforms.client.view.LoginDialog
 * This login dialog will let our users log themselves in with out
 * having to be online by using HTML5 storage. Pretty cool huh
 * 
 * @author daniel
 *
 */
public class SaveToLibraryDialog extends DialogBox {


	/** The widget for organizing widgets in a table format. */
	public FlexTable table = new FlexTable();
	
	/** lets us know how things are going**/
	private Label statusLabel;
	
	/** The XML of the form*/
	private String formXmlStr;
	
	/**If we're doing a block we need the form*/
	private FormDef form;
	
	/**If we're doing a block we need the questions*/
	private List<IFormElement> questions;
	
	/**If we're doing a block we need the attribute map*/
	private HashMap<String, String> attrMap;
	
	
	
	/** Text Box for acquiring the item name */
	private TextBox txtBoxItemName = new TextBox();
	
	/** Block, question, option, template**/
	private String itemType = "block";
		
	/** Text Box for acquiring the tags */
	private TextBox txtBoxTags = new TextBox();
	
	/** The widget for acquiring the weights from the user*/
	private ListBox listBoxWeights = new ListBox(false);
	
	/** Label for weights*/
	private Label weightLabel = new Label(LocaleText.get("weight"));
	
	public void setFormXmlStr(String formXmlStr) {
		this.formXmlStr = formXmlStr;
	}

	/** The HTML for the applet*/
	public HTML appletHtml;
	
	private static SaveToLibraryDialog instance = null;
	
	/**
	 * Used to create the file save dialog
	 * @param formXmlStr the xml to save to disk
	 * @param formName the name of the form to save
	 * @return
	 */
	public static SaveToLibraryDialog getInstnace(String formXmlStr, String type)
	{
		if(instance != null)
		{
			instance.setFormXmlStr(formXmlStr);
			instance.setType(type);
			return instance;
		}
		instance = new SaveToLibraryDialog(formXmlStr, type);
		return instance;
	}
	
	public static SaveToLibraryDialog getInstnace(String type, List<IFormElement> questions, FormDef form, HashMap<String, String> attrMap)
	{
		if(instance != null)
		{
			instance.setType(type);
			return instance;
		}
		else
		{
			instance = new SaveToLibraryDialog("", type);
		}
		instance.setForm(form);
		instance.setQuestions(questions);
		instance.setAttrMap(attrMap);
		return instance;
	}
	

	/**
	 * Used to set the item type
	 * @param type
	 */
	public void setType(String type)
	{
		this.itemType = type;
		
		if(itemType.equals("template"))
		{
			table.remove(weightLabel);
			table.remove(listBoxWeights);
		}
		else
		{
			table.setWidget(2, 0, weightLabel);
			table.setWidget(2, 1, listBoxWeights);
		}
	}
	
	/**
	 * Used to set the attribute map when dealing with a block
	 * @param attrMap
	 */
	private void setAttrMap(HashMap<String, String> attrMap) 
	{
		this.attrMap = attrMap;		
	}

	/**
	 * Used to set the list of questions to copy when dealing with a block
	 * @param questions
	 */
	private void setQuestions(List<IFormElement> questions) 
	{
		this.questions = questions;
	}

	/**
	 * Used to set the form when dealing with a block
	 * @param form
	 */
	private void setForm(FormDef form) 
	{
		this.form = form;
	}

	
	/**
	 * Used to close the file save dialog
	 */
	public static void closeInstance()
	{
		String isSaved = getBlockXmlToSave().toString();
		//turn off the applet
		instance.table.remove(instance.appletHtml);
		
		//didn't save properly
		if(isSaved != null && isSaved.equals("didn'tsave"))
		{
			
		}		
		else if(isSaved == null)
		{
			Window.alert("Error communicating with Java Applet");
		}
		else if(!isSaved.equals("saved"))
		{
			Window.alert(isSaved);
		}
		else
		{
			if(instance != null)
			{
				instance.cancel();
			}
		}
	}
	
		
	/**
	 * Pulls the XML from java script that the applet left waiting for us.
	 * @return
	 */
	private native static String getBlockXmlToSave()
	/*-{
		return $wnd.blockXmlToSave;
	}-*/;
	
	/**
	 * Creates a new instance of the login dialog box.
	 */
	private SaveToLibraryDialog(String formXmlStr, String type){
		
		this.formXmlStr = formXmlStr;
		this.itemType = type;
		setup();
		
		
	}

	
	/**
	 * Sets up the login widget.
	 */
	private void setup(){
		
		setText(LocaleText.get("saveToLibraryTitle"));
		statusLabel = new Label();
		
				
		Button btnCancel = new Button(LocaleText.get("close"), new ClickHandler(){
			public void onClick(ClickEvent event){
				cancel();
			}
		});
		
		Button btnSave = new Button(LocaleText.get("save"), new ClickHandler(){
			public void onClick(ClickEvent event){
				save();
			}
		});
		
		
		
		appletHtml = new HTML();
		
		//Text Box Name
		table.setWidget(0, 0, new Label(LocaleText.get("itemName")));
		table.setWidget(0, 1, txtBoxItemName);
		//Tags
		table.setWidget(1, 0, new Label(LocaleText.get("tags")));
		table.setWidget(1, 1, txtBoxTags);
		
		//weights
		for(int i = 0; i <= 10; i++)
		{
			listBoxWeights.addItem(i+"");
		}
		
		//templates don't use weights
		if(!itemType.equals("template"))
		{
			table.setWidget(2, 0, weightLabel);
			table.setWidget(2, 1, listBoxWeights);
		}
		
		table.setWidget(3, 0, statusLabel);		
		table.setWidget(4, 0, btnSave);
		table.setWidget(4, 1, btnCancel);
		
		FlexCellFormatter formatter = table.getFlexCellFormatter();
		
		formatter.setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		formatter.setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);
		formatter.setHorizontalAlignment(4, 1, HasHorizontalAlignment.ALIGN_RIGHT);
		
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
	 * Method to save stuff 
	 */
	private void save()
	{
		//what name was selected
		String itemNameStr = txtBoxItemName.getText();
		
		//if it's a block create the xml now so we can make sure the
		//block id is properly set
		if(itemType.equals("block"))
		{
			formXmlStr = FormHandler.copyQuestionsToNewForm(questions, form, attrMap, itemNameStr);
		}
		
		//What weight was selected
		String weightStr = listBoxWeights.getItemText(listBoxWeights.getSelectedIndex());
		//what tags were selected
		String tagsStr = txtBoxTags.getText();
		//make tags into a list of xml
		String[] tagsArray = tagsStr.split(",");
		String tagsXml = "<tags>\r\n";
		for(String t : tagsArray)
		{
			tagsXml += "\t<tag>" + t.trim() + "</tag>\r\n";
		}
		tagsXml += "</tags>\r\n";
		
		//now create the xml we're going to write out
		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
		xmlStr += "<kobo_item type=\""+itemType+"\" weight=\""+ weightStr +"\">\r\n";
		xmlStr += "<name><![CDATA["+itemNameStr+"]]></name>\r\n";
		xmlStr += tagsXml;
		xmlStr += "<item_data><![CDATA[" + formXmlStr.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "") + "]]></item_data></kobo_item>";
				

		formXmlToSave(xmlStr);
		
		//span the applet by adding it to the dialog
				appletHtml.setHTML("<APPLET codebase=\"fileioapplets/\"+" +
				" archive=\"kobo_fileIOApplets.jar, plugin.jar\" "+
				" code=\"org.oyrm.kobo.fileIOApplets.ui.BlockSaveApplet.class\" "+
				" width=\"5\" HEIGHT=\"5\" MAYSCRIPT>  "+
				"<param name=\"formName\" value=\""+itemNameStr+"\"/>"+
				"</APPLET>");
				
		//force the library helper to reparse the library directory
		LibraryHelper.resetLibrary();
		//add the widget to the UI
		table.setWidget(5, 0, appletHtml);
	}
	
	
	
	public static native void formXmlToSave(String xml)/*-{
		$wnd.formXmlToSave = xml;
	}-*/;
	
	
	
	
	
	/**
	 * Called when the user selects the CANCEL button.
	 */
	private void cancel(){
		SaveToLibraryDialog.instance = null;
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
