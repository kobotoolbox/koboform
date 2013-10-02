package org.openrosa.client.view;

import java.util.ArrayList;
import java.util.List;

import org.openrosa.client.OpenRosaConstants;
import org.openrosa.client.controller.FormDesignerController;
import org.openrosa.client.controller.IFileListener;
import org.openrosa.client.library.LibraryHelper;
import org.openrosa.client.model.FormDef;
import org.openrosa.client.util.FileIOUtil;
import org.openrosa.client.util.GracefulShutDown;
import org.openrosa.client.util.UserInfo;
import org.purc.purcforms.client.controller.IFormSelectionListener;
import org.purc.purcforms.client.locale.LocaleText;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.MessageBox.MessageBoxType;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * This is the widget which we display in the design tab.
 * 
 * @author daniel
 *
 */
public class DesignTabWidget extends Composite implements IFormSelectionListener{
	
	/** The main widget for the form designer. */
	private ContentPanel panel;

	/**
	 * Widget used on the right hand side of the form designer. This widget has the
	 * Properties, Xforms Source, Design Surface, LayoutXml, Language Xml
	 * and Model Xml tabs.
	 */
	private CenterPanel centerPanel = new CenterPanel(FormDesignerWidget.images);
	
		/** 
	 * Widget used on the left hand side of the form designer to display a list
	 * of forms and their pages, questions, etc.
	 */
	private LeftPanel leftPanel = new LeftPanel(FormDesignerWidget.images,centerPanel);

	/** The coordinator for execution of commands between the menu and tool bar, left and center panel. */
	private FormDesignerController controller = new FormDesignerController(centerPanel,leftPanel);

	/** List of form item selection listeners. */
	private List<IFormSelectionListener> formSelectionListeners = new ArrayList<IFormSelectionListener>();
	

	
	public DesignTabWidget(IFileListener fileListener){
		leftPanel.showFormAsRoot();

		leftPanel.setFormDesignerListener(controller);
		leftPanel.addFormSelectionListener(this);
		leftPanel.addFormSelectionListener(centerPanel.getFormSelectionListener());
		
		centerPanel.setFormActionListener(leftPanel.getFormActionListener());
		
		initDesigner(fileListener);  
		centerPanel.setFormChangeListener(controller);
		
		controller.setCenterWidget(fileListener);

	}


	private void initDesigner(IFileListener fileListener){
		
		controller.setCenterWidget(fileListener);
		
	    panel = new ContentPanel();  
	    panel.setCollapsible(false);  
	    panel.setFrame(true);  
	    panel.setHeading(LocaleText.get("title_version"));  
	    //BorderLayout layout = new BorderLayout();
	    JohnRowLayout layout = new JohnRowLayout(Orientation.HORIZONTAL);
	    panel.setLayout(layout); 
	    panel.setBorders(false);
	    
		Toolbar toolbar = Toolbar.getToolbar(FormDesignerWidget.images,controller,fileListener,this);
		//Context.addLocaleListChangeListener(toolbar);
		
		UserHeader userHeader = new UserHeader(controller);
		//test if we can do storage
		if(!UserInfo.isStorageSupported())
		{
			MessageBox mb = new MessageBox();
			mb.setMinWidth(600);
			mb.setButtons("");
			mb.setTitle("Storage Not Supported");
			mb.setType(MessageBoxType.ALERT);
			mb.setMessage("<h2>&nbsp;</h2><br/><br/><h2>We are sorry, but it appears that your browser does not support HTML5 Storage. The Form Builder can not work with out it.</h2><br/><br/><h2> Please try again with a different browser.</h2><br/><br/><h2>We recommend <a href=\"http://www.mozilla.com/products/download.html\">FireFox</a></h2>");
			mb.getDialog().setHeight(400);
			mb.show();
			return;
		}
		UserInfo.init(userHeader);
		
		//if a user is logged in then check and then set set their graceful shut down flag
		String currentUser = UserInfo.getCurrentUser();
		if(currentUser != null)
		{
			//did they log out gracefully last time? If not, don't let their last autosave get over written
			if(UserInfo.getGracefulShutDown(currentUser) == GracefulShutDown.notGraceful)
			{
				//This way the program won't know the name of the last autosave, and thus can't overwrite it.
				UserInfo.setLastAutoSaveName(currentUser, null);
			}
			UserInfo.setGracefulShutDown(currentUser, GracefulShutDown.notGraceful);
		}

		
		//this will hold the menu bar and the login info
		ContentPanel topContentPanel = new ContentPanel();
		topContentPanel.setBorders(false);
		topContentPanel.setHeaderVisible(false);
		topContentPanel.setTopComponent(userHeader);
		topContentPanel.setBottomComponent(toolbar.getToolBar());
		topContentPanel.layout();

		panel.setTopComponent(topContentPanel);
		HTML appletHTML = new HTML();
		panel.add(appletHTML);
		FileIOUtil.init(appletHTML);
	    
		//the search options
		SearchPanel searchPanel = new SearchPanel(controller);		
		
		//and go ahead and set the controller for the LibraryHelper
		LibraryHelper.setController(controller);
	
		
	    
	    


	    
	    BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER,300);  
	    centerData.setMargins(new Margins(0,0,10,0));  
	    centerData.setSplit(true); 
	    
		ContentPanel cp = new ContentPanel();	    
		//make sure we can scroll
		cp.setScrollMode(Scroll.AUTO);
		cp.setStyleAttribute("background-color", "white");
	    cp.expand();
	    cp.setHeading(LocaleText.get("selectedQuestion2"));
	    centerPanel.setWidth("100%");
	    centerPanel.setHeight("100%");
	    cp.add(centerPanel);
	    
	    
	    //custom panel ordering
	    panel.add(leftPanel);
	    panel.add(cp);
	    if(OpenRosaConstants.ENABLE_LIBRARY)
		{
	    	panel.add(searchPanel);
		}
	    

	    panel.expand();
		initWidget(panel);
		
		DOM.sinkEvents(getElement(),DOM.getEventsSunk(getElement()) | Event.MOUSEEVENTS);
				
	}
	
	/**
	 * @see com.google.gwt.user.client.WindowResizeListener#onWindowResized(int, int)
	 */
	public void onWindowResized(int width, int height){		
		int shortcutHeight = height - leftPanel.getAbsoluteTop() - 5;//8;
		if (shortcutHeight < 1) 
			shortcutHeight = 1;

		shortcutHeight = height - centerPanel.getAbsoluteTop() - 70;
		if(shortcutHeight > 100){
			//centerPanel.adjustHeight(shortcutHeight + PurcConstants.UNITS);
			//centerPanel.onWindowResized(width, height);
			//panel.setHeight(shortcutHeight+60+PurcConstants.UNITS);
			panel.setHeight(height);
		}
	}
	

	
	public void loadForm(FormDef formDef){
		leftPanel.handleLoadNewForm(formDef);
	}
	

	
	public FormDef addNewForm(){
		return controller.newForm();
	}

	
	/**
	 * Adds a listener to form item selection events.
	 * 
	 * @param formSelectionListener the listener to add.
	 */
	public void addFormSelectionListener(IFormSelectionListener formSelectionListener){
		this.formSelectionListeners.add(formSelectionListener);
	}
	
	public void onFormItemSelected(Object formItem){
		for(int i=0; i<formSelectionListeners.size(); i++)
			formSelectionListeners.get(i).onFormItemSelected(formItem);
	}
	
	
}
