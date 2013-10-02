package org.openrosa.client.view;

import org.openrosa.client.Context;
import org.openrosa.client.controller.ICenterPanel;
import org.openrosa.client.controller.IFormChangeListener;
import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.IFormElement;
import org.openrosa.client.postprocess.PostProcessProperties;
import org.openrosa.client.view.FormDesignerWidget.Images;
import org.purc.purcforms.client.PurcConstants;
import org.purc.purcforms.client.controller.IFormActionListener;
import org.purc.purcforms.client.controller.IFormDesignerListener;
import org.purc.purcforms.client.controller.IFormSelectionListener;
import org.purc.purcforms.client.controller.LayoutChangeListener;
import org.purc.purcforms.client.controller.SubmitListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.util.FormDesignerUtil;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.util.LanguageUtil;
import org.purc.purcforms.client.view.PreviewView;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;


/**
 * Panel containing the contents on the form being designed.
 * 
 * @author daniel
 *
 */
@SuppressWarnings("deprecation")
public class CenterPanel extends Composite implements SelectionHandler<Integer>, IFormSelectionListener, SubmitListener, LayoutChangeListener, ICenterPanel{
	/**
	 * TextArea displaying the XForms xml.
	 */
	private TextArea txtXformsSource = new TextArea();
 
	/**
	 * The view displaying form item properties.
	 */
	private PropertiesView propertiesView = new PropertiesView();
		
	/** The text area which contains javascript source. */
	private TextArea txtJavaScriptSource = new TextArea();

	/** The text area which contains layout xml. */
	private TextArea txtLayoutXml = new TextArea();

	/** The text area which contains model xml. */
	private TextArea txtModelXml = new TextArea();

	/** The text area which contains locale or language xml. */
	private TextArea txtLanguageXml = new TextArea();

	/**
	 * View used to display a form as it will look when the user is entering data in non-design mode.
	 */
	private PreviewView previewView;

	/** The form defintion object thats is currently being edited. */
	private FormDef formDef;

	/** Scroll panel for the preview surface. */
	private ScrollPanel scrollPanelPreview = new ScrollPanel();

	/** Listener to form designer global events. */
	private IFormDesignerListener formDesignerListener;

	
	/**
	 * Constructs a new center panel widget.
	 * 
	 * @param images
	 */
	public CenterPanel(Images images) {
		propertiesView.addStyleName("myPropsStyle");

		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setWidget(propertiesView);
		initWidget(scrollPanel);

		Context.setCurrentMode(Context.MODE_QUESTION_PROPERTIES);

		previewEvents();
	}
	
	

	/**
	 * @see com.google.gwt.user.client.DOM#addEventPreview(EventPreview)
	 */
	private void previewEvents(){

		DOM.addEventPreview(new EventPreview() { 
			public boolean onEventPreview(Event event) 
			{ 				
				if (DOM.eventGetType(event) == Event.ONKEYDOWN) {
					byte mode = Context.getCurrentMode();
					if(mode == Context.MODE_PREVIEW)
						return previewView.handleKeyBoardEvent(event);
					/*
					else if(mode == Context.MODE_QUESTION_PROPERTIES || mode == Context.MODE_XFORMS_SOURCE)
						return formDesignerListener.handleKeyBoardEvent(event);
					*/
				}

				return true;
			}
		});
	}


	/**
	 * Sets the listener to form item property change events.
	 * 
	 * @param formChangeListener the listener.
	 */
	public void setFormChangeListener(IFormChangeListener formChangeListener){
		propertiesView.setFormChangeListener(formChangeListener);
	}

	/**
	 * @see com.google.gwt.event.logical.shared.SelectionHandler#onSelection(SelectionEvent)
	 */
	public void onSelection(SelectionEvent<Integer> event){
			Context.setCurrentMode(Context.MODE_QUESTION_PROPERTIES);
	}

