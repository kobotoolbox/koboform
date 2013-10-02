package org.openrosa.client.controller;

import org.openrosa.client.view.Toolbar;


/**
 * Interface for listening to file New, Open and Save events.
 * 
 * @author daniel
 *
 */
public interface IFileListener {

	void onNew();
	void onOpen();
	void openText();
	void showOpen();
	void onSave(boolean showWindow);
	void showItext();
	void onSaveFile();
	void onOpenFile();
	public void setToolbar(Toolbar toolbar);
	public void setController(FormDesignerController formDesignerController);
}
