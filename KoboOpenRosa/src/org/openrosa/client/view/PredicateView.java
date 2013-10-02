package org.openrosa.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openrosa.client.model.DataDef;
import org.openrosa.client.model.DataDefBase;
import org.openrosa.client.model.DataInstanceDef;
import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.IFormElement;
import org.openrosa.client.model.ItemSetDef;
import org.openrosa.client.model.PredicateDef;
import org.openrosa.client.model.QuestionDef;
import org.openrosa.client.widget.skiprule.FieldWidget;
import org.purc.purcforms.client.controller.IFormChangeListener;
import org.purc.purcforms.client.controller.ItemSelectionListener;
import org.purc.purcforms.client.locale.LocaleText;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This'll be what the users see when they wana edit an item set
 * @author etherton
 *
 */
public class PredicateView extends Composite implements ItemSelectionListener{

	/** Table used for organizing widgets in a table format. */
	private FlexTable table = new FlexTable();

	/** The Predicate in question*/
	private PredicateDef predicate;
	
	/** The itemset in question*/
	private ItemSetDef itemSet;
	
	/**  The data def instance we're looking at currently*/
	private DataInstanceDef did = null;
	
	/** Make sure that the appropriate authorities are notified*/	
	private IFormChangeListener formChangeListener;
	
	/** Text box that lists questions**/
	private FieldWidget fieldWidget = null;
	
	/** Drop downs that let you pick the attributes you want to use*/
	private ListBox attributeDrpDwn = new ListBox();
	
	/** Drop downs that let you pick if you want to choose from a question or a preselcted value*/
	private ListBox sourceDrpDwn = new ListBox();
	
	/** a text box for typing in values**/
	private TextBox valueTextBox = new TextBox();

	
	private int nextOpenRow = 0;
	
	public PredicateView(IFormChangeListener formChangeListener)
	{	
		this.formChangeListener = formChangeListener;
		initializeTable();
		setupEventListeners();
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.add(table);
		initWidget(verticalPanel);
	}
	
