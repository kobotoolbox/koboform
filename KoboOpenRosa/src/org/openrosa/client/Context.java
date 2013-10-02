package org.openrosa.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.ItextModel;
import org.purc.purcforms.client.controller.ILocaleListChangeListener;
import org.purc.purcforms.client.model.Locale;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.widget.DesignWidgetWrapper;

import com.extjs.gxt.ui.client.store.ListStore;


/**
 * Contains shared information that has the notion of being current (eg currently
 * selected form, current locale, curent mode (design or preview), and more. 
 * It represents the runtime context of the form designer.
 * Contexts are associated with the current thread.
 * 
 * @author daniel
 *
 */
public class Context {
	/** State of the form designer being in neither preview or design mode. */
	public static final byte MODE_NONE = 0;
	
	/** State when setting questions properties in the properties tab. */
	public static final byte MODE_QUESTION_PROPERTIES = 1;
	
	/** State when the form designer is in design mode. 
	 * As in used dragging around widgets on the design surface.
	 */
	public static final byte MODE_DESIGN = 2;
	
	/** State when the user is previewing their form designs. */
	public static final byte MODE_PREVIEW = 3;
	
	/** State when displaying the xforms source. */
	public static final byte MODE_XFORMS_SOURCE = 4;
	
	/** 	Set the following to true in order to run the file upload and download
	 * 		servlets to run within an embedded jetty servlet engine
	 */
	public static boolean JETTY_SERVLETS_ENABLED = false;
	
	/** The default locale key. */
	private static Locale defaultLocale = new Locale("en","English");
	
	/** The current locale. */
	private static Locale locale = defaultLocale;
	
	/** A list of supported locales. */
	private static List<Locale> locales = new ArrayList<Locale>();
	
	/**Determines if we should allow changing of question bindings.
	 * This is useful for cases where users are not allowed to change the question binding
	 * which affected the names of the xml model.
	 */
	private static boolean allowBindEdit = true;
	
	/** The current mode of the form designer. */
	private static byte currentMode = MODE_NONE;
	
	/** The form having focus. */
	private static FormDef formDef;
	
	/** Flag telling whether widgets and locked and hence allow no movement. */
	private static boolean lockWidgets = false;
	
	/** A list of widgets that have been cut or copied to the clipboard and ready for pasting. */
	public static List<DesignWidgetWrapper> clipBoardWidgets = new Vector<DesignWidgetWrapper>();
	
	private static boolean offlineMode = true;
	
	/** List of those interested in being notified whenever the locale list changes. */
	private static List<ILocaleListChangeListener> localeListeners = new ArrayList<ILocaleListChangeListener>();
	
	private static EventBus eventBus = new EventBus();
	
	/** A mapping for form locale text. The key is the formId while the value is a map of locale 
	 * key and text, where locale key is the value map key and text is the value map value.
	 */
	private static HashMap<Integer,HashMap<String,String>> languageText = new HashMap<Integer,HashMap<String,String>>();

	
	
	/**
	 * Mapping of ItextModel objects to their ids as they are in the id column
	 * of the grid.
	 */
	//private static HashMap<String, ItextModel> itextMap = new HashMap<String, ItextModel>();

	/** Lost of ItextModel objects as they are shown in the grid. */
	private static ListStore<ItextModel> itextList = new ListStore<ItextModel>();
	
	/**
	 * Mapping of itext id's to their form attribute values. eg id=name,
	 * form=short
	 */
	private static HashMap<String, String> formAttrMap = new HashMap<String, String>();
	

	/**
	 * Get the form attribute map
	 * @return form attribute map
	 */
	public static HashMap<String, String> getFormAttrMap()
	{
		return formAttrMap;
	}
	
	/**
	 * Reset the formAttrMap
	 */
	public static void resetFormAttrMap()
	{
		formAttrMap = new HashMap<String, String>();
	}
	
	
	/**
	 * Used to manipulate the iTextMap
	 * @return
	 */
	//public static HashMap<String, ItextModel> getItextMap()
	//{
	//	return itextMap;
	//}
	
	/**
	 * Used to manipulate the iTextList 
	 * @return
	 */
	public static ListStore<ItextModel> getItextList()
	{
		return itextList;
	}
	
	public static void clearItextList()
	{
		itextList = new ListStore<ItextModel>();
	}
	
	//public static void clearItextMap()
	//{
	//	itextMap.clear();
	//}
	
	
	
	/**
	 * The blank form string. includes the start, end, and deviceid nodes
	 */
	private static String blankForm = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	+"<h:html xmlns:h=\"http://www.w3.org/1999/xhtml\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:jr=\"http://openrosa.org/javarosa\" xmlns=\"http://www.w3.org/1999/xhtml\">"
		+"<h:head>"
		+"<h:title ref=\"jr:itext('form')\">Form</h:title>"
	    +"<model xmlns=\"\">"
	    +"<instance id=\"form\">"
	    +"<form id=\"form\">"
	    +"<start/>"
	    +"<end/>"
	    +"<deviceid/>"
	    +"<A01/>"
	    +"</form>"
	    +"</instance>"	    
		+"<bind nodeset=\"/form/start\" type=\"time\" jr:preload=\"timestamp\" jr:preloadParams=\"start\"/>"
		+"<bind nodeset=\"/form/end\" type=\"time\" jr:preload=\"timestamp\" jr:preloadParams=\"end\"/>"
		+"<bind nodeset=\"/form/deviceid\" type=\"string\" jr:preload=\"property\" jr:preloadParams=\"deviceid\"/>"
		+"<bind id=\"A01\" nodeset=\"/form/A01\" type=\"xsd:string\" required=\"true()\"/>"
		+"<itext>"
		+"<translation lang=\"English\">"
		+"<text id=\"form\">"
		+"<value>Form</value>"
		+"</text>"
		+"<text id=\"A01\">"
		+"<value>QuestionA01</value>"
		+"</text>"
		+"<text id=\"A01_1\">"
		+"<value>OptionA01_1</value>"
		+"</text>"
		+"</translation>"
		+"</itext>"
		+"</model>"
		+"</h:head>"
		+"<h:body>"
		+"<select1 xmlns=\"\" bind=\"A01\">"
		+"<label ref=\"jr:itext('A01')\"/>"
	      +"<item id=\"A01_1\">"
	      +"<label ref=\"jr:itext('A01_1')\"/>"
	        +"<value>1</value>"
	        +"</item>"
	        +"</select1>"
	        +"</h:body>"
	        +"</h:html>";

	
	public static String getBlankForm() {
		return blankForm;
	}

