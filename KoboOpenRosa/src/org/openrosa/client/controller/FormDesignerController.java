package org.openrosa.client.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.openrosa.client.Context;
import org.openrosa.client.model.DataDef;
import org.openrosa.client.model.DataInstanceDef;
import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.GroupDef;
import org.openrosa.client.model.IFormElement;
import org.openrosa.client.model.OptionDef;
import org.openrosa.client.model.QuestionDef;
import org.openrosa.client.model.TreeModelItem;
import org.openrosa.client.postprocess.PostProcessProperties;
import org.openrosa.client.util.FormHandler;
import org.openrosa.client.util.GracefulShutDown;
import org.openrosa.client.util.UserInfo;
import org.openrosa.client.view.AddBlockFromBrowserDialog;
import org.openrosa.client.view.AddBlockFromLibraryDialog;
import org.openrosa.client.view.AddBlockFromTextDialog;
import org.openrosa.client.view.CenterPanel;
import org.openrosa.client.view.DeleteFromBrowserDialog;
import org.openrosa.client.view.ImportDataCsvDialog;
import org.openrosa.client.view.KoboLoginDialog;
import org.openrosa.client.view.KoboSettingsDialog;
import org.openrosa.client.view.LeftPanel;
import org.openrosa.client.view.LoadFromBrowserDialog;
import org.openrosa.client.view.SPSSDialog;
import org.openrosa.client.view.SaveToBrowserDialog;
import org.openrosa.client.view.SaveToLibraryDialog;
import org.openrosa.client.view.Toolbar;
import org.purc.purcforms.client.AboutDialog;
import org.purc.purcforms.client.PurcConstants;
import org.purc.purcforms.client.controller.IFormSaveListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.Locale;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.util.LanguageUtil;
import org.purc.purcforms.client.view.LocalesDialog;
import org.purc.purcforms.client.view.LoginDialog;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;

/**
 * Controls the interactions between the menu, tool bar and various views (eg
 * Left and Center panels) for the form designer.
 * 
 * @author daniel
 * 
 */
public class FormDesignerController implements IFormDesignerListener, IFormChangeListener {

	/** The panel on the right hand side of the form designer. */
	private CenterPanel centerPanel;

	/** The panel on the left hand side of the form designer. */
	private LeftPanel leftPanel;
	
	private ArrayList<IFormUIListener> listeningUIComponents = null;

	private IFileListener fileListener = null;
	
	private static boolean isDirty = false;
	

	/**
	 * Setter for CenterWidget
	 * @param centerWidget center widget to share with this controller
	 */
	public void setCenterWidget(IFileListener fileListener) {
		this.fileListener = fileListener;
		fileListener.setController(this);
	}

	/**
	 * The identifier of the loaded or opened form.
	 */
	private Integer formId;

	/**
	 * The listener to form save events.
	 */
	private IFormSaveListener formSaveListener;

	// These are constants to remember the current action during the login call
	// back
	// such that we know which action to execute.
	/** No current action. */
	private static final byte CA_NONE = 0;

	/** Action for loading a form definition. */
	private static final byte CA_LOAD_FORM = 1;

	/** Action for saving form. */
	private static final byte CA_SAVE_FORM = 2;

	/** Action for refreshing a form. */
	private static final byte CA_REFRESH_FORM = 3;

	/** Action for setting file contents. */
	private static final byte CA_SET_FILE_CONTENTS = 4;

	/**
	 * The current action by the time to try to authenticate the user at the
	 * server.
	 */
	private static byte currentAction = CA_NONE;

	/**
	 * The dialog box used to log on the server when the user's session expires
	 * on the server.
	 */
	private static LoginDialog loginDlg = new LoginDialog();

	/**
	 * Static self reference such that the static login call back can have a
	 * reference to proceed with the current action.
	 */
	private static FormDesignerController controller;

	/** The object that is being refreshed. */
	private Object refreshObject;
	
	
	/**
	 * Timer object for autosave.
	 */
	private AutoSaveTimer autoSaveTimer;