	/**
	 * Sets the height of the text area widgets.
	 * 
	 * @param height the height in pixels
	 */
	public void adjustHeight(String height){
		txtXformsSource.setHeight(height);
		txtJavaScriptSource.setHeight(height);
		txtLayoutXml.setHeight(height);
		txtModelXml.setHeight(height);
		txtLanguageXml.setHeight(height);
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormSelectionListener#onFormItemSelected(java.lang.Object)
	 */
	public void onFormItemSelected(Object formItem) {
		propertiesView.onFormItemSelected(formItem);
		propertiesView.setFocus();

		FormDef form = FormDef.getFormDef((IFormElement)formItem);

		if(this.formDef != form){
			setFormDef(form);
		updateScrollPos();
		}
	}

	/**
	 * @see com.google.gwt.user.client.WindowResizeListener#onWindowResized(int, int)
	 */
	public void onWindowResized(int width, int height){
		propertiesView.onWindowResized(width, height);
		updateScrollPos();
	}

	/**
	 * Sets the current scroll height and width of the design and preview surface.
	 */
	private void updateScrollPos(){
		onVerticalResize();

		int height = getOffsetHeight()-48;
		if(height > 0){
			scrollPanelPreview.setHeight(height +PurcConstants.UNITS);
		}
	}

	/**
	 * Called every time this widget is resized.
	 */
	public void onVerticalResize(){
		int d = Window.getClientWidth()-getAbsoluteLeft();
		if(d > 0){
			scrollPanelPreview.setWidth(d-16+PurcConstants.UNITS);
		}
	}



	/**
	 * @see org.purc.purcforms.client.controller.SubmitListener#onSubmit(String)()
	 */
	public void onSubmit(String xml) {
		this.txtModelXml.setText(xml);
		
		/*if(showModelXml)
			tabs.selectTab(SELECTED_INDEX_MODEL_XML);
		else*/
			Window.alert(LocaleText.get("formSubmitSuccess"));
	}

	/**
	 * @see org.purc.purcforms.client.controller.SubmitListener#onCancel()()
	 */
	public void onCancel(){

	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#alignLeft()
	 */
	public void alignLeft() {
		/*if(selectedTabIndex == SELECTED_INDEX_DESIGN_SURFACE)
			designSurfaceView.alignLeft();*/
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#alignRight()
	 */
	public void alignRight() {
		/*if(selectedTabIndex == SELECTED_INDEX_DESIGN_SURFACE)
			designSurfaceView.alignRight();*/
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#alignTop()
	 */
	public void alignTop() {
		/*if(selectedTabIndex == SELECTED_INDEX_DESIGN_SURFACE)
			designSurfaceView.alignTop();*/
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#alignBottom()
	 */
	public void alignBottom() {
		/*if(selectedTabIndex == SELECTED_INDEX_DESIGN_SURFACE)
			designSurfaceView.alignBottom();*/
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#makeSameHeight()
	 */
	public void makeSameHeight() {
		/*if(selectedTabIndex == SELECTED_INDEX_DESIGN_SURFACE)
			designSurfaceView.makeSameHeight();*/
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#makeSameSize()
	 */
	public void makeSameSize() {
		/*if(selectedTabIndex == SELECTED_INDEX_DESIGN_SURFACE)
			designSurfaceView.makeSameSize();*/
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#makeSameWidth()
	 */
	public void makeSameWidth() {
		/*if(selectedTabIndex == SELECTED_INDEX_DESIGN_SURFACE)
			designSurfaceView.makeSameWidth();*/
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormDesignerController#refresh()
	 */
	public void refresh(){
		/*if(selectedTabIndex == SELECTED_INDEX_PREVIEW)
			previewView.refresh(); //loadForm(formDef,designSurfaceView.getLayoutXml(),null);
		else if(selectedTabIndex == SELECTED_INDEX_DESIGN_SURFACE)
			designSurfaceView.refresh();*/
	}

	/**
	 * Sets the current form that is being designed.
	 * 
	 * @param formDef the form definition object.
	 */
	public void setFormDef(FormDef formDef){
		if(this.formDef == null || this.formDef != formDef){
			if(formDef ==  null){
				txtLayoutXml.setText(null);
				txtXformsSource.setText(null);
				txtLanguageXml.setText(null);
				txtJavaScriptSource.setText(null);
			}
			else{
				txtLayoutXml.setText(formDef.getLayoutXml());
				txtXformsSource.setText(formDef.getXformXml());
				txtLanguageXml.setText(formDef.getLanguageXml());
				txtJavaScriptSource.setText(formDef.getJavaScriptSource());
			}
		}

		this.formDef = formDef;
	}

	/**
	 * Gets the current form that is being designed.
	 * 
	 * @return the form definition object.
	 */
	public FormDef getFormDef(){
		return formDef;
	}

	/**
	 * Sets the height offset used by this widget when embedded as a widget
	 * in a GWT application.
	 * 
	 * @param offset the offset pixels.
	 */
	public void setEmbeddedHeightOffset(int offset){
		previewView.setEmbeddedHeightOffset(offset);
	}

	/**
	 * Sets the listener to form action events.
	 * 
	 * @param formActionListener the listener.
	 */
	public void setFormActionListener(IFormActionListener formActionListener){
		this.propertiesView.setFormActionListener(formActionListener);
	}

	/**
	 * @see org.purc.purcforms.client.controller.LayoutChangeListener#onLayoutChanged(String)
	 */
	public void onLayoutChanged(String xml){
		txtLayoutXml.setText(xml);
		if(formDef != null)
			formDef.setLayoutXml(xml);
	}

	/**
	 * Sets listener to form designer global events.
	 * 
	 * @param formDesignerListener the listener.
	 */
	public void setFormDesignerListener(IFormDesignerListener formDesignerListener){
		this.formDesignerListener = formDesignerListener;
	}

	/**
	 * Checks if the current selection mode allows refreshes.
	 * 
	 * @return true if it allows, else false.
	 */
	public boolean allowsRefresh(){
		return false; 
		//selectedTabIndex == SELECTED_INDEX_DESIGN_SURFACE || selectedTabIndex == SELECTED_INDEX_PREVIEW;
	}
	
	public IFormSelectionListener getFormSelectionListener(){
		return propertiesView;
	}



	/**
	 * Called to start post processing stuff
	 */
	public void postProcess(PostProcessProperties postProcessProperties) {
		propertiesView.onFormItemSelected(postProcessProperties);		
	}
	
	/**
	 * Pass in some XML and this will validate it for you
	 * @param xmlStr
	 */
	public void validate(String xmlStr)
	{
		propertiesView.validate(xmlStr);
	}
	
	
	/**
	 * Hides the applets when we want our screen back
	 */
	public void hideApplets()
	{
		propertiesView.hideApplets();
	}
	
	/**
	 * Used to hide the properties. Use this when clearing the form tree.
	 */
	public void hideProperties()
	{
		propertiesView.hideProperties();
	}
	
}
