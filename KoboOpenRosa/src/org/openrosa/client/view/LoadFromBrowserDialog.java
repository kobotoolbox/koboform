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
public class LoadFromBrowserDialog extends DialogBox {

	/** For capturing the user name. */
	private ListBox listOfForms = null;
	
	/** The widget for organizing widgets in a table format. */
	private FlexTable table = new FlexTable();
	
	/** lets us know how things are going**/
	private Label statusLabel;
	
	
	/** The file listener, where put the XML that the user wants to load*/
	private FormDesignerController controller;
	
	
	/**
	 * Creates a new instance of the login dialog box.
	 */
	public LoadFromBrowserDialog(FormDesignerController controller){
		setup();
		
		this.controller = controller;
	}

	
	/**
	 * Sets up the login widget.
	 */
	private void setup(){
		
		setText(LocaleText.get("loadFormFromBrowserTitle"));
		statusLabel = new Label();
		
		//first check to see if we're logged in
		String user = UserInfo.getCurrentUser();
		if(user == null)
		{
			statusLabel.setText(LocaleText.get("mustLoginFirstLoad"));
			DOM.setStyleAttribute(statusLabel.getElement(), "color", "red");
		}
		else
		{
				
			Label label = new Label(LocaleText.get("formToLoad"));
			table.setWidget(1, 0, label);
			
			String[] formList = UserInfo.getFormForUser(user);			
			listOfForms = new ListBox(false);
			for(String formName : formList)
			{
				listOfForms.addItem(formName);
			}
			if(formList.length == 0)
			{
				statusLabel.setText(LocaleText.get("noBrowserFormToLoad"));
				DOM.setStyleAttribute(statusLabel.getElement(), "color", "red");
			}
			else
			{
				
				Button btnSave = new Button(LocaleText.get("load"), new ClickHandler(){
					public void onClick(ClickEvent event){
						load();
					}
				});
				
				table.setWidget(3, 0, btnSave);
			}
			listOfForms.setWidth("150px");
			table.setWidget(1, 1, listOfForms);
			
			FormUtil.maximizeWidget(listOfForms);
		}
		
		Button btnCancel = new Button(LocaleText.get("cancel"), new ClickHandler(){
			public void onClick(ClickEvent event){
				cancel();
			}
		});
		
		
		
		table.setWidget(2, 0, statusLabel);
		
		table.setWidget(3, 1, btnCancel);
		
	
	
		
		
		

		
		VerticalPanel panel = new VerticalPanel();
		FormUtil.maximizeWidget(panel);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.add(table);
		
		setWidget(panel);
		
		setWidth("400px");

		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				if(listOfForms != null)
				{
					listOfForms.setFocus(true);
					
				}
			}
		});
	}
	
	/**
	 * Called when one selects the save button.
	 */
	private void load(){
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
			
			
			String xmlStr = UserInfo.getForm(user, listOfForms.getItemText(listOfForms.getSelectedIndex()));
			
			if(xmlStr == null)
			{
				statusLabel.setText("The requested form,"+ listOfForms.getItemText(listOfForms.getSelectedIndex())
						+ ", can not be found");
				DOM.setStyleAttribute(statusLabel.getElement(), "color", "red");
				return;
			}			
			try
			{
				controller.loadNewForm(xmlStr);
			}
			catch (Exception e)
			{
				Window.alert(LocaleText.get("error") + ":\n\r" + e.getMessage());
				return;
			}
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
				if(listOfForms != null)
				{
					listOfForms.setFocus(true);
				}
			}
		});
	}
}
