package org.openrosa.client.view;

import org.openrosa.client.controller.FormDesignerController;
import org.openrosa.client.util.UserInfo;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
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
public class AddBlockFromTextDialog extends DialogBox {

	/** For capturing the user name. */
	private TextArea addText = null;
	
	/** The widget for organizing widgets in a table format. */
	private FlexTable table = new FlexTable();
	
	/** lets us know how things are going**/
	private Label statusLabel;	
	
	/** The file listener, where put the XML that the user wants to load*/
	private FormDesignerController controller;
	
	/** The check box for saving the given block to the browser **/
	private CheckBox saveToBrowserCheckBox;
	
	/** The text box that is used to set the name of the block we want to save to the browser **/
	private TextBox saveToBrowserNameTextBox;
	
	/** We need a class variable for this so we can hide it and unhide it as need be**/
	private Label saveToBrowserLabel;
	
	/**
	 * Creates a new instance of the login dialog box.
	 */
	public AddBlockFromTextDialog(FormDesignerController controller){
		setup();
		
		this.controller = controller;
	}

	
	/**
	 * Sets up the login widget.
	 */
	private void setup(){
		
		//find out if the user is logged in or not
		String user = UserInfo.getCurrentUser();
		
		setText(LocaleText.get("addQuestionsFromText"));
					
		//setup the "XML text to add" text
		Label label = new Label(LocaleText.get("txtToMerge"));
		table.setWidget(1, 0, label);
		label.getElement().getParentElement().getStyle().setProperty("verticalAlign", "text-top");
		
		//setup the text area where the user will paste in the XML
		addText = new TextArea();
		addText.setWidth("400px");
		addText.setHeight("400px");
		table.setWidget(1, 1, addText);
		
		//setup the status label
		statusLabel = new Label();
		statusLabel.getElement().getStyle().setColor("red");
		table.setWidget(2, 0, statusLabel);
			

		//setup the "add to browser" check box and label
		saveToBrowserCheckBox = new CheckBox(LocaleText.get("addBlockToBrowser"));
		saveToBrowserCheckBox.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event)
			{
				boolean checked = ((CheckBox)event.getSource()).getValue();
				saveToBrowserClicked(checked);
			}
		});
		if(user != null)
		{
			table.setWidget(3, 1, saveToBrowserCheckBox);			
		}
		
		//setup the text box and label that'll capture the name we want to give
		//this block when saving to the browser.
		saveToBrowserLabel = new Label(LocaleText.get("SaveBlockAs"));
		saveToBrowserLabel.setVisible(false);
		table.setWidget(4, 0, saveToBrowserLabel);
		saveToBrowserNameTextBox = new TextBox();
		saveToBrowserNameTextBox.setVisible(false);
		table.setWidget(4, 1, saveToBrowserNameTextBox);
		
		//setup the merge button
		Button btnMerge = new Button(LocaleText.get("merge"), new ClickHandler(){
			public void onClick(ClickEvent event){
				addToForm();
			}
		});			
		table.setWidget(5, 0, btnMerge);
		
		//setup the cancel button		
		Button btnCancel = new Button(LocaleText.get("cancel"), new ClickHandler(){
			public void onClick(ClickEvent event){
				cancel();
			}
		});
		table.setWidget(5, 1, btnCancel);

			
		//setup the overall placement of panels and things
		VerticalPanel panel = new VerticalPanel();
		FormUtil.maximizeWidget(panel);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.add(table);
		
		setWidget(panel);
		
		setWidth("600px");
		
		//check if they're logged in, if not g

	}
	
	/**
	 * Called when one selects the save button.
	 */
	private void addToForm(){
		try
		{
			//get the user, if there is one
			String userName = UserInfo.getCurrentUser();
			
			//if they did select save to browser, make sure they filled in a name
			//for what to call the block
			if(saveToBrowserCheckBox.getValue())
			{
				if(saveToBrowserNameTextBox.getValue() == null || saveToBrowserNameTextBox.getValue() == "")
				{
					statusLabel.setText(LocaleText.get("mustGiveNameForBlockWhenSaving"));
					return;
				}
				
				//see if their overwriting a previously saved block
				String oldBlock = UserInfo.getBlock(userName, saveToBrowserNameTextBox.getValue());
				if(oldBlock != null)
				{
					//prompt the user if they want to continue
					if(!Window.confirm(LocaleText.get("aBlockAlreadyExistsWithThatName")))
					{
						return;
					}
				}
			}
			

			//so first we get the XML from the user
			String xmlStr = addText.getText();

			//parse and insert
			controller.insertBlockQuestions(xmlStr);
			
			
			//assuming that everything worked parsing and inserting that 
			//form, now save it to the browser, if that's what they
			//want.
			if(saveToBrowserCheckBox.getValue())
			{
				UserInfo.setBlock(userName, saveToBrowserNameTextBox.getValue(), xmlStr);
			}
			
			
			hide();
		}
		catch(Exception e) // in case there's an error with storage, or anything else for that matter
		{
			statusLabel.setText(e.getMessage());
			DOM.setStyleAttribute(statusLabel.getElement(), "color", "red");
		}
		
		
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
		
		//Some how focus will not get to the user name unless when called within
		//a deffered command.		
		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				if(addText != null)
				{
				}
			}
		});
	}
	
	/**
	 * Hanldes what happens when the user clicks the save to browser check box
	 * @param checked
	 */
	private void saveToBrowserClicked(boolean checked)
	{
		saveToBrowserLabel.setVisible(checked);
		saveToBrowserNameTextBox.setVisible(checked);
	}
}
