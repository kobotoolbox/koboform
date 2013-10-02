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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
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
public class KoboLoginDialog extends DialogBox {

	/** For capturing the user name. */
	private TextBox txtUserName;
	
	/** For capturing the user password. */
	private PasswordTextBox txtPassword;
	
	/** For comparing the user password. */
	private PasswordTextBox txtPasswordConfirm;
	
	/** The widget for organising widgets in a table format. */
	private FlexTable table = new FlexTable();
	
	/** keeps track of the mode we're in**/
	private boolean isSignUp = false;
	
	/** lets us know how things are going**/
	private Label statusLabel;
	
	private FormDesignerController fdc = null;
	
	/**
	 * Creates a new instance of the login dialog box.
	 */
	public KoboLoginDialog(FormDesignerController fdc){
		this.fdc = fdc;
		setup();
	}
	
	/**
	 * Creates a new instance of the login dialog box. 
	 * and sets it up for new user sign up if the isSignup is true
	 * @param isSignUp if true then this dialog will sign up a user
	 */
	public KoboLoginDialog(boolean isSignUp, FormDesignerController fdc){
		this.isSignUp = isSignUp;
		this.fdc = fdc;
		setup();
		
	}
	
	/**
	 * Sets up the login widget.
	 */
	private void setup(){
		
		if(isSignUp){setText(LocaleText.get("newUserAuthenticationPrompt"));}
		else {setText(LocaleText.get("authenticationPrompt"));}
				
		Label label = new Label(LocaleText.get("userName"));
		table.setWidget(1, 0, label);
		
		txtUserName = new TextBox();
		txtUserName.setWidth("50%");
		table.setWidget(1, 1, txtUserName);
		
		txtUserName.addKeyUpHandler(new KeyUpHandler(){
			public void onKeyUp(KeyUpEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
					txtPassword.setFocus(true);
			}
		});
		
		
		label = new Label(LocaleText.get("password"));
		table.setWidget(2, 0, label);
		
		txtPassword = new PasswordTextBox();
		txtPassword.setWidth("50%");
		table.setWidget(2, 1, txtPassword);
		
		txtPassword.addKeyUpHandler(new KeyUpHandler(){
			public void onKeyUp(KeyUpEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
					login();
			}
		});
		
		if(isSignUp)
		{
			label = new Label(LocaleText.get("confirmPassword"));
			table.setWidget(3, 0, label);
			
			txtPasswordConfirm = new PasswordTextBox();
			txtPasswordConfirm.setWidth("100%");
			table.setWidget(3, 1, txtPasswordConfirm);
			
			txtPasswordConfirm.addKeyUpHandler(new KeyUpHandler(){
				public void onKeyUp(KeyUpEvent event) {
					if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
						login();
					else //for everything else check that this matches what's in the the other password box
					{
						if(txtPasswordConfirm.getText().equals(txtPassword.getText()))
						{
							statusLabel.setText(LocaleText.get("passwordsMatch"));
							DOM.setStyleAttribute(statusLabel.getElement(), "color", "black");		
						}
						else
						{
							statusLabel.setText(LocaleText.get("passwordsDontMatch"));
							DOM.setStyleAttribute(statusLabel.getElement(), "color", "red");		
						}
					}
				}
			});
		}
		
		String loginButtonText = isSignUp ? LocaleText.get("signUp") : LocaleText.get("login");
		
		Button btnLogin = new Button(loginButtonText, new ClickHandler(){
			public void onClick(ClickEvent event){
				login();
			}
		});
		
		Button btnCancel = new Button(LocaleText.get("cancel"), new ClickHandler(){
			public void onClick(ClickEvent event){
				cancel();
			}
		});
		
		statusLabel = new Label();
		
		table.setWidget(4, 0, statusLabel);
		table.setWidget(6, 0, btnLogin);
		table.setWidget(6, 1, btnCancel);
		
		FlexCellFormatter formatter = table.getFlexCellFormatter();
		formatter.setColSpan(4, 0, 3);
		formatter.setColSpan(5, 0, 3);
		formatter.setColSpan(6, 0, 2);
		formatter.setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		formatter.setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		formatter.setHorizontalAlignment(3, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		formatter.setHorizontalAlignment(5, 0, HasHorizontalAlignment.ALIGN_CENTER);
		
		formatter.setWidth(1, 1, "50%");
		formatter.setWidth(2, 1, "50%");
		formatter.setWidth(3, 1, "50%");
		
		FormUtil.maximizeWidget(txtUserName);
		FormUtil.maximizeWidget(txtPassword);
		
		VerticalPanel panel = new VerticalPanel();
		FormUtil.maximizeWidget(panel);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.add(table);
		
		setWidget(panel);
		
		setWidth("500px");

		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				txtUserName.setFocus(true);
			}
		});
	}
	
	/**
	 * Called when one selects the OK button.
	 */
	private void login(){
		
		if(isSignUp)
		{
			signUpUser();
		}
		else
		{
			loginUser();
		}
	
	}
	
	
	
	/**
	 * Called to log a user in
	 */
	private void signUpUser()
	{
		if(UserInfo.signUp(txtUserName.getText(), txtPassword.getText()))
		{
			hide();
		}
		else
		{
			//clearUserInfo();
			statusLabel.setText(LocaleText.get("userNameAlreadyExists"));
			DOM.setStyleAttribute(statusLabel.getElement(), "color", "red");			
			txtUserName.setFocus(true);
		}
	}
	
	
	
	/**
	 * Called to log a user in
	 */
	private void loginUser()
	{

		if(UserInfo.logIn(txtUserName.getText(), txtPassword.getText()))
		{
			hide();
		}
		else
		{
			//clearUserInfo();
			statusLabel.setText(LocaleText.get("invalidUser"));
			DOM.setStyleAttribute(statusLabel.getElement(), "color", "red");			
			txtUserName.setFocus(true);
		}
	}
	
	/**
	 * Called when the user selects the CANCEL button.
	 */
	private void cancel(){
		hide();
	}
	
	/**
	 * Clears previously entered user name and password.
	 */
	public void clearUserInfo(){
		txtUserName.setText(null);
		txtPassword.setText(null);
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
				txtUserName.setFocus(true);
			}
		});
	}
}