	/**
	 * Event listeners are added at this point
	 */
	private void setupEventListeners()
	{
		attributeDrpDwn.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updateAttribute();
			}
		});
		
		sourceDrpDwn.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updateSource();
			}
		});
		
		valueTextBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updateValue();
			}
		});
		
		valueTextBox.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				updateValue();
			}
		});
	}
	
	/**
	 * Creates all the UI elements that are not generated dynamically
	 */
	private void initializeTable()
	{	
		sourceDrpDwn.addItem(LocaleText.get("question"), "question");
		sourceDrpDwn.addItem(LocaleText.get("value"), "value");
		
		fieldWidget = new FieldWidget(this);		
		fieldWidget.setForDynamicOptions(true);
		table.setWidget(0, 0, new Label(LocaleText.get("attribute")));
		table.setWidget(0, 1, attributeDrpDwn);
		table.setWidget(0, 2, new Label(LocaleText.get("matches")));
		table.setWidget(0, 3, sourceDrpDwn);
		table.setWidget(0, 4, fieldWidget);
		table.setWidget(0, 5, valueTextBox);
		valueTextBox.setVisible(false);
	}
	
	/** Used to set things in motion**/
	public void setPredicate(PredicateDef predicate, ItemSetDef itemSet, DataInstanceDef did)
	{
		this.predicate = predicate;
		this.itemSet = itemSet;
		this.did = did;
		
		//set the question text		
		FormDef formDef = (FormDef)(did.getParent());		
		//are we using questions or values?
		if(predicate.getQuestionDef() != null) //we're using a question
		{
			QuestionDef questionDef = (QuestionDef)(predicate.getQuestionDef());
			fieldWidget.setDynamicQuestionDef((QuestionDef)(itemSet.getParent()));
			fieldWidget.setForDynamicOptions(true);
			fieldWidget.setQuestion(questionDef);
			sourceDrpDwn.setItemSelected(0, true);
		}
		else //we're using a value
		{
			valueTextBox.setText(predicate.getValue());
			sourceDrpDwn.setItemSelected(1, true);
		}
		fieldWidget.setFormDef(formDef);
		updateSource();
		
		
		//get the list of attributes for this level
		ArrayList<String> allPossibleAttributes = new ArrayList<String>();
		//what "level" is this predicate at
		int level = 0;
		for(Object obj : itemSet.getNodeSet())
		{
			if(obj instanceof Object[])
			{
				level++;
			}
			else if(obj == predicate)
			{
				break;
			}
		}
		getAttributes(level, did, allPossibleAttributes, 0, (ItemSetDef)(predicate.getParent()));
		
		for(String s : allPossibleAttributes)
		{
			attributeDrpDwn.addItem(s,s);
			
			if(s.equals(predicate.getAttributeName()))
			{
				attributeDrpDwn.setSelectedIndex(attributeDrpDwn.getItemCount()-1);
			}
		}
		//make sure a value is set for the attribute
		if(allPossibleAttributes.size() > 0)
		{
			updateAttribute();
		}
	}
	
	/** A recursive function that gets the attributes at a certain level*/
	private void getAttributes(int level, DataDefBase dataDef, ArrayList<String> allPossibleAttributes, int currentLevel, ItemSetDef itemSet)
	{

		if(level == 0)
		{
			List<IFormElement> kids = dataDef.getChildren();
			for(IFormElement kid: kids)
			{
				String name = itemSet.getLevelNameAtLevel(currentLevel);
				if(((DataDefBase)kid).getName().equals(name))
				{
					HashMap<String, String> attributes = ((DataDef)kid).getAttributes();
					for(String s : attributes.keySet())
					{
						if(!allPossibleAttributes.contains(s))
						{
							allPossibleAttributes.add(s);
						}
					}
				}
			}
			return;
		}
		else //keep going and recurse
		{
			List<IFormElement> kids = dataDef.getChildren();
			for(IFormElement kid: kids)
			{
				String name = itemSet.getLevelNameAtLevel(currentLevel);
				if(((DataDefBase)kid).getName().equals(name))
				{
					getAttributes(level-1, (DataDefBase)kid, allPossibleAttributes, currentLevel+1, itemSet);
				}
			}
		}
	}

	/**
	 * Override onItemSelected
	 */
	public void onItemSelected(Object sender, Object item) {		
		if(sender == fieldWidget)
		{
			QuestionDef questionDef = (QuestionDef)item;
			predicate.setQuestionDef(questionDef);
			formChangeListener.onFormItemChanged(this.predicate);
		}	
	}

	@Override
	public void onStartItemSelection(Object sender) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * This is used to update the attributes that are selected for the given predicate
	 */
	public void updateAttribute()
	{
		String attribute = attributeDrpDwn.getItemText(attributeDrpDwn.getSelectedIndex());
		predicate.setAttributeName(attribute);
		formChangeListener.onFormItemChanged(predicate);
	}
	
	/**
	 * This is called when a user, or even us on the inside, change what the source is
	 */
	public void updateSource()
	{
		//are we using questions or values?
		String sourceValue = sourceDrpDwn.getValue(sourceDrpDwn.getSelectedIndex());
		if(sourceValue.equals("value"))
		{ //it's the value
			fieldWidget.setVisible(false);
			valueTextBox.setVisible(true);
			updateValue();
		}
		else
		{//it's a question
			fieldWidget.setVisible(true);
			valueTextBox.setVisible(false);
			QuestionDef question = fieldWidget.getQuestion();
			if(question != null)
			{
				predicate.setQuestionDef(question);
				formChangeListener.onFormItemChanged(this.predicate);
			}
			
		}
	}//end update source
	
	/**
	 * This is used to update the value of a predicate
	 */
	public void updateValue()	
	{
		predicate.setValue(valueTextBox.getText());
		formChangeListener.onFormItemChanged(this.predicate);
	}//end update value;

	
}
