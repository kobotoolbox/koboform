package org.openrosa.client.controller;

import java.util.List;

import org.openrosa.client.model.IFormElement;

public interface IFormChangeListener extends org.purc.purcforms.client.controller.IFormChangeListener
{
	public IFormElement addNewQuestion(int dataType);
	
	public void moveFormObjects(IFormElement thingToMove, IFormElement newParentOfThingToMove, int newPosition, IFormUIListener ui);
	
	public List<IFormElement> deleteSelectedItem();
	
	public void setItemAsSelected(IFormElement toSelect);
}
