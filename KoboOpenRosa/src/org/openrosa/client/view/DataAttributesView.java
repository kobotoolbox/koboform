package org.openrosa.client.view;

import java.util.HashMap;

import org.openrosa.client.controller.FormDesignerController;
import org.openrosa.client.model.DataDef;
import org.purc.purcforms.client.controller.IFormChangeListener;
import org.purc.purcforms.client.locale.LocaleText;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DataAttributesView extends Composite {

	/** Table used for organizing widgets in a table format. */
	private FlexTable table = new FlexTable();
	
	/** The button to add new data attributes**/
	private Button addNewButton = new Button(LocaleText.get("addNewDataAttributeButton"));
	
	/** The DataDef this view is manipulating**/
	private DataDef dataDef;
	
	private HashMap<String, AttrTextBox> attributeNameTextBoxes= new HashMap<String, AttrTextBox>();
	private HashMap<String, AttrTextBox> attributeValueTextBoxes= new HashMap<String, AttrTextBox>();
	private HashMap<String, AttrButton> attributeButtons= new HashMap<String, AttrButton>();
	
	private IFormChangeListener formChangeListener;
	
	public DataAttributesView()
	{	
		initializeTable();
		setupEventListeners();
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.add(table);
		initWidget(verticalPanel);
	}
	
	public void setFormChangeListener(IFormChangeListener listener)
	{
		this.formChangeListener = listener;
	}
	
	/**
	 * Creates all the UI elements that are not generated dynamically
	 */
	private void initializeTable()
	{
		table.setWidget(0, 0, new Label(LocaleText.get("dataAttributes")));
		table.setWidget(0, 3, addNewButton);
		table.setWidget(1, 0, new Label(LocaleText.get("attributeName")));
		table.setWidget(1, 1, new Label(LocaleText.get("attributeValue")));
	}
	
	/**
	 * Sets the event handlers for the static UI elements
	 */
	private void setupEventListeners() 
	{
		addNewButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dataDef.addAttribute(dataDef.getNextAttributeName(), LocaleText.get("attributeDefaultValue"));
				setDataDef(dataDef);
			}
		});
		
	}
	
	
	/**
	 * Used to set the data def in question
	 * @param dataDef
	 */
	public void setDataDef(DataDef dataDef)
	{
		this.dataDef = dataDef;
		
		//first clear out what's already in the table.
		table.removeAllRows();
		initializeTable();
		
		//now add rows and stuff for each attribute
		int row = table.getRowCount();
		
		//loop over the attributes in the DataDef
		for(String attrName : dataDef.getAttributes().keySet())
		{
			String attrValue = dataDef.getAttributes().get(attrName);
			
			AttrTextBox attrNameTextBox = new AttrTextBox();
			attrNameTextBox.setAttributeName(attrName);
			attrNameTextBox.setText(attrName);
			attributeNameTextBoxes.put(attrName, attrNameTextBox);
			
			AttrTextBox attrValueTextBox = new AttrTextBox();
			attrValueTextBox.setAttributeName(attrName);
			attrValueTextBox.setText(attrValue);
			attributeValueTextBoxes.put(attrName, attrValueTextBox);
			
			AttrButton removeButton = new AttrButton(LocaleText.get("removeAttribute"));
			removeButton.setAttributeName(attrName);
			attributeButtons.put(attrName, removeButton);
			
			//add event handlers			
			attrValueTextBox.addChangeHandler(new ChangeHandler() {
				public void onChange(ChangeEvent event) {
					AttrTextBox textBox = (AttrTextBox)(event.getSource());
					updateValue(textBox.getAttributeName(), textBox.getText());
				}
			});
			attrValueTextBox.addKeyUpHandler(new KeyUpHandler() {
				public void onKeyUp(KeyUpEvent event) {
					AttrTextBox textBox = (AttrTextBox)(event.getSource());
					updateValue(textBox.getAttributeName(), textBox.getText());
				}
			});
			
			attrNameTextBox.addChangeHandler(new ChangeHandler() {
				public void onChange(ChangeEvent event) {
					AttrTextBox textBox = (AttrTextBox)(event.getSource());
					String str = textBox.getText();
					String str2 = PropertiesView.removeShadyCharacters(str);
					if(!str.equals(str2))
					{
						textBox.setText(str2);
					}
					updateName(textBox.getAttributeName(), str2);
				}
			});
			attrNameTextBox.addKeyUpHandler(new KeyUpHandler() {
				public void onKeyUp(KeyUpEvent event) {
					AttrTextBox textBox = (AttrTextBox)(event.getSource());
					String str = textBox.getText();
					String str2 = PropertiesView.removeShadyCharacters(str);
					if(!str.equals(str2))
					{
						textBox.setText(str2);
					}
					updateName(textBox.getAttributeName(), str2);
				}
			});
			
			removeButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					AttrButton button = (AttrButton)(event.getSource());
					removeAttribute(button.getAttributeName());
				}
			});
			
			table.setWidget(row, 0, attrNameTextBox);
			table.setWidget(row, 1, attrValueTextBox);
			table.setWidget(row, 2, removeButton);
			row++;
			
			
		}
	}//end method
	
	/**
	 * Change an attributes value
	 * @param attrName
	 * @param value
	 */
	private void updateValue(String attrName, String value)
	{
		dataDef.addAttribute(attrName, value);
		dataDef = (DataDef)(formChangeListener.onFormItemChanged(dataDef));
		FormDesignerController.makeDirty();
	}
	
	/**
	 * Change an attributes name
	 * @param oldName
	 * @param newName
	 */
	private void updateName(String oldName, String newName)
	{
		//get the button and text boxes for this guy
		AttrTextBox valueBox = attributeValueTextBoxes.get(oldName);
		AttrTextBox nameBox = attributeNameTextBoxes.get(oldName);
		AttrButton button = attributeButtons.get(oldName);
		
		//now remove them from the hash maps
		attributeValueTextBoxes.remove(oldName);
		attributeNameTextBoxes.remove(oldName);
		attributeButtons.remove(oldName);
		
		//now update the names in the elements
		valueBox.setAttributeName(newName);
		nameBox.setAttributeName(newName);
		button.setAttributeName(newName);
		
		//put the elements back in the hashmaps
		attributeValueTextBoxes.put(newName, valueBox);
		attributeNameTextBoxes.put(newName, nameBox);
		attributeButtons.put(newName, button);
		
		//so now that the UI is straight, we can update the attribute itself
		
		String value = dataDef.getAttributeValue(oldName);
		dataDef.removeAttribute(oldName);
		dataDef.addAttribute(newName, value);
		dataDef = (DataDef)(formChangeListener.onFormItemChanged(dataDef));
		FormDesignerController.makeDirty();
	}
	
	/**
	 * Say good bye to that attribute.
	 * @param name
	 */
	private void removeAttribute(String name)
	{
		//update the UI hash maps
		attributeValueTextBoxes.remove(name);
		attributeNameTextBoxes.remove(name);
		attributeButtons.remove(name);
		
		dataDef.removeAttribute(name);
		dataDef = (DataDef)(formChangeListener.onFormItemChanged(dataDef));
		FormDesignerController.makeDirty();
	}
}