	/**
	 * Constructs a new instance of the form designer controller.
	 * 
	 * @param centerPanel
	 *            the right hand side panel.
	 * @param leftPanel
	 *            the left hand side panel.
	 */
	public FormDesignerController(CenterPanel centerPanel, LeftPanel leftPanel) {
		this.leftPanel = leftPanel;
		this.centerPanel = centerPanel;
		
		//setup the listening UI components array list and add the left panel
		listeningUIComponents = new ArrayList<IFormUIListener>();
		listeningUIComponents.add(leftPanel);

		this.centerPanel.setFormDesignerListener(this);


		controller = this;
		
		autoSaveTimer = new AutoSaveTimer(this);
		autoSaveTimer.scheduleRepeating(1000 * 60 * 5);
						
		
		//Capture close events
		Window.addWindowClosingHandler(new Window.ClosingHandler() {
			
			@Override
			public void onWindowClosing(ClosingEvent event) {
				String msg = "All clean";
				if(getIsDirty())
				{
					msg = LocaleText.get("leaveUnsaved");
					event.setMessage(msg);	
				}
				
				
			}
		});
		
		
		//When we're closed save the graceful shutdown state
		Window.addCloseHandler(new CloseHandler<Window>() {

			@Override
			public void onClose(CloseEvent<Window> event) {
				//get the current user
				String userName = UserInfo.getCurrentUser();
				if(userName == null)
				{
					return;
				}
				
				UserInfo.setGracefulShutDown(userName, GracefulShutDown.graceful);
				
			}
		});
	}

	
	/**
	 * This method is called every five minutes by the autoSaveTimer to auto save
	 * the user's work
	 */
	public void autoSave()
	{
		//first check and see if there are any forms to auto save.
		FormDef selectedForm = getSelectedForm();
		if(selectedForm == null)
		{			
			return;
		}
		
		//now check if we're logged in as someone, if so, get that info.		
		String user = UserInfo.getCurrentUser();
		if(user == null)
		{
			//for the time being we don't auto save form for non-logged in users
			return;
		}
		
		
		
		//get the xml
		String xml = null;
		//seems that often there are issues with auto save, so if it crashes, just ignore it
		try
		{
			xml = FormHandler.writeToXML(selectedForm);
		}
		catch(Exception E)
		{
			return;
		}
		if(xml == null)
		{
			return;
		}
		
	
		try
		{
			Date now = new Date();
			DateTimeFormat dtf = DateTimeFormat.getFormat("h:mm a MM/dd");
			//save the form
	        String dateStr = dtf.format(now);
						
			
			String autoSaveName = "Auto Save - " + dateStr;
			UserInfo.setForm(user, autoSaveName, xml);
			
			//get the old autosave name;			
			String oldAutoSaveName = UserInfo.getLastAutoSaveName(user);
			if(oldAutoSaveName != null)
			{
				UserInfo.deleteForm(user, oldAutoSaveName);
			}
			
			//set the new autosave name
			UserInfo.setLastAutoSaveName(user, autoSaveName);
		}
		catch (Exception e)
		{
			Window.alert("Insuffecient space in browser memory to complete auto save. Please delete an unneccisary form from the browser's memroy");
		}
		
	}
	


	/**
	 * Etherton - This has been refactored
	 * @see org.purc.purcforms.client.controller.IFormActionListener#addNewChildItem()
	 */
	public void addNewChildItem() {
		//get the item that was selected when this button was pushed
		List<IFormElement> selectedElements = leftPanel.getSelectedItems();
		IFormElement newElement = null;
		IFormElement insertPoint = null;
		//loop through and pull out the selected item that's an option or a select/select1 question
		for(IFormElement elem : selectedElements)
		{
			if(elem instanceof OptionDef)
			{
				//if it's an option, get the parent
				insertPoint = (QuestionDef)(elem.getParent());
				newElement = FormHandler.addNewOptionDef((QuestionDef)insertPoint);
				break;
			}
			else if(elem instanceof QuestionDef && (((QuestionDef)elem).getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE
					|| ((QuestionDef)elem).getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE))
			{
				//if it's a select or select 1 question then add the option
				insertPoint = (QuestionDef)(elem);
				newElement = FormHandler.addNewOptionDef((QuestionDef)insertPoint);
				break;
			}			
			else if(elem instanceof QuestionDef && (((QuestionDef)elem).getDataType() == QuestionDef.QTN_TYPE_REPEAT))
			{
				//if it's a select or select 1 question then add the option
				insertPoint = (QuestionDef)(elem);
				newElement = FormHandler.addNewQuestion(insertPoint.getFormDef(), QuestionDef.QTN_TYPE_LIST_EXCLUSIVE, insertPoint);
				break;
			}
			else if(elem instanceof GroupDef)
			{
				//if it's a select or select 1 question then add the option
				insertPoint = (GroupDef)(elem);
				newElement = FormHandler.addNewQuestion(insertPoint.getFormDef(), QuestionDef.QTN_TYPE_LIST_EXCLUSIVE, insertPoint);
				break;
			}
			
		}//end for loop
		
		//watch for nulls
		if(insertPoint == null)
		{
			return;
		}
		
		//loop over UIs and let them know there was a change
		for(IFormUIListener l : listeningUIComponents)
		{
			if(newElement instanceof OptionDef)
			{
				l.handleAddNewOption((QuestionDef)insertPoint, (OptionDef)newElement);
			}
			else if(newElement instanceof QuestionDef)
			{
				l.handleAddNewElement(insertPoint.getFormDef(), insertPoint, newElement);
			}
		}
				
	}//end addNewChildren()

	public IFormElement addNewQuestion(int dataType) {
		//get the current form
		FormDef form = this.getSelectedForm();
		//make sure a form exists, if not, make one
		if(form == null)
		{
			this.newForm();
			form = this.getSelectedForm();
		}
		//get the currently selected form item, so we know where to add the new question
		List<IFormElement> list = this.getSelectedItems();
		//if nothing is selected, set to null, otherwise choose the first item
		IFormElement insertPoint = list.size() != 0 ? list.get(0) : null;
		//if the IFormElement is a option then go up one
		insertPoint = insertPoint instanceof OptionDef ? insertPoint.getParent() : insertPoint;
		//add the new question to the form
		IFormElement newQuestion = FormHandler.addNewQuestion(form, dataType, insertPoint);
		//alert all UI components that want to know about this new question
		for(IFormUIListener listener : listeningUIComponents)
		{
			listener.handleAddNewElement(form, insertPoint, newQuestion);
			listener.setItemAsSelected(newQuestion);
		}
		
		
		
		return newQuestion;
		
	}


