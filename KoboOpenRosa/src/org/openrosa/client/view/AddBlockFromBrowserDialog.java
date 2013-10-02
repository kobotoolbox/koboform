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
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is ripped off from org.purc.purcforms.client.view.LoginDialog
 * This login dialog will let our users log themselves in with out
 * having to be online by using HTML5 storage. Pretty cool huh
 * 
 * @author daniel
 *
 */
public class AddBlockFromBrowserDialog extends DialogBox {

	/** For capturing the user name. */
	private ListBox blockNameBox = null;
	
	/** The widget for organizing widgets in a table format. */
	private FlexTable table = new FlexTable();
	
	/** lets us know how things are going**/
	private Label statusLabel;	
	
	/** The file listener, where put the XML that the user wants to load*/
	private FormDesignerController controller;
		
	/**
	 * Creates a new instance of the login dialog box.
	 */
	public AddBlockFromBrowserDialog(FormDesignerController controller){
		setup();
		
		this.controller = controller;
	}

	
	/**
	 * Sets up the login widget.
	 */
	private void setup(){
		
		//find out if the user is logged in or not
		String user = UserInfo.getCurrentUser();
		if(user == null)
		{
			Window.alert(LocaleText.get("mustBeLoggedInOpenBlocks"));
			hide();
			return;
		}
		
		setText(LocaleText.get("addQuestionsFromLocal"));
					
		//setup the "select block to load text
		Label label = new Label(LocaleText.get("selectBlockToLoad"));
		table.setWidget(1, 0, label);
		label.getElement().getParentElement().getStyle().setProperty("verticalAlign", "text-top");
		
		//setup the select box where the user will pick which block to add
		blockNameBox = new ListBox();
		//get the blocks the user has saved to the browser
		String[] blockNames = UserInfo.getBlocksForUser(user);
		//now put those block names into the drop down list
		for(String blockName : blockNames)
		{
			blockNameBox.addItem(blockName);
		}
		blockNameBox.setWidth("200px");
		//blockNameBox.setHeight("400px");
		blockNameBox.setVisibleItemCount(15);
		table.setWidget(1, 1, blockNameBox);
		
		//setup the status label
		statusLabel = new Label();
		statusLabel.getElement().getStyle().setColor("red");
		table.setWidget(2, 0, statusLabel);
			

		//setup the merge button
		Button btnMerge = new Button(LocaleText.get("merge"), new ClickHandler(){
			public void onClick(ClickEvent event){
				addToForm();
			}
		});			
		table.setWidget(3, 0, btnMerge);
		
		//setup the cancel button		
		Button btnCancel = new Button(LocaleText.get("cancel"), new ClickHandler(){
			public void onClick(ClickEvent event){
				cancel();
			}
		});
		table.setWidget(3, 1, btnCancel);

			
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
	
	/**
	 * Called when one selects the save button.
	 */
	private void addToForm(){
		try
		{
			//get the user, if there is one
			String userName = UserInfo.getCurrentUser();
			

			//so first we get the XML from the user
			String blockName = blockNameBox.getItemText(blockNameBox.getSelectedIndex());
			String xmlStr = UserInfo.getBlock(userName, blockName);
			
			//parse and insert
			controller.insertBlockQuestions(xmlStr);
			
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
				if(blockNameBox != null)
				{
					blockNameBox.setFocus(true);
				}
			}
		});
	}
}
