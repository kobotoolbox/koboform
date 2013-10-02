package org.purc.purcforms.client.controller;

import org.purc.purcforms.client.model.Locale;


/**
 * This interface is implemented by those who want to listen to global events 
 * of the form designer. An examples of such global events are: New Form, Open,
 * Save, Close, Print, Show About Info, and more.
 * 
 * @author daniel
 *
 */
public interface IFormDesignerListener extends IFormActionListener{

	
	
	/**
	 * Closes the form designer.
	 */
	public void closeForm();
	
	/** 
	 * Toggles the visibility of the toolbar.
	 */
	public void viewToolbar();
	
	/**
	 * Shows the form designer's help files.
	 */
	public void showHelpContents();
	
	/**
	 * Shows the about dialog box.
	 */
	public void showAboutInfo();
	
	/**
	 * Shows tbe list of languages or locales supported by the form designer.
	 */
	public void showLanguages();
	
	/**
	 * Shows a list of user options or settings for the form designer.
	 */
	public void showOptions();
	
	/**
	 * Aligns widgets on design surface to the left of the widget which was selected last.
	 */
	public void alignLeft();
	
	/**
	 * Aligns widgets on design surface to the right of the widget which was selected last.
	 */
	public void alignRight();
	
	/**
	 * Aligns widgets on design surface to the top of the widget which was selected last.
	 */
	public void alignTop();
	
	/**
	 * Aligns widgets on design surface to the bottom of the widget which was selected last.
	 */
	public void alignBottom();
	
	/**
	 * Makes widgets on design surface to be the same size as the widget which was selected last.
	 */
	public void makeSameSize();
	
	/**
	 * Makes widgets on design surface to be the same height as the widget which was selected last.
	 */
	public void makeSameHeight();
	
	/**
	 * Makes widgets on design surface to be the same width as the widget which was selected last.
	 */
	public void makeSameWidth();
	
	/**
	 * Formats xml with easily readable indenting. The formated is what is currently 
	 * selected (Xforms source, layout xml, labguage xml or model xml
	 */
	public void format();
	
		
	
	/**
	 * Changes the current locale of the form designer.
	 * 
	 * @param locale the locale to change to.
	 */
	public boolean changeLocale(Locale locale);
	
		
	
}
