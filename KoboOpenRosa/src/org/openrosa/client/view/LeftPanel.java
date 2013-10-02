package org.openrosa.client.view;

import java.util.List;

import org.openrosa.client.Context;
import org.openrosa.client.controller.IFormDesignerListener;
import org.openrosa.client.controller.IFormUIListener;
import org.openrosa.client.model.DataDefBase;
import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.IFormElement;
import org.openrosa.client.model.OptionDef;
import org.openrosa.client.model.QuestionDef;
import org.purc.purcforms.client.controller.IFormActionListener;
import org.purc.purcforms.client.controller.IFormSelectionListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.Locale;
import org.purc.purcforms.client.util.FormUtil;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * This widget is on the left hand side of the form designer and contains
 * the Forms, Palette, and Widget Properties panels.
 * 
 * @author daniel
 *
 */
public class LeftPanel extends ContentPanel implements IFormUIListener {

	/**
	 * An image bundle specifying the images for this Widget and aggregating
	 * images needed in child widgets.
	 */
	public interface Images extends FormsTreeView.Images{
		@Source("org/openrosa/client/resources/tasksgroup.gif")
		ImageResource tasksgroup();
		@Source("org/openrosa/client/resources/filtersgroup.gif")
		ImageResource filtersgroup();
	}

	/** The GWT stack panel which serves as the main or root widget. */
	private FlowPanel stackPanel = new FlowPanel();
	
	/** Widgets which displays the list of forms in a tree view. */
	private FormsTreeView formsTreeView;
	

	/**
	 * Constructs a new left panel object.
	 * 
	 * @param images a bundle that provides the images for this widget
	 */
	public LeftPanel(Images images, IFormSelectionListener formSelectionListener) {
		formsTreeView = new FormsTreeView(images,formSelectionListener);

		setHeading(LocaleText.get("activeSurvey"));
		setScrollMode(Scroll.AUTO);
		add(formsTreeView);
		setStyleAttribute("background", "white");
		FormUtil.maximizeWidget(stackPanel);
		
	}

	/**
	 * Sets the listener to form designer global events.
	 * 
	 * @param formDesignerListener the listener.
	 */
	public void setFormDesignerListener(IFormDesignerListener formDesignerListener){
		formsTreeView.setFormDesignerListener(formDesignerListener);
	}

	public void showFormAsRoot(){
		formsTreeView.showFormAsRoot(true);
	}



	private void add(Images images, Widget widget, ImageResource imageProto,String caption) {
//		stackPanel.add(widget, FormDesignerUtil.createHeaderHTML(imageProto, caption), true);
	}

	/**
	 * Loads a given form.
	 * 
	 * @param formDef the form definition object.
	 */
	public void handleLoadNewForm(FormDef formDef){
		formsTreeView.handleLoadNewForm(formDef);
	}

	/*
	public void refresh(FormDef formDef){
		formsTreeView.refreshForm(formDef);
	}
	*/
	
	/**
	 * Gets the list of forms which are loaded.
	 * 
	 * @return the forms list.
	 */
	public List<FormDef> getForms(){
		return formsTreeView.getForms();
	}




	/**
	 * Gets the selected form.
	 * 
	 * @return the form definition object.
	 */
	public FormDef getSelectedForm(){
		return formsTreeView.getSelectedForm();
	}

	/**
	 * Removes all forms.
	 */
	public void clear(){
		formsTreeView.clear();
	}


	/**
	 * Checks if the selected form is valid for saving.
	 * 
	 * @return true if valid, else false.
	 */
	public boolean isValidForm(){
		return formsTreeView.isValidForm();
	}
	
	/**
	 * Gets the listener to form action events.
	 * 
	 * @return the listener.
	 */
	public IFormActionListener getFormActionListener(){
		return formsTreeView;
	}
	
	/**
	 * Sets the default locale.
	 * 
	 * @param locale the localey key.
	 */
	public void setDefaultLocale(Locale locale){
		Context.setDefaultLocale(locale);
	}
	
	/**
	 * Adds a listener to form item selection events.
	 * 
	 * @param formSelectionListener the listener to add.
	 */
	public void addFormSelectionListener(IFormSelectionListener formSelectionListener){
		formsTreeView.addFormSelectionListener(formSelectionListener);
	}
		
	public List<IFormElement> getSelectedItems()
	{
		return formsTreeView.getSelectedItems();
	}

	@Override
	public void handleAddNewElement(FormDef form, IFormElement insertPoint, IFormElement newElement) 
	{
		formsTreeView.handleAddNewElement(form, insertPoint, newElement, false);
	}

	@Override
	public void handleAddNewOption(QuestionDef question, OptionDef option) 
	{
		formsTreeView.handleAddNewOption(question, option);
	}

	@Override
	public void handleDelete(List<IFormElement> selectedElements) 
	{
		formsTreeView.handleDelete(selectedElements);
	}

	@Override
	public void handleCopy(List<IFormElement> selectedElements) 
	{
		formsTreeView.handleCopy(selectedElements);
	}

	@Override
	public void handleCut(List<IFormElement> selectedElements) 
	{
		formsTreeView.handleCut(selectedElements);
	}

	@Override
	public void handlePaste(IFormElement insertPoint, List<IFormElement> selectedElements, List<IFormElement> clipboard) 
	{
		formsTreeView.handlePaste(insertPoint, selectedElements, clipboard);
	}

	@Override
	public Object handleFormItemChanged(Object formItem) 
	{
		return formsTreeView.handleFormItemChanged(formItem);
	}

	@Override
	public void handleAddData(IFormElement selectedItem, DataDefBase did) 
	{
		formsTreeView.handleAddData(selectedItem, did);
	}

	@Override
	public void handleMoveFormObjects(IFormElement thingToMove, IFormElement newParentOfThingToMove, int newPosition) 
	{
		formsTreeView.handleMoveFormObjects(thingToMove, newParentOfThingToMove, newPosition);		
	}

	@Override
	public void handleRefreshText(IFormElement element) {
		formsTreeView.handleRefreshText(element);
		
	}

	@Override
	public void setItemAsSelected(IFormElement toSelect) {
		formsTreeView.setItemAsSelected(toSelect);
		
	}
	
	
}
