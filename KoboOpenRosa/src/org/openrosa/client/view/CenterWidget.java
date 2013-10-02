package org.openrosa.client.view;

import java.util.List;
import java.util.Set;

import org.openrosa.client.Context;
import org.openrosa.client.controller.FormDesignerController;
import org.openrosa.client.controller.IFileListener;
import org.openrosa.client.controller.ITextListener;
import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.ItextModel;
import org.openrosa.client.util.FormHandler;
import org.purc.purcforms.client.PurcConstants;
import org.purc.purcforms.client.controller.IFormSelectionListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.view.LoginDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedTabPanel;

/**
 * This is the widget that houses the xforms, design and internationalization
 * tab.
 * 
 * @author daniel
 * 
 */
public class CenterWidget extends Composite implements IFileListener,
		IFormSelectionListener, ITextListener {

	private static final int TAB_INDEX_DESIGN = 0;
	// private static final int TAB_INDEX_XFORMS = 1;
	private static final int TAB_INDEX_ITEXT = 2;

	/**
	 * Tab widget housing the contents.
	 */
	private DecoratedTabPanel tabs = new DecoratedTabPanel();

	private XformsTabWidget xformsWidget = new XformsTabWidget(this);
	DesignTabWidget designWidget = new DesignTabWidget(this);
	private TextTabWidget itextWidget = new TextTabWidget(this);

	/** The current form definition object. */
	private FormDef formDef;

	



	/**
	 * this is a flag the onSave() method checks to see if it should show the
	 * xml window when it saves.
	 */
	private boolean showXMLWindowFlag = true;

	/**
	 * The dialog box used to log on the server when the user's session expires
	 * on the server.
	 */
	private static LoginDialog loginDlg = new LoginDialog();

	/**
	 * Static self reference such that the static login call back can have a
	 * reference to proceed with the current action.
	 */
	private static CenterWidget centerWidget;
	
	/**
	 * The tool bar for times when the center widget wants to do things with the tool bar
	 */
	private Toolbar toolbar;
	
	/**
	 * The left panel, for times the center widget wants to do things to the left panel
	 */
	private FormDesignerController controller;



	public CenterWidget() {
		centerWidget = this;

		initDesignTab();
		initXformsTab();

		FormUtil.maximizeWidget(tabs);

		tabs.selectTab(TAB_INDEX_DESIGN);

		// ////////////////////////////!!!!!!!!!!!!!!!!!
		initWidget(designWidget); // / <<<<<<---------------------- This is a
									// gruesome shortcut
		// ///////////////////////////////////////////// The whole
		// 'CenterWidget' should be removed
		// and designWidget should be directly called from FormDesignWidget.java

		// tabs.addSelectionHandler(this);

		FormUtil.maximizeWidget(tabs);
		FormUtil.maximizeWidget(designWidget);
		FormUtil.maximizeWidget(this);

		designWidget.addFormSelectionListener(this);
	}

	private void initDesignTab() {
		tabs.add(designWidget, "Design");
	}

	private void initXformsTab() {
		tabs.add(xformsWidget, "Xforms");
	}

	public void onWindowResized(int width, int height) {
		int shortcutHeight = height - getAbsoluteTop();
		if (shortcutHeight > 50) {
			xformsWidget.adjustHeight(shortcutHeight - 130
					+ PurcConstants.UNITS);
			itextWidget.adjustHeight(shortcutHeight - 50 + PurcConstants.UNITS);
		}

		designWidget.onWindowResized(width, height);
	}
	
	public Toolbar getToolbar() {
		return toolbar;
	}

	public void setToolbar(Toolbar toolbar) {
		this.toolbar = toolbar;
	}
	
	/**
	 * Sets the leftPanel
	 * @param leftPanel
	 */
	public void setController(FormDesignerController controller)
	{
		this.controller = controller;
	}

	/**
	 * This has been refactored for the post FormsTreeView world
	 */
	public void onNew() {		
		designWidget.addNewForm();
	}

	

	
	public void showOpen() {
		xformsWidget.showWindow();
	}
	

	/**
	 * Used to open a form from text
	 * This is post-FTV
	 */
	public void onOpen() {
		
		//if things are dirty, ask if they're sure they want to do this
		if(FormDesignerController.getIsDirty())
		{
			//if the use says no, then bounce
			if(!Window.confirm(LocaleText.get("newFormConfirm")))
			{
				return;
			}
		}
		
		xformsWidget.hideWindow();
		FormUtil.dlg.setText("Opening...");
		FormUtil.dlg.show();


		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				try {
					//openFile();
					xformsWidget.setXform("");
					xformsWidget.showOpenButton(true);
					xformsWidget.showWindow();
					FormUtil.dlg.hide();
				} catch (Exception ex) {
					FormUtil.displayException(ex);
				}
			}
		});
	}

	/**
	 * Used to open text that has been copy and pasted
	 * This is post-FTV
	 */
	public void openText() {
		String xml = xformsWidget.getXform();
		if (xml == null || xml.trim().length() == 0) {
			Window.alert(LocaleText.get("emptyFormError"));
			showOpen();
			return;
		}
		xformsWidget.hideWindow();
		try
		{
			controller.loadNewForm(xml);
		}
		catch (Exception e)
		{
			Window.alert(LocaleText.get("error") + ":\n\r" + e.getMessage());
		}
	}
	
	
	

	/**
	 * Called when you want to save something, I'm going to leave this
	 * alone for now. I'm not crazy about this defereed execution stuff
	 * but out of scope right now
	 */
	public void onSave(boolean showWindow) {
		xformsWidget.hideWindow();
		FormUtil.dlg.setText("Saving...");
		FormUtil.dlg.show();
		showXMLWindowFlag = showWindow;
		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				try {
					saveFile(showXMLWindowFlag);
					xformsWidget.showOpenButton(false);
					xformsWidget.showWindow();
					FormUtil.dlg.hide();
				} catch (Exception ex) {
					FormUtil.displayException(ex);
				}
			}
		});
	}

	/**
	 * This is legit for post-FTV operations
	 * again not crazy about this deferred exeuction
	 * what does it do? why do we need it?
	 */
	public void showItext() {

		FormUtil.dlg.setText("Opening...");
		FormUtil.dlg.show();


		
		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {

				try {

					if (formDef == null)
					{
						FormUtil.dlg.hide();
						return;
					}

					tabs.selectTab(TAB_INDEX_DESIGN);
					
					//update the itext map
					controller.updateLanguage(Context.getLocale(), Context.getLocale(), formDef);
					FormDef form = controller.getSelectedForm();
					
					//clear the itext list
					Context.getItextList().removeAll();
					//copy over the map to the list
					Set<String> keys = form.getITextMap().keySet();
					for(String key : keys)
					{
						ItextModel itext = form.getITextMap().get(key);
						Context.getItextList().add(itext);
					}
					
					itextWidget.loadItext(Context.getItextList());
					itextWidget.showWindow();

					FormUtil.dlg.hide();
				} catch (Exception ex) {
					FormUtil.displayException(ex);
				}
			}
		});
	}

	/**
	 * Save file is good to go post-FTV
	 * @param showWindow
	 */
	private void saveFile(boolean showWindow) {

		FormDef form = controller.getSelectedForm();
		if(form == null)
		{
			return;
		}

		String xml = FormHandler.writeToXML(form);

		
		itextWidget.loadItext(Context.getItextList());

		xformsWidget.setXform(xml);
		if (showWindow) {
			xformsWidget.showWindow();
		}
	}
	
	
	

	/**
	 * For the moment this is cool, but i have my eye on it
	 */
	public void onFormItemSelected(Object formItem) {
		if (!(formItem instanceof FormDef))
			return;

		FormDef formDef = (FormDef) formItem;

		this.formDef = formDef;
	}


	/**
	 * This is cool post FTV
	 */
	public void onSaveItext(List<ItextModel> itext) {
		
		//we're just going to use the itext list to remake the itextmap
		//and then use updatelanguage from the leftpanel and update everything
		
		formDef.getITextMap().clear();
		
		for(ItextModel itextModel: itext)
		{
			formDef.getITextMap().put((String)(itextModel.get("id")), itextModel);
		}
		//use null as the old locale since we don't want to save the old
		//locale values, we want to update everything.
		controller.updateLanguage(Context.getLocale(), null, formDef);
						
	}

	/**
	 * On save is go to go post-FTV
	 */
	public void onSaveFile(){

		
		FormUtil.dlg.setText("Saving...");
		FormUtil.dlg.show();
		GWT.runAsync(new RunAsyncCallback() {
	          public void onFailure(Throwable caught) {}
	          public void onSuccess() {	       
				//DeferredCommand.addCommand(new Command() {
	        	  Scheduler scheduler = Scheduler.get();
	        	  scheduler.scheduleDeferred(new Command() {
					public void execute() {
					    FormDef form = controller.getSelectedForm();				      				
						try{
							if(form != null){
								saveFile(false);
								FormUtil.dlg.hide();
		
								String fileName = "filename";
								fileName = form.getName();
								String xmlFormStr = FormHandler.writeToXML(form);
								SaveToFileDialog dlg = SaveToFileDialog.getInstnace(xmlFormStr, fileName);
								dlg.center();	
							}
							else{
								FormUtil.dlg.hide();
								Window.alert("No form to save");
							}
						}
						catch(Exception ex){
							FormUtil.displayException(ex);
						}
					}
				});
	          }
		});
	}

	/**
	 * onOpenFile is good to go post-FTV
	 */
	public void onOpenFile() {
		
		//if things are dirty, ask if they're sure they want to do this
		if(FormDesignerController.getIsDirty())
		{
			//if the use says no, then bounce
			if(!Window.confirm(LocaleText.get("newFormConfirm")))
			{
				return;
			}
		}
		
		OpenFromFileDialog dlg = OpenFromFileDialog.getInstnace(controller);
		dlg.center();
		
	}

	
	
	/**
	 * Pulls the XML from java script that the applet left waiting for us.
	 * @return
	 */
	public static native void consoleLog(String text)
	/*-{
		$wnd.console.log(text);
	}-*/;
}
