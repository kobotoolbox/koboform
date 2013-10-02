package org.openrosa.client.view;

import org.openrosa.client.controller.FormDesignerController;
import org.openrosa.client.util.UserInfo;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
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
public class SaveToBrowserDialog extends DialogBox {

	/** For capturing the user name. */
	private TextBox txtFormName = null;
	
	/** The widget for organizing widgets in a table format. */
	private FlexTable table = new FlexTable();
	
	/** lets us know how things are going**/
	private Label statusLabel;
	
	/** The XML of the form*/
	private String formXmlStr;
	
	/** What the form is called currently*/
	private String formDisplayText;
	
	
	/**
	 * Creates a new instance of the login dialog box.
	 */
	public SaveToBrowserDialog(String formXmlStr, String formDisplayText){
		
		this.formXmlStr = formXmlStr;
		this.formDisplayText = formDisplayText;
		setup();
		
		
	}

	
	/**
	 * Sets up the login widget.
	 */
	private void setup(){
		
		setText(LocaleText.get("saveFormToBrowserTitle"));
		statusLabel = new Label();
		
		//first see if the user is logged in.
		String user = UserInfo.getCurrentUser();
		if(user == null)
		{
			statusLabel.setText(LocaleText.get("mustLoginFirst"));
			DOM.setStyleAttribute(statusLabel.getElement(), "color", "red");
		}
		else
		{
			
			Label label = new Label(LocaleText.get("formName"));
			table.setWidget(1, 0, label);
			
					
			txtFormName = new TextBox();
			txtFormName.setText(formDisplayText);
			
			//check and see if this form has already been saved
			String currentForm = UserInfo.getCurrentForm();
			if(currentForm != null)
			{
				txtFormName.setText(currentForm);
			}
		
			txtFormName.setWidth("50%");
			table.setWidget(1, 1, txtFormName);
			FormUtil.maximizeWidget(txtFormName);
			
			txtFormName.addKeyUpHandler(new KeyUpHandler(){
				public void onKeyUp(KeyUpEvent event) {
					if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
						save();
				}
			});
			
			
			
			Button btnSave = new Button(LocaleText.get("save"), new ClickHandler(){
				public void onClick(ClickEvent event){
					save();
				}
			});

			table.setWidget(3, 0, btnSave);
			
		}
				
				
		
		Button btnCancel = new Button(LocaleText.get("cancel"), new ClickHandler(){
			public void onClick(ClickEvent event){
				cancel();
			}
		});
		
		
		
		table.setWidget(2, 0, statusLabel);
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
		
		setWidth("300px");

		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				if(txtFormName != null)
				{
					txtFormName.setFocus(true);
				}
			}
		});
	}
	
	/**
	 * Called when one selects the save button.
	 */
	private void save(){
		try
		{
			//first see if the user is logged in.
			String user = UserInfo.getCurrentUser();
			if(user == null)
			{
				statusLabel.setText(LocaleText.get("mustLoginFirst"));
				DOM.setStyleAttribute(statusLabel.getElement(), "color", "red");
				return;
			}
			
			//check and see if this form already exists
			String form = UserInfo.getForm(user, txtFormName.getText());
			
			if(form != null)
			{
				//Window.alert("This form already exists and will be over written");
				if(!Window.confirm("A form by this name is already saved in this browser. Do you want to overwrite it?"))
				{
					return;
				}
			}
			
			UserInfo.setForm(user, txtFormName.getText(), formXmlStr);
			//clear dirty flags
			FormDesignerController.clearIsDirty();
			
			hide();
		}
		catch(Exception e) // in case there's an error with storage
		{
			statusLabel.setText(e.getMessage());
			DOM.setStyleAttribute(statusLabel.getElement(), "color", "red");
		}
		
		
	}
	

	
	
	
	/**
	 * Called when the user selects the CANCEL button.
	 */
	private void cancel()
	{
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
				if(txtFormName != null)
				{
					txtFormName.setFocus(true);
				}
			}
		});
	}
}
