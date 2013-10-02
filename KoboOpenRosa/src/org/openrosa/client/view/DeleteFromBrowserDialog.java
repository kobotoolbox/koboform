package org.openrosa.client.view;

import org.openrosa.client.controller.IFileListener;
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
public class DeleteFromBrowserDialog extends DialogBox {

	/** For capturing the user name. */
	private ListBox listOfForms = null;
	
	/** The widget for organizing widgets in a table format. */
	private FlexTable table = new FlexTable();
	
	/** lets us know how things are going**/
	private Label statusLabel;
	
	/** Show how much memory space we're working with*/
	private Label sizeLabel;
		
	
	/**
	 * Creates a new instance of the login dialog box.
	 */
	public DeleteFromBrowserDialog(){
		setup();
		
	}

	
	/**
	 * Sets up the login widget.
	 */
	private void setup(){
		
		setText(LocaleText.get("deleteFormFromBrowserTitle"));
		statusLabel = new Label();
		
		//first check to see if we're logged in
		String user = UserInfo.getCurrentUser();
		if(user == null)
		{
			statusLabel.setText(LocaleText.get("mustLoginFirstDelete"));
			DOM.setStyleAttribute(statusLabel.getElement(), "color", "red");
		}
		else
		{
			//setup the size label
			
			sizeLabel = new Label((UserInfo.getTotalBytesUsed()/1000.00) + "kb" + LocaleText.get("bytesUsed"));
			table.setWidget(1,1, sizeLabel);
			
			Label label = new Label(LocaleText.get("formsToDelete"));
			table.setWidget(2, 0, label);
			
			String[] formList = UserInfo.getFormForUser(user);			
			listOfForms = new ListBox(true);
			listOfForms.setVisibleItemCount(10);
			
			for(String formName : formList)
			{
				String formSize = ""+((UserInfo.getForm(user, formName).length() * 2.0) / 1000.00);
				listOfForms.addItem(formName + " -- " + formSize + "kb");
			}
			if(formList.length == 0)
			{
				statusLabel.setText(LocaleText.get("noBrowserFormToDelete"));
				DOM.setStyleAttribute(statusLabel.getElement(), "color", "red");
			}
			else
			{
				
				Button btnDelete = new Button(LocaleText.get("delete"), new ClickHandler(){
					public void onClick(ClickEvent event){
						delete();
					}
				});
				
				table.setWidget(4, 0, btnDelete);
			}
			listOfForms.setWidth("250px");
			table.setWidget(2, 1, listOfForms);
			
			FormUtil.maximizeWidget(listOfForms);
		}
		
		Button btnCancel = new Button(LocaleText.get("cancel"), new ClickHandler(){
			public void onClick(ClickEvent event){
				cancel();
			}
		});
		
		
		
		table.setWidget(3, 0, statusLabel);
		
		table.setWidget(4, 1, btnCancel);
		
	
	
		
		
		

		
		VerticalPanel panel = new VerticalPanel();
		FormUtil.maximizeWidget(panel);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.add(table);
		
		setWidget(panel);
		
		setWidth("500px");

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
	private void delete(){
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
			
			//but first double check that you really want to do this
			if(!Window.confirm(LocaleText.get("deleteConfirm")))
			{
				return;
			}

			//if we're logged in and all is good then delete away.
			int selection = -1;
			while( (selection = listOfForms.getSelectedIndex()) != -1)
			{
				String selectedStr = listOfForms.getItemText(selection);
				String formName = selectedStr.substring(0, selectedStr.lastIndexOf(" -- "));
				UserInfo.deleteForm(user, formName);
				listOfForms.removeItem(selection);
			}
			
			
			
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
