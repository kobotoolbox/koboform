package org.openrosa.client.controller;

import java.util.List;

import org.openrosa.client.model.DataDefBase;
import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.IFormElement;
import org.openrosa.client.model.OptionDef;
import org.openrosa.client.model.QuestionDef;

public interface IFormUIListener {

	/**
	 * This notifies the UI that the given form has had a new question of type QuestionType
	 * added to it
	 * @param form Form that has had the new question added to it
	 * @param insertPoint where the new question was added
	 * @param newQuestion the new question itself
	 */
	public void handleAddNewElement(FormDef form, IFormElement insertPoint, IFormElement newElement);
	
	/**
	 * This notifies the UI that it needs to load in a new form
	 * @param form the new form to load in
	 */
	public void handleLoadNewForm(FormDef form);
	
	/**
	 * Use this to notify the UI that a new option has 
	 * been added to the given question
	 * @param question the question that just received a new option
	 * @param option the new option
	 */
	public void handleAddNewOption(QuestionDef question, OptionDef option);
	
	/**
	 * Used to notify the UI that something has been deleted and that the 
	 * UI needs to update accordingly
	 * @param selectedElements
	 */
	public void handleDelete(List<IFormElement> selectedElements);
	
	/**
	 * This is used to notify the UI that the user has copied some elements
	 * @param selectedElements The elements that were selected to be copied
	 */
	public void handleCopy(List<IFormElement> selectedElements);

	/**
	 * This is used to notify the UI that the user has cut some elements
	 * @param selectedElements The elments that were selected to be cut
	 */
	public void handleCut(List<IFormElement> selectedElements);
	
	/**
	 * This is used to let the UI know that things have been pasted
	 * @param selectedElements the newly created things that were pasted
	 */
	public void handlePaste(IFormElement insertPoint, List<IFormElement> selectedElements, List<IFormElement> clipboard);
	
	/**
	 * Used to update the details of a form item. Like if the text changed.
	 * @param formItem
	 * @return
	 */
	public Object handleFormItemChanged(Object formItem);
	
	/**
	 * This method lets the tree know it should add a new data item to the mix
	 * @param selectedItem whatever the user has selected
	 * @param element the data in question
	 */
	public void handleAddData(IFormElement selectedItem, DataDefBase did);
	
	/**
	 * This is used to notify the UI that an element has been moved, most
	 * likely as a reslut of a drag drop operation
	 * @param thingToMove The thing that has been moved
	 * @param newParentOfThingToMove the new parent of the thing that has been moved
	 * @param newPosition the new position as a child of the parent for the thing that has been moved
	 */
	public void handleMoveFormObjects(IFormElement thingToMove, IFormElement newParentOfThingToMove, int newPosition);
	
	/**
	 * Force the UI to update it's text
	 * This'll generally happen when there's a language switch
	 * @param element
	 */
	public void handleRefreshText(IFormElement element);
	
	/**
	 * Sets the specified element as selected in the UI
	 * @param toSelect item to set as selected
	 */
	public void setItemAsSelected(IFormElement toSelect);
	
}
