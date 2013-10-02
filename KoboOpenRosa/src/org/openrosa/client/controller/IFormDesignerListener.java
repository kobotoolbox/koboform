package org.openrosa.client.controller;

import java.util.List;

import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.IFormElement;
import org.openrosa.client.postprocess.PostProcessProperties;

/**
 * This interface is implemented by those who want to listen to global events 
 * of the form designer. An examples of such global events are: New Form, Open,
 * Save, Close, Print, Show About Info, and more. Now with a method for 
 * Post Processing
 * 
 * @author Etherton
 *
 */
public interface IFormDesignerListener extends
		org.purc.purcforms.client.controller.IFormDesignerListener {
	
	
	/**
	 * Opens a new XForm.
	 */
	public FormDef newForm();
	
	/**
	 * Takes in a string of xml and then validates it to the user
	 * @param xmlStr XML string to validate
	 */
	public void validate(String xmlStr);
	
	/**
	 * Call this to hide the applets when we want our screen back
	 */
	public void hideApplets();
	
	
	/**
	 * Opens up the post processor for making something usefull out of all that
	 * XML.
	 */
	public void postProcess(PostProcessProperties postProcessProperties);
	
	/**
	 * Lets users sign in so they can access specific stuff
	 */
	public void logIn();
	
	/**
	 * Lets users sign out so the next guy or girl can't steal their ideas
	 */
	public void logOut();
	
	/**
	 * Lets noobs sign up and start using the FB. And the people rejoice. 
	 */
	public void signUp();
	
	
	/**
	 * When the user wants to save the current form to the browser
	 */
	public void onSaveToBrowser();
	
	/**
	 * When the user wants to load a form from the browser
	 */
	public void onLoadFromBrowser();
	
	
	/**
	 * When the user wants to blow stuff away that's in the browser's memory
	 */
	public void onDeleteFromBrowser();
	

	/**
	 * Shows the help page in a new tab
	 */
	public void showHelpPage();
	
	public List<IFormElement> deleteSelectedItem();
	
	/**
	 * Used to add cascading questions
	 */
	public void importDataFromCSV();

	/**
	 * Used to add a data instance to a form.
	 */
	public void addDataInstance();
	

	/**
	 * Used to add a data element to a form.
	 */
	public void addDataElement();
	
	/**
	 * Used to add blocks of questions from the XML text of a form
	 */
	public void addBlockFromText();
	
	/**
	 * Used to add blocks of questions from the browser's storage
	 */
	public void addBlockFromHTML5Local();
	
	/**
	 * Used to add blocks of questions from the file system,
	 * which here to should be called the "library"
	 */
	public void addBlockFromLibrary();
	
	/**
	 * Used to copy selected questions in to the question library
	 * If it's just one question it'll be a question.
	 * If it's just options, it'll be options.
	 * If it's multiple questions it'll be a block.
	 * Everything else will resutl in an error
	 */
	public void copySelectedToLibraryBut();
	
	/**
	 * Used to save an entire form as a template
	 */
	public void onSaveAsTemplate();
	
	/**
	 * Used to load a new form from the "templates" which are way different from just loading a form... trust me.
	 */
	public void loadTemplate();
	
	
	public void addNewChildItem();
	
	/**
	 * Used to copy something to the clipboard.
	 */
	public void copyItem();
	
	/**
	 * Used to copy something to the clipboard, so we can eventually paste it and cut it.
	 */
	public void cutItem(); 
	
	/**
	 * Used to paste things that have been copied or pasted
	 */
	public void pasteItem();
	
	/**
	 * Called when the property of a form item (form,page,question or question option) 
	 * is changed. Such properties could be, Text, Help Text, Binding, Data Type, Visibility, and more.
	 * 
	 * @param formItem the item which has been changed.
	 * @return the new item in case the called has changed it.
	 */
	public Object onFormItemChanged(Object formItem);
	
	/**
	 * Called when it is time to deleted the kids of a form item (QuestionDef,PageDef).
	 * 
	 * @param formItem the form item whose kids are to be deleted.
	 */
	public void onDeleteChildren(Object formItem);
	
	/**
	 * This little cuttie gets called when you want to move things around.
	 * Most likely this is happending because there's a drag drop operation 
	 * going on, but other things could cause it as well.
	 * @param thingToMove The item to move
	 * @param newParentOfThingToMove The new parent of the thing we're moving
	 * @param newPosition the new position in the parent's kids of the thing we're moving
	 */
	public void moveFormObjects(IFormElement thingToMove, IFormElement newParentOfThingToMove, int newPosition, IFormUIListener ui);
	
}