	/**
	 * Opens a new browser window with a given title and html contents.
	 * 
	 * @param title
	 *            the window title.
	 * @param html
	 *            the window html contents.
	 */
	public static native void openForm(String title, String html) /*-{
		var win =window.open('','purcforms','width=350,height=250,menubar=1,toolbar=1,status=1,scrollbars=1,resizable=1');
		win.document.open("text/html","replace");
		win.document.writeln('<html><head><title>' + title + '</title></head><body bgcolor=white onLoad="self.focus()">'+html+'</body></html>');
		win.document.close();
	}-*/;

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#closeForm()
	 */
	public void closeForm() {
		String url = FormUtil.getCloseUrl();
		if (url != null && url.trim().length() > 0)
			Window.Location.replace(url);
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormActionListener#deleteSelectedItems()
	 * Etherton, this is refactored
	 */
	public List<IFormElement> deleteSelectedItem() {
		//get what's selected
		List<IFormElement> selectedElements = leftPanel.getSelectedItems();
		//ask the form hanlder to handle removing these elements
		List<IFormElement> deletedItems = FormHandler.delete(selectedElements);
		//notify the UI that this has happened.
		for(IFormUIListener l : listeningUIComponents)
		{
			l.handleDelete(deletedItems);
		}		
		
		return deletedItems;
	}
	

	/**
	 * @see org.purc.purcforms.client.controller.IFormActionListener#newForm()
	 */
	public FormDef newForm()
	{
		return newForm(true);
	}
	
	/**
	 * Used to specify if we want a 
	 * @param withDefaultQuestion
	 * @return
	 */
	public FormDef newForm(boolean withDefaultQuestion) 
	{
		//if we're dirty, then tell the user
		if(FormDesignerController.isDirty)
		{
			if(!Window.confirm(LocaleText.get("newFormConfirm")))
			{
				return null;
			}		
		}
		//call the form handler to create a new form
		FormDef form = FormHandler.newForm(withDefaultQuestion);
		//make sure it's valid
		if(form != null)
		{
			//notify the UIs that a new form has been loaded
			for(IFormUIListener l : listeningUIComponents)
			{
				l.handleLoadNewForm(form);
			}
			//setup the languages
			Context.getItextList().removeAll();
			form.getLocales().add(new Locale("English", "English"));
			Context.setLocales(form.getLocales());
			Context.setLocale(form.getLocales().get(0));
			Context.setDefaultLocale(form.getLocales().get(0));
		}
		return form;
	}

	

	

	

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#showAboutInfo()
	 */
	public void showAboutInfo() {
		AboutDialog dlg = new AboutDialog();
		dlg.setAnimationEnabled(true);
		dlg.center();
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#alignLeft()
	 */
	public void showHelpContents() {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#showLanguages()
	 */
	public void showLanguages() {
		LocalesDialog dlg = new LocalesDialog();
		dlg.center();
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#showOptions()
	 */
	public void showOptions() {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#viewToolbar()
	 */
	public void viewToolbar() {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#alignLeft()
	 */
	public void alignLeft() {
		centerPanel.alignLeft();
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#alignRight()
	 */
	public void alignRight() {
		centerPanel.alignRight();
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#alignTop()
	 */
	public void alignTop() {
		centerPanel.alignTop();
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#alignBottom()
	 */
	public void alignBottom() {
		centerPanel.alignBottom();
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#makeSameHeight()
	 */
	public void makeSameHeight() {
		centerPanel.makeSameHeight();
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#makeSameSize()
	 */
	public void makeSameSize() {
		centerPanel.makeSameSize();
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#makeSameWidth()
	 */
	public void makeSameWidth() {
		centerPanel.makeSameWidth();
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormActionListener#copyItem()
	 * Etherton - This has been refactored
	 */
	public void copyItem() 
	{
		//get the items that are selected to be copied.
		List<IFormElement> selectedElements = leftPanel.getSelectedItems();
		FormHandler.copy(selectedElements);
		//let the UI know a copy has occured
		for(IFormUIListener l : listeningUIComponents)
		{
			l.handleCopy(selectedElements);
		}
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormActionListener#cutItem()
	 * Etherton - This has been refactored
	 */
	public void cutItem() {
		//get the items that are selected to be cut.
		List<IFormElement> selectedElements = leftPanel.getSelectedItems();
		FormHandler.cut(selectedElements);
		//let the UI know a cut has occured
		for(IFormUIListener l : listeningUIComponents)
		{
			l.handleCut(selectedElements);
		}
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormActionListener#pasteItem()
	 * This has been refactored
	 */
	public void pasteItem() {
		if(FormHandler.getIsCutMode())
		{
			pasteCutItem();
		}
		else
		{
			//get what's selected
			List<IFormElement> selectedElements = leftPanel.getSelectedItems();
			//get just one selected item
			IFormElement insertPoint = selectedElements.size() > 0 ? selectedElements.get(0) : null;
			//let the form handler do it's things
			List<IFormElement> newElements = FormHandler.paste(insertPoint);
			//make sure something was pasted
			if(newElements == null || newElements.size() == 0)
			{
				return;
			}
			//call the approprite UIs
			for(IFormUIListener l : listeningUIComponents)
			{
				l.handlePaste(insertPoint, newElements, FormHandler.getClipboard());
			}
		}
	}//end pasteItem


	/**
	 * @see org.purc.purcforms.client.controller.IFormActionListener#pasteItem()
	 * This has been refactored
	 */
	public void pasteCutItem() {
	
		//get what's selected
		List<IFormElement> selectedElements = leftPanel.getSelectedItems();
		//get just one selected item
		IFormElement insertPoint = selectedElements.size() > 0 ? selectedElements.get(0) : null;
		

		
		//let the form handler do it's things
		List<IFormElement> pastedElements = FormHandler.paste(insertPoint);
		//make sure something was pasted
		if(pastedElements == null || pastedElements.size() == 0)
		{
			return;
		}
		
		
		//call the approprite UIs
		for(IFormUIListener l : listeningUIComponents)
		{
			l.handleDelete(pastedElements);
			l.handlePaste(insertPoint, pastedElements, FormHandler.getClipboard());
			
		}
	}//end pasteItem
	
	
	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#format()
	 */
	public void format() {
		// centerPanel.format();
	}



	public void saveForm(String xformXml, String layoutXml, String languageXml,
			String javaScriptSrc) {
		String url = FormUtil.getHostPageBaseURL();
		url += FormUtil.getFormDefUploadUrlSuffix();
		url += FormUtil.getFormIdName() + "=" + this.formId;

		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL
				.encode(url));

		try {
			String xml = xformXml;
			if (layoutXml != null && layoutXml.trim().length() > 0)
				xml += PurcConstants.PURCFORMS_FORMDEF_LAYOUT_XML_SEPARATOR
						+ layoutXml;

			if (languageXml != null && languageXml.trim().length() > 0)
				xml += PurcConstants.PURCFORMS_FORMDEF_LOCALE_XML_SEPARATOR
						+ languageXml;

			if (javaScriptSrc != null && javaScriptSrc.trim().length() > 0)
				xml += PurcConstants.PURCFORMS_FORMDEF_JAVASCRIPT_SRC_SEPARATOR
						+ javaScriptSrc;

			builder.sendRequest(xml, new RequestCallback() {
				public void onResponseReceived(Request request,
						Response response) {
					FormUtil.dlg.hide();
					Window.alert(LocaleText.get("formSaveSuccess"));
				}

				public void onError(Request request, Throwable exception) {
					FormUtil.dlg.hide();
					FormUtil.displayException(exception);
				}
			});
		} catch (RequestException ex) {
			FormUtil.dlg.hide();
			FormUtil.displayException(ex);
		}
	}

	public void saveLocaleText(String languageXml) {
		String url = FormUtil.getHostPageBaseURL();
		url += FormUtil.getFormDefUploadUrlSuffix();
		url += FormUtil.getFormIdName() + "=" + this.formId;
		url += "&localeXml=true";

		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL
				.encode(url));

		try {
			builder.sendRequest(languageXml, new RequestCallback() {
				public void onResponseReceived(Request request,
						Response response) {
					FormUtil.dlg.hide();
					Window.alert(LocaleText.get("formSaveSuccess"));
				}

				public void onError(Request request, Throwable exception) {
					FormUtil.dlg.hide();
					FormUtil.displayException(exception);
				}
			});
		} catch (RequestException ex) {
			FormUtil.dlg.hide();
			FormUtil.displayException(ex);
		}
	}

	/**
	 * Checks if the form designer is in offline mode.
	 * 
	 * @return true if in offline mode, else false.
	 */
	public boolean isOfflineMode() {
		return formId == null;
	}


	/**
	 * Sets the listener to form save events.
	 * 
	 * @param formSaveListener
	 *            the listener.
	 */
	public void setFormSaveListener(IFormSaveListener formSaveListener) {
		this.formSaveListener = formSaveListener;
	}




	

	/**
	 * Reloads forms in a given locale.
	 * 
	 * @param locale
	 *            the locale.
	 */
	public boolean changeLocale(Locale newLocale) {
		//what's the old locale
		Locale oldLocale = Context.getLocale();

		//is this a null form? If so, don't do anything.
		final FormDef formDef = leftPanel.getSelectedForm();
		if (formDef == null)
			return false;

		//get the itext map and then remake everything
		Context.setLocale(newLocale);		
		updateLanguage(newLocale, oldLocale, formDef);
		
		
		

		return true;
	}

	/**
	 * Sets locale text for a given form.
	 * 
	 * @param formId
	 *            the form identifier.
	 * @param locale
	 *            the locale key.
	 * @param text
	 *            the form locale text.
	 */
	private void setLocaleText(Integer formId, String locale, String text) {
		HashMap<String, String> map = Context.getLanguageText().get(formId);
		if (map == null) {
			map = new HashMap<String, String>();
			Context.getLanguageText().put(formId, map);
		}

		map.put(locale, text);
	}

	/**
	 * Gets locale text for a given form.
	 * 
	 * @param formId
	 *            the form identifier.
	 * @param locale
	 *            the locale key.
	 * @return the form locale text.
	 */
	private String getFormLocaleText(int formId, String locale) {
		HashMap<String, String> map = Context.getLanguageText().get(formId);
		if (map != null)
			return map.get(locale);
		return null;
	}

	/**
	 * Sets xforms and layout locale text for a given form.
	 * 
	 * @param formId
	 *            the form identifier.
	 * @param locale
	 *            the locale key.
	 * @param xform
	 *            the xforms locale text.
	 * @param layout
	 *            the layout locale text.
	 */
	public void setLocaleText(Integer formId, String locale, String xform,
			String layout) {
		setLocaleText(formId, locale, LanguageUtil.getLocaleText(xform, layout));
	}

	/**
	 * Sets the default locale used by the form designer.
	 * 
	 * @param locale
	 *            the locale.
	 */
	public void setDefaultLocale(Locale locale) {
		Context.setDefaultLocale(locale);
	}


	



	/**
	 * Called to start post processing stuff
	 */
	public void postProcess(PostProcessProperties postProcessProperties) {
		centerPanel.postProcess(postProcessProperties);
		
	}

	@Override
	public void logIn() {
		KoboLoginDialog ld = new KoboLoginDialog(this);
		ld.center();
		
	}

	@Override
	public void logOut() 
	{		
		UserInfo.logOut();		
	}

	@Override
	public void signUp() {
		KoboLoginDialog ld = new KoboLoginDialog(true, this);
		ld.center();		
	}
	
	public void setSettings()
	{
		
		KoboSettingsDialog ksd = KoboSettingsDialog.getInstnace();
		ksd.center();
	}
	
	
	public void testXML()
	{
		FormDef formDef = getSelectedForm();
		FormHandler.writeToXML(formDef);
	}

	@Override
	public void onSaveToBrowser() {
		//get the current form, if there isn't one bail
		if(leftPanel.getForms().size() != 1)
		{
			return;
		}
		
		FormDef formDef = getSelectedForm();
		SaveToBrowserDialog saveToBrowserDialog = new SaveToBrowserDialog(
					FormHandler.writeToXML(formDef), 
					formDef.getDisplayText());
		saveToBrowserDialog.center();
		
	}

	@Override
	public void onLoadFromBrowser() {
		
		LoadFromBrowserDialog loadFromBrowserDialog = new LoadFromBrowserDialog(controller);
		loadFromBrowserDialog.center();
		
	}


	@Override
	public void showHelpPage() {
		com.google.gwt.user.client.Window.open(LocaleText.get("helpURL"), LocaleText.get("help"), "");
	}

	@Override
	public void validate(String xmlStr) {
		centerPanel.validate(xmlStr);
	}

	@Override
	public void hideApplets() {
		centerPanel.hideApplets();
	}


	@Override
	public void onDeleteFromBrowser() {
		
		DeleteFromBrowserDialog deleteFromBrowserDialog = new DeleteFromBrowserDialog();
		deleteFromBrowserDialog.center();
		
	}

	
	
	/**
	 * returns whether this form has had an unsaved change or not
	 * @return
	 */
	public static boolean getIsDirty()
	{
		return isDirty;
	}
	
	/**
	 * Clears the dirty flag because a save took place, or because a new form was loaded
	 */
	public static void clearIsDirty()
	{
		isDirty = false;
	}
	
	/**
	 * Called whenever there's a change made after a save
	 */
	public static void makeDirty()
	{
		isDirty = true;
	}
	
	
	/**
	 * This is designed to be used when loggin users in and out.
	 * If there is any work that's active this will double check with the user
	 * to make sure they don't lose their work.
	 */
	public boolean clearData()
	{
		//check if there's any unsaved work
		if(getIsDirty())
		{
			if(!Window.confirm(LocaleText.get("leaveUnsaved")))
			{
				return false;
			}
		}
		
		leftPanel.clear();
		centerPanel.hideProperties();
		return true;
	}


	/**
	 * This is called when we want to add cascading questions.
	 * First we pop up a dialog and ask the user to paste a CSV of the
	 * cascading info. The dialog does some light weight parsing of the data. 
	 * Finally we send the data off to the LeftPanel to make questions
	 * for us. 
	 */
	public void importDataFromCSV() {	
		//get the formDef
		List<FormDef> forms = leftPanel.getForms();
		if(forms.size() == 0)
		{
			return;
		}
		//there should be only one form
		FormDef formDef = forms.get(0);
				
		ImportDataCsvDialog addCascadingDialog = new ImportDataCsvDialog(formDef, this);
		addCascadingDialog.center();
	}


	@Override
	public void addDataInstance() {
		//figure out what form to add this data instance to
		List<IFormElement> selectedItems = leftPanel.getSelectedItems();
		//if there's nothing selected bounce
		if(selectedItems.size() == 0) {return;}
		//get the form of the first object we find
		FormDef form = selectedItems.get(0).getFormDef();
		//add the new data instance to the form
		DataInstanceDef did = FormHandler.addDataInstance(form);
		//loop over UIs and let them know there was a change
		for(IFormUIListener l : listeningUIComponents)
		{
			//pass in null so the data instance is added at the root level
			l.handleAddData(null, did);
		}
	}
	
	@Override
	public void addDataElement() {
		//figure out what form to add this data instance to
		List<IFormElement> selectedItems = leftPanel.getSelectedItems();
		//if there's nothing selected bounce
		if(selectedItems.size() == 0) {return;}
		//add the new data element to the form
		DataDef did = FormHandler.addDataElement(selectedItems.get(0));
		//loop over UIs and let them know there was a change
		for(IFormUIListener l : listeningUIComponents)
		{
			l.handleAddData(selectedItems.get(0), did);
		}
		
	}


	/**
	 * Used to insert questions into the existing from
	 * questions in another form's XML
	 */
	@Override
	public void addBlockFromText() {
		//find out if the user is logged in or not
		String user = UserInfo.getCurrentUser();
		if(user == null)
		{
			Window.alert(LocaleText.get("mustBeLoggedInOpenBlocks"));
			return;
		}
		
		//create a dialog box for getting input from the user. Primarily we want the XML and for
		//the user to set a couple of options		
		AddBlockFromTextDialog dialog = new AddBlockFromTextDialog(this);	
		dialog.center();
	}


	@Override
	public void addBlockFromHTML5Local() {
		//create a dialog box for getting input from the user. Primarily we want the XML and for
		//the user to set a couple of options		
		AddBlockFromBrowserDialog dialog = new AddBlockFromBrowserDialog(this);	
		dialog.center();		
	}

	

	
	/**
	 * Used to copy selected questions in to the question library
	 * If it's just one question it'll be a question.
	 * If it's just options, it'll be options.
	 * If it's multiple questions it'll be a block.
	 * Everything else will resutl in an error
	 */	
	public void copySelectedToLibraryBut() {
		
		//find out if the user is logged in or not
		String user = UserInfo.getCurrentUser();
		if(user == null)
		{
			Window.alert(LocaleText.get("mustBeLoggedInSaveBlocks"));
			return;
		}
		
		
		//get a list of selected items
		//this needs to return the selected items in the right order
		List<IFormElement> items = leftPanel.getSelectedItems();

		
		//for the moment we assume we only do one form at a time
		FormDef form = controller.getSelectedForm();
		

		//if nothing is selected then lets bounce
		if(items.size() == 0)
			return;
		
		//update the language
		updateLanguage(Context.getLocale(), Context.getLocale(), form);
		
		
		//if there's only one selected item see if it's a question
		if(items.size() == 1)
		{
			IFormElement element = items.get(0);
		
			
			if(element instanceof QuestionDef) //if it's a question then send it save it as a question
			{
				ArrayList<IFormElement> questions = new ArrayList<IFormElement>();
				questions.add((QuestionDef)element);
				String questionXml = FormHandler.copyQuestionsToNewForm(questions, form, Context.getFormAttrMap());
				SaveToLibraryDialog sld = SaveToLibraryDialog.getInstnace(questionXml, "question");
				sld.center();
			}
			else if (element instanceof GroupDef)
			{
				ArrayList<IFormElement> questions = new ArrayList<IFormElement>();
				questions.add((GroupDef)element);
				String questionXml = FormHandler.copyQuestionsToNewForm(questions, form, Context.getFormAttrMap());
				SaveToLibraryDialog sld = SaveToLibraryDialog.getInstnace(questionXml, "question");
				sld.center();
			}
			
		}
		else //it's either a block or a chunk of select options
		{
			//make sure they're all off the same type
			String lastObjectType = "none";
			for(IFormElement element : items)
			{
			
				if(element instanceof QuestionDef)
				{
					if(lastObjectType != "none" && lastObjectType != "QuestionDef")
					{
						lastObjectType = "mixed";
						break;
					}
					lastObjectType = "QuestionDef";
				}
				else if(element instanceof OptionDef)
				{
					if(lastObjectType != "none" && lastObjectType != "OptionDef")
					{
						lastObjectType = "mixed";
						break;
					}
					lastObjectType = "OptionDef";
				}
				else if(element instanceof GroupDef)
				{
					if(lastObjectType != "none" && lastObjectType != "OptionDef")
					{
						lastObjectType = "mixed";
						break;
					}
					lastObjectType = "GroupDef";
				}
				else
				{
					lastObjectType = "unsuported";
					break;
				}
			}
			
			if(lastObjectType == "unsuported")
			{
				Window.alert(LocaleText.get("selectedDataTypeCanNotBeSavedToLibrary"));
				return;
			}
			
			else if(lastObjectType == "QuestionDef" || lastObjectType == "GroupDef" || lastObjectType == "mixed")
			{
				ArrayList<IFormElement> questions = new ArrayList<IFormElement>();
				for(IFormElement element : items)
				{
					questions.add((IFormElement)element);
				}
				
				SaveToLibraryDialog sld = SaveToLibraryDialog.getInstnace("block", questions, form, Context.getFormAttrMap());
				sld.center();
			}
		}//end else dealing with more than one question/select
	}//end copy stuff to library method


	@Override
	public void addBlockFromLibrary() {

		//find out if the user is logged in or not
		String user = UserInfo.getCurrentUser();
		if(user == null)
		{
			Window.alert(LocaleText.get("mustBeLoggedInOpenBlocks"));
			return;
		}
		//now that we know we're logged in, lets talk about what kinds of items the user can
		//search for
		//bit masking time baby
		//now time for some good old bit masking, heck ya, brings a tear to my eye.
		int blockBitMask = 1;
		int questionBitMask = 2;
		int optionBitMask = 4;
		int templateBitMask = 8;
		
		int bitMask = blockBitMask | questionBitMask;
		
		//Either way we're going to show blocks and questions, and not templates here,
		//so the question is, is the user sitting on a select question or not?
		List<IFormElement> items = leftPanel.getSelectedItems();
		//only if there's one item selected and it's
		if(items.size() == 1 && (((TreeModelItem)items.get(0)).getUserObject() instanceof QuestionDef && 
				( ((QuestionDef)((TreeModelItem)items.get(0)).getUserObject()).getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE ||
				((QuestionDef)((TreeModelItem)items.get(0)).getUserObject()).getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE)) ||
				((TreeModelItem)items.get(0)).getUserObject() instanceof OptionDef )
		{
			bitMask = bitMask | optionBitMask;
		}
		
		AddBlockFromLibraryDialog dialog = AddBlockFromLibraryDialog.getInstance(this, bitMask);	
		dialog.center();
	}


	@Override
	public void onSaveAsTemplate() {
		//find out if the user is logged in or not
		String user = UserInfo.getCurrentUser();
		if(user == null)
		{
			Window.alert(LocaleText.get("mustBeLoggedInSaveTemplates"));
			return;
		}
		FormDef form = getSelectedForm();
		//get the xml for the whole form
		String formXml = FormHandler.writeToXML(form);
		
		//now save all that goodness
		SaveToLibraryDialog sld = SaveToLibraryDialog.getInstnace(formXml, "template");
		sld.center();
		
	}


	@Override
	public void loadTemplate() {

		//find out if the user is logged in or not
		String user = UserInfo.getCurrentUser();
		if(user == null)
		{
			Window.alert(LocaleText.get("mustBeLoggedInOpenTemplates"));
			return;
		}
		//now that we know we're logged in, lets talk about what kinds of items the user can
		//search for
		//bit masking time baby
		//now time for some good old bit masking, heck ya, brings a tear to my eye.

		int templateBitMask = 8;
		
		int bitMask = templateBitMask;
				
		AddBlockFromLibraryDialog dialog = AddBlockFromLibraryDialog.getInstance(this, bitMask);	
		dialog.center();
		
	}
	
	/**
	 * Returns a list of form elements that are selected by the UI
	 * @return
	 */
	public List<IFormElement> getSelectedItems()
	{
		return leftPanel.getSelectedItems();
	}
	
	
	
	/**
	 * Gets the selected form.
	 * @return the form definition object.
	 */
	public FormDef getSelectedForm(){
		return leftPanel.getSelectedForm();
	}


	@Override
	public Object onFormItemChanged(Object formItem) {
		formItem = FormHandler.handleFormItemChanged((IFormElement)formItem);
		for(IFormUIListener l : listeningUIComponents)
		{
			formItem = l.handleFormItemChanged(formItem);
		}
		return formItem;
	}


	@Override
	public void onDeleteChildren(Object formItem) {
		//get a list of the kids, we can't use the same list of the object since it's troublesome to delete things from a list you're looping over
		ArrayList<IFormElement> selectedElements = new ArrayList<IFormElement>();
		//make sure their are children
		if(((IFormElement)formItem).getChildren() == null)
		{
			return;
		}
		
		for(IFormElement element : ((IFormElement)formItem).getChildren())
		{
			selectedElements.add(element);
		}
		//ask the form handler to update the form itself
		List<IFormElement> deletedItems = FormHandler.delete(selectedElements);
		//the have the UIs update as well
		for(IFormUIListener l : listeningUIComponents)
		{
			l.handleDelete(deletedItems);
		}		
	}//end on delete children
	
	/**
	 * Use this to add data defs to the form
	 * @param did
	 */
	public void addDataInstance(DataInstanceDef did)
	{
		//figure out what form to add this data instance to
		List<IFormElement> selectedItems = leftPanel.getSelectedItems();
		//if there's nothing selected then select the form itself
		if(selectedItems.size() == 0)
		{
			selectedItems.add(this.getSelectedForm());
		}
		//get the form of the first object we find
		FormDef form = selectedItems.get(0).getFormDef();
		//add the new data instance to the form
		FormHandler.addDataInstance(form, did);
		//loop over UIs and let them know there was a change
		for(IFormUIListener l : listeningUIComponents)
		{
			//data instances should be children of the form. They should never be the child of a question or anything else
			l.handleAddData(null, did);
		}
	}
	
	/**
	 * Used to add a new element at the root
	 * @param element the thing to add
	 */
	public void loadElement(IFormElement element)
	{
		//get what's selected
		//figure out what form to add this data instance to
		List<IFormElement> selectedItems = leftPanel.getSelectedItems();
		//if there's nothing selected bounce
		if(selectedItems.size() == 0) {return;}
		//get the form of the first object we find
		FormDef form = selectedItems.get(0).getFormDef();
		
		FormHandler.addNewElement(form, element);
		//loop over UIs and let them know there was a change
		for(IFormUIListener l : listeningUIComponents)
		{
			l.handleAddNewElement(form, form, element);
		}
	}


	/**
	 * This little cuttie gets called when you want to move things around.
	 * Most likely this is happending because there's a drag drop operation 
	 * going on, but other things could cause it as well.
	 * @param thingToMove The item to move
	 * @param newParentOfThingToMove The new parent of the thing we're moving
	 * @param newPosition the new position in the parent's kids of the thing we're moving
	 */
	public void moveFormObjects(IFormElement thingToMove, IFormElement newParentOfThingToMove, int newPosition, IFormUIListener ui) 
	{
		if(FormHandler.moveFormObjects(thingToMove, newParentOfThingToMove, newPosition))
		{
			//loop over UIs and let them know there was a change
			for(IFormUIListener l : listeningUIComponents)
			{
				//but it seem these pesky UI elements handle drags and drops deep in the class files and I can't
				//make it happen after the fact, so just don't tell the calling UI to update itself
				if(ui != l)
				{
					l.handleMoveFormObjects(thingToMove, newParentOfThingToMove, newPosition);
				}
			}
		}
		else //put things back where they were
		{
			//loop over UIs and let them know there was a change
			for(IFormUIListener l : listeningUIComponents)
			{
				//but it seem these pesky UI elements handle drags and drops deep in the class files and I can't
				//make it happen after the fact, so just don't tell the calling UI to update itself
				if(ui != l)
				{
					l.handleMoveFormObjects(thingToMove, thingToMove.getParent(), thingToMove.getParent().getIndexOfChild(thingToMove));
				}
			}
		}
		
	}//end moveFormObjects()
	
	/**
	 * This will be used to inset blocks of questions at the location desired
	 * @param xmlStr
	 */
	public void insertBlockQuestions(String xmlStr) throws Exception
	{
		//first get whatever is selected as the insert point
		List<IFormElement> selectedElements = leftPanel.getSelectedItems();
		//make sure there's something there
		if(selectedElements.size() == 0)
		{
			//if there's nothing there then make something.
			FormDef newForm = newForm(false);
			selectedElements.add(newForm);
		}
		//get the first item
		IFormElement selectedItem = selectedElements.get(0);
		//next parse the xml
		FormDef block = FormHandler.parseXml(xmlStr);
		//now add the block into our current form
		List<IFormElement> newElements = FormHandler.insertBlock(block, selectedItem);
		//make sure this is valid
		if(newElements == null || newElements.size() == 0)
		{
			return;
		}
		//now let the UI know that we've added new stuff		
		for(IFormUIListener l : listeningUIComponents)
		{
			l.handlePaste(selectedItem, newElements, null);
		}
		//make sure the toolbar is fully functional
		Toolbar.getInstance().setNewQuestionButtonEnable(true);
	}//end insertBlockQuestions
	
	/**
	 * This'll update a langauge
	 * @param newLocale
	 * @param oldLocale
	 * @param form
	 */
	public void updateLanguage(Locale newLocale, Locale oldLocale, FormDef form)
	{
		FormHandler.updateLanguage(newLocale, oldLocale, form);
		//loop over UIs and let them know there was a change
		for(IFormUIListener l : listeningUIComponents)
		{
			l.handleRefreshText(form);
		}
	}//end updateLanguage
	
	/**
	 * This will load up a new form from XML.
	 * From now on this is the only method that should be called to create new forms
	 * @param xml
	 * @return
	 */
	public FormDef loadNewForm(String xml) throws Exception
	{
		FormDef form = FormHandler.parseXml(xml);
		if(form != null)
		{
			for(IFormUIListener l : listeningUIComponents)
			{
				l.handleLoadNewForm(form);
			}
		}
		//make sure the form has some locales
		if(form.getLocales().size() == 0)
		{
			//if not add a default locale
			form.getLocales().add(new Locale("English", "English"));
		}
		
		Context.setLocales(form.getLocales());
		Context.setLocale(form.getLocales().get(0));
		Context.setDefaultLocale(form.getLocales().get(0));
		
		
		Toolbar.getInstance().setNewQuestionButtonEnable(true);
		
		return form;
	}
	
	
	/** 
	 * Used to create a SPSS label file
	 */
	public void createSPSS()
	{
		//launch an SPSS dialog
		FormDef form = getSelectedForm();
		if(form == null)
		{
			return;
		}
		SPSSDialog s = SPSSDialog.getNewInstance(form);
		s.center();
	}


	/**
	 * Used to set an item as selected
	 */
	public void setItemAsSelected(IFormElement toSelect) 
	{
		for(IFormUIListener l : listeningUIComponents)
		{
			l.setItemAsSelected(toSelect);
		}
		
	}
	
	
}//end class
