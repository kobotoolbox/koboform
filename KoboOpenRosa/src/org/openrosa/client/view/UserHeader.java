package org.openrosa.client.view;

import org.openrosa.client.controller.FormDesignerController;
import org.openrosa.client.postprocess.PostProcessProperties;
import org.purc.purcforms.client.locale.LocaleText;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Header;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;

public class UserHeader extends Header{
		
	Label userStatus = new Label();
	Button logInButton = new Button();
	Button signUpButton = new Button();
	Button logOutButton = new Button();
	Button settingsButton = new Button();
	
	/** The coordinator for execution of commands between stuff */
	private FormDesignerController controller = null;
	
	public UserHeader(FormDesignerController controller)
	{
		this.controller = controller;
		setupComponents();
		setupEventHandlers();
		
	}
	
	private void setupComponents()
	{
		//setup our elements
		//I like margins, it's easier on the eyes, so give me some margins
		//and set the text while we're at it.
		userStatus.setStyleAttribute("margin-right", "20px");
		userStatus.setText(LocaleText.get("notLoggedIn"));
		logInButton.setStyleAttribute("margin-right", "5px");
		logInButton.setText(LocaleText.get("logIn"));
		signUpButton.setStyleAttribute("margin-right", "5px");
		signUpButton.setText(LocaleText.get("signUp"));
		logOutButton.setStyleAttribute("margin-right", "5px");
		logOutButton.setText(LocaleText.get("logOut"));
		logOutButton.hide();
		
		settingsButton.setStyleAttribute("margin-right", "5px");
		settingsButton.setText(LocaleText.get("settings"));
		settingsButton.hide();
		
		
		//add our elements
		addTool(userStatus);
		addTool(settingsButton);
		addTool(logInButton);
		addTool(signUpButton);
		addTool(logOutButton);
	}
	
	private void setupEventHandlers()
	{
		logInButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {								
				controller.logIn();
			}
		});
		
		logOutButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {								
				controller.logOut();
			}
		});
		
		signUpButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {								
				controller.signUp();
			}
		});
		
		settingsButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {								
				controller.setSettings();
			}
		});
	}
	
	/**
	 * Sets the user status string
	 * @param text Text to set the user status string to
	 */
	public void setUserStatusText(String text)
	{
		userStatus.setText(text);
		if(text.equals(LocaleText.get("notLoggedIn")))
		{
			settingsButton.hide();
			logOutButton.hide();
			signUpButton.show();
			logInButton.show();
		}
		else
		{
			settingsButton.show();
			logOutButton.show();
			signUpButton.hide();
			logInButton.hide();
		}
	}

}