	/**
	 * Sets the default locale.
	 * 
	 * @param locale the default locale key.
	 */
	public static void setDefaultLocale(Locale locale){
		Context.defaultLocale = locale;
	}
	
	/**
	 * Gets the default locale.
	 * 
	 * @return the default locale key.
	 */
	public static Locale getDefaultLocale(){
		return defaultLocale;
	}
	
	/**
	 * Sets the current locale.
	 * 
	 * @param locale the locale.
	 */
	public static void setLocale(Locale locale){
		Context.locale = locale;
	}
	
	/**
	 * Gets the current locale.
	 * 
	 * @return the locale.
	 */
	public static Locale getLocale(){
		return locale;
	}
	
	/**
	 * Gets the form that has focus.
	 * 
	 * @return the form definition object.
	 */
	public static FormDef getFormDef() {
		return formDef;
	}

	/**
	 * Sets the form that has focus.
	 * 
	 * @param formDef the form definition object.
	 */
	public static void setFormDef(FormDef formDef) {
		Context.formDef = formDef;
	}

	/**
	 * Checks if the form designer is in text locale or language translation mode.
	 * 
	 * @return true if in localization mode, else false.
	 */
	public static boolean inLocalizationMode(){
		return !defaultLocale.getKey().equalsIgnoreCase(locale.getKey());
	}
	
	/**
	 * Gets the list of supported locales.
	 * 
	 * @return the locale list
	 */
	public static List<Locale> getLocales(){
		return locales;
	}
	
	/**
	 * Adds a new locale to the list of locales
	 */
	public static void addLocale(Locale locale)
	{
		locales.add(locale);
	}
	
	/**
	 * Is the below Locale listed?
	 * @param locale
	 * @return
	 */
	public static boolean hasLocale(Locale locale)
	{
		boolean found = false;
		for(Locale l : locales)
		{
			if(l.equals(locale))
			{
				found = true;
				break;
			}
		}
		return found;
	}
	
	/**
	 * Sets the list of supported locales.
	 * 
	 * @param locales the locale list.
	 */
	public static void setLocales(List<Locale> locales){
		Context.locales = locales;
		
		for(ILocaleListChangeListener listener : localeListeners)
			listener.onLocaleListChanged();
	}
	
	/**
	 * Adds a listener to locale list change event.
	 * 
	 * @param listener the listener.
	 */
	public static void addLocaleListChangeListener(ILocaleListChangeListener listener){
		localeListeners.add(listener);
	}
	
	/**
	 * Check if the current form allows changes for both structure and text.
	 * 
	 * @return true if readonly else false.
	 */
	public static boolean isReadOnly(){
		return (formDef != null && formDef.isReadOnly());
	}
	
	/**
	 * Checks whether the current form structure allows changes.
	 * 
	 * @return true if readonly else false.
	 */
	public static boolean isStructureReadOnly(){
		return false;
		/*
		if((formDef != null && formDef.isReadOnly()) || Context.inLocalizationMode())
			return true;
		return false;
		*/
	}
	
	/**
	 * Checks if we should allow changing of question bindings.
	 * 
	 * @return true if yes, else false.
	 */
	public static boolean allowBindEdit(){
		return allowBindEdit;
	}
	
	/**
	 * Turns off or on question binding.
	 * 
	 * @param allowBindEdit set to true if we should allow editing bindings, else false.
	 */
	public static void setAllowBindEdit(boolean allowBindEdit){
		Context.allowBindEdit = allowBindEdit;
	}
	
	/**
	 * Gets the current form designer mode.
	 * 
	 * @return can be (MODE_DESIGN,MODE_PREVIEW,MODE_NONE)
	 */
	public static byte getCurrentMode(){
		return currentMode;
	}
	
	/**
	 * Sets the current form designer mode.
	 * 
	 * @param currentMode should be (MODE_DESIGN or MODE_PREVIEW,MODE_NONE)
	 */
	public static void setCurrentMode(byte currentMode){
		Context.currentMode = currentMode;
	}
	
	public static boolean getLockWidgets(){
		return lockWidgets;
	}
	
	public static void setLockWidgets(boolean lockWidgets){
		Context.lockWidgets = lockWidgets;
	}
	
	public static boolean isOfflineMode(){
		return offlineMode;
	}
	
	public static void setOfflineModeStatus(){
		try{
			String formId = FormUtil.getFormId();
			if(formId != null && Integer.parseInt(formId) >= 0)
				offlineMode = false;
		}
		catch(Exception ex){}
	}
	
	public static EventBus getEventBus(){
		return eventBus;
	}
	
	public static HashMap<Integer,HashMap<String,String>> getLanguageText(){
		return languageText;
	}
}
