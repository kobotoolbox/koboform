package org.openrosa.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.openrosa.client.controller.FormDesignerController;
import org.openrosa.client.model.DataDef;
import org.openrosa.client.model.DataDefBase;
import org.openrosa.client.model.DataInstanceDef;
import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.IFormElement;
import org.openrosa.client.model.ItemSetDef;
import org.openrosa.client.model.PredicateDef;
import org.openrosa.client.model.QuestionDef;
import org.purc.purcforms.client.controller.IFormChangeListener;
import org.purc.purcforms.client.locale.LocaleText;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
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
public class ItemSetPropertiesView extends Composite implements IFormChangeListener{

	/** Table used for organizing widgets in a table format. */
	private FlexTable table = new FlexTable();

	/** The itemset in question*/
	private ItemSetDef itemSet;
	
	/** Make sure that the appropriate authorities are notified*/	
	private IFormChangeListener formChangeListener;
	
	/** List of instances**/
	private ListBox instancesDrpDwn = new ListBox();
	
	/** Drop downs that let you pick the attributes you want to use*/
	private ListBox valueAttributeDrpDwn = new ListBox();
	private ListBox labelAttributeDrpDwn = new ListBox();
	
	/** Add path node button*/
	private Button addPathNodeBtn = new Button(LocaleText.get("addPathNode"));
	
	/**Delete path node button*/
	private Button deletePathNodeBtn = new Button(LocaleText.get("deletePathNode"));
	
	private DataInstanceDef did = null;
	private int nextOpenRow = 0;
	private int dynamicContentStartRow = 6;
	
	
	public ItemSetPropertiesView()
	{	
		initializeTable();
		
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.add(table);
		initWidget(verticalPanel);
		setupEventListeners();
	}
	
	
	/**
	 * Creates event listeners for everything
	 */
	private void setupEventListeners()
	{
		instancesDrpDwn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				updateInstance();
			}
		});
		instancesDrpDwn.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updateInstance();
			}
		});
		
		addPathNodeBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addNodePath();
			}
		});
		
		deletePathNodeBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				deleteNodePath();
			}
		});
		
		
		valueAttributeDrpDwn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				updateValueAttribute();
			}
		});
		valueAttributeDrpDwn.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updateValueAttribute();
			}
		});
		
		
		labelAttributeDrpDwn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				updateLabelAttribute();
			}
		});
		labelAttributeDrpDwn.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updateLabelAttribute();
			}
		});
		
		
	}
	
	/**
	 * Used to update the label attribute of this item set
	 */
	private void updateLabelAttribute()
	{
		if(labelAttributeDrpDwn.getItemCount() == 0)
		{
			return;
		}
		itemSet.setLabelAttribute(labelAttributeDrpDwn.getItemText(labelAttributeDrpDwn.getSelectedIndex()));
		formChangeListener.onFormItemChanged(itemSet);
	}
	
	/**
	 * Used to update the value attribute of this item set
	 */
	private void updateValueAttribute()
	{
		if(valueAttributeDrpDwn.getItemCount() == 0)
		{
			return;
		}
		itemSet.setValueAttribute(valueAttributeDrpDwn.getItemText(valueAttributeDrpDwn.getSelectedIndex()));
		formChangeListener.onFormItemChanged(itemSet);
	}
	
	/**
	 * blows away a level of the node path, like forever man, no getting it back...
	 * I hope no one ever asks for an undo feature, can you imagine...
	 */
	private void deleteNodePath()
	{
		itemSet.removeLevel();
		setItemSet(itemSet);
	}
	
	/** Adds a new node path**/
	private void addNodePath()
	{
		if(instancesDrpDwn.getItemCount() == 0)
		{
			Window.alert(LocaleText.get("noInstance"));
			return;
		}
		//is this the base nodepath?
		if(itemSet.getNodeSetLength() == 0)
		{
			//get the did
			String didId = instancesDrpDwn.getItemText(instancesDrpDwn.getSelectedIndex());
			FormDef formDef = itemSet.getFormDef();
			did = formDef.getDataInstance(didId);
			
			//get a list of possible names at the zero level
			ArrayList<String> names = new ArrayList<String>();
			this.getNameList(0, did, names);
			
			//use the first one as your default name, I really want to avoid defaulting things to nothing
			String name = names.get(0);
			
			itemSet.addNodeSet(did);
			itemSet.addNodeSet(name);
			this.setItemSet(itemSet);
			
		}
		else
		{
			//first we need to know what level we're adding to
			int currentLevel = itemSet.getNodeSetDepth();
			
			//get a list of possible names at that level
			ArrayList<String> names = new ArrayList<String>();
			this.getNameList(currentLevel + 1, did, names);
			
			//make sure there's something there
			if(names.size() == 0)
			{
				Window.alert(LocaleText.get("noMoreLevelsForDataInstance"));
				return;
			}

			//use the first one as your default name, I really want to avoid defaulting things to nothing
			String name = names.get(0);

			
			itemSet.addNodeSet(name);
			this.setItemSet(itemSet);
		}
	}
	/**
	 * Updates the instance data
	 */
	private void updateInstance()
	{
		//get the name of the instance we're using
		if(instancesDrpDwn.getItemCount() == 0)
		{
			return;
		}
		String instanceName = instancesDrpDwn.getItemText(instancesDrpDwn.getSelectedIndex());
		//don't change things if they just selected the same instance
		if(did != null && instanceName.equals(did.getInstanceId()))
		{
			return;
		}
		

		//get the new data instance object that we're using
		did = itemSet.getFormDef().getDataInstance(instanceName);	
		
		//create a new node set
		List<Object> nodeSet = new ArrayList<Object>();
		//get a list of possible names at the zero level
		ArrayList<String> names = new ArrayList<String>();
		
		getNameList(0, did, names);
		if(names.size() == 0 )
		{
			Window.alert(LocaleText.get("noDataInInstance"));
			return;
		}


		String name = names.get(0);
		nodeSet.add(did);
		nodeSet.add(name);
		//add the new set of nodes to our itemset
		itemSet.setNodeSet(nodeSet);
		//for the UI to refresh
		setItemSet(itemSet);
		formChangeListener.onFormItemChanged(itemSet);
	}
	
	/**
	 * Creates all the UI elements that are not generated dynamically
	 */
	private void initializeTable()
	{		
		table.setWidget(nextOpenRow, 0, new Label(LocaleText.get("dataInstances")));
		table.setWidget(nextOpenRow, 3, instancesDrpDwn);
		nextOpenRow++;
		table.setWidget(nextOpenRow, 0, new Label(LocaleText.get("valueAttribute")));
		table.setWidget(nextOpenRow, 3, valueAttributeDrpDwn);
		nextOpenRow++;
		table.setWidget(nextOpenRow, 0, new Label(LocaleText.get("labelAttribute")));
		table.setWidget(nextOpenRow, 3, labelAttributeDrpDwn);
		nextOpenRow++;
		
		table.setWidget(nextOpenRow, 3, addPathNodeBtn);
		table.setWidget(nextOpenRow, 4, deletePathNodeBtn);
		nextOpenRow++;
		
		table.setWidget(nextOpenRow, 0, new Label("________"));
		table.setWidget(nextOpenRow, 3, new Label("_________________"));
		table.setWidget(nextOpenRow, 4, new Label("_____________________________________________"));
		nextOpenRow++;
		table.setWidget(nextOpenRow, 0, new Label(LocaleText.get("pathElement")));
		table.setWidget(nextOpenRow, 3, new Label(LocaleText.get("enablePredicate")));
		table.setWidget(nextOpenRow, 4, new Label(LocaleText.get("predicateElement")));
		nextOpenRow++;
	}
	
	
	/**
	 * update the instances drop down
	 */
	private void setupInstancesDropDown()
	{
		if(itemSet == null)
			return;
		
		//get the data instance that should be selected if there is one.
		String selectKey = null;
		if(itemSet.getNodeSetLength() > 0)
		{
			if(itemSet.getNodeSetAtIndex(0) instanceof DataInstanceDef)
			{
				did = (DataInstanceDef)(itemSet.getNodeSetAtIndex(0));
				selectKey = did.getInstanceId();
			}
		}
		
		instancesDrpDwn.clear();
		FormDef formDef = itemSet.getFormDef();
		Set<String> keys = formDef.getDataInstances().keySet();
		for(String key: keys)
		{
			DataInstanceDef tempDid = formDef.getDataInstance(key);
			ArrayList<String> names = new ArrayList<String>();
			this.getNameList(0, tempDid, names);

			if(names.size() > 0) //only allow instances with data
			{
				instancesDrpDwn.addItem(key, key);
				if(key.equals(selectKey))
				{
					instancesDrpDwn.setSelectedIndex(instancesDrpDwn.getItemCount()-1);
				}
			}
		}
		
		//if this item set doesn't have an dataset assigned to it, then do that now
		if(selectKey == null)
		{
			updateInstance();
		}
		
	}
	
	/**
	 * Sets listener
	 * @param formChangeListener
	 */
	public void setFormChangeListener(IFormChangeListener formChangeListener)
	{
		this.formChangeListener = formChangeListener;
	}
	
	/**
	 * Sets the itemset, which really, is the whole reason we're here.
	 * @param itemSet
	 */
	public void setItemSet(ItemSetDef itemSet)
	{
		this.itemSet = itemSet;
		//clear list dropdowns after index 3
		while(table.getRowCount() > dynamicContentStartRow)
		{
			table.removeRow(dynamicContentStartRow);
		}
		nextOpenRow = dynamicContentStartRow;
		setupInstancesDropDown();
		setupValueAndLabels();
		createNodeLists();
	}
	
	private void setupValueAndLabels()
	{

		
		String labelAttribute = itemSet.getLabelAttribute();
		String valueAttribute = itemSet.getValueAttribute();
		
		labelAttributeDrpDwn.clear();
		valueAttributeDrpDwn.clear();
		
		
		
		//get the zero indexed number of nodes, minus predicates
		int level = -1;
		for(Object obj : itemSet.getNodeSet())
		{
			if(obj instanceof String)
			{
				level++;
			}
		}
		
		ArrayList<String> allPossibleAttributes = new ArrayList<String>();
		if(did == null)
		{
			return;
		}
		getAttributes(level, did, allPossibleAttributes, 0);
		
		for(String s : allPossibleAttributes)
		{
			labelAttributeDrpDwn.addItem(s,s);
			valueAttributeDrpDwn.addItem(s,s);
			if(s.equals(labelAttribute))
			{
				labelAttributeDrpDwn.setSelectedIndex(labelAttributeDrpDwn.getItemCount()-1);
			}
			if(s.equals(valueAttribute))
			{
				valueAttributeDrpDwn.setSelectedIndex(valueAttributeDrpDwn.getItemCount()-1);
			}
		}
		
		//make sure there's a default value
		if(allPossibleAttributes.size() > 0)
		{
			updateLabelAttribute();
			updateValueAttribute();
		}
		else
		{
			//figure out the attributes on the base level
			DataDef baseDef = (DataDef)(did.getChildAt(0));
			for(String key : baseDef.getAttributes().keySet())
			{
				this.labelAttributeDrpDwn.addItem(key);
				this.valueAttributeDrpDwn.addItem(key);
			}
			
			updateLabelAttribute();
			updateValueAttribute();
		}
		
	}
	
	/** A recursive function that gets the attributes at a certain level*/
	private void getAttributes(int level, DataDefBase dataDef, ArrayList<String> allPossibleAttributes, int currentLevel)
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
					getAttributes(level-1, (DataDefBase)kid, allPossibleAttributes, currentLevel+1);
				}
			}
		}
	}
	
	
	/**Does the work of setting up dropdowns and such*/
	private void createNodeLists()
	{
		boolean lastWasPredicate = false;
		List<Object> nodeSets = itemSet.getNodeSet();
		int count = -1; //zero indexed
		
		//iterate over all these
		for(Object node : nodeSets)
		{
			if(node instanceof DataInstanceDef)
			{
				
			}			
			else if(node instanceof String)
			{
			
				if(!lastWasPredicate && count > -1)
				{
					//add a blank predicate spot here
					CheckBox cb = new CheckBox();
					cb.setName(count+"");
					cb.setValue(false);
					cb.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							CheckBox cb = (CheckBox)(event.getSource());
							int level = Integer.parseInt(cb.getName());
							boolean checked = cb.getValue();
							handleEnablePredicateClick(level, checked);
						}
					});
					table.setWidget(nextOpenRow, 3, cb);
					nextOpenRow++;
				}
				
				count++;
				
				String selector = (String)node;
				List<String> selectorOptions = new ArrayList<String>(); 
				getNameList(count, did, selectorOptions);
				
				//create drop down list
				ListBox nodeListDropDown = new ListBox();
				//populate the drop down box
				for(String option : selectorOptions)
				{
					nodeListDropDown.addItem(option, option);
					if(option.equals(selector))
					{
						nodeListDropDown.setSelectedIndex(nodeListDropDown.getItemCount()-1);
					}
				}
				nodeListDropDown.setName(count+"");

				nodeListDropDown.addChangeHandler(new ChangeHandler() {
					public void onChange(ChangeEvent event) {
						ListBox lb = (ListBox)(event.getSource());
						int level = Integer.parseInt(lb.getName());
						String selector = lb.getItemText(lb.getSelectedIndex());
						handleSelectorSelection(level, selector);
					}
				});
				
				table.setWidget(nextOpenRow, 0, nodeListDropDown);
				
				lastWasPredicate = false;
			}
			else //it's a predicate
			{
				lastWasPredicate = true;
				PredicateDef pd = (PredicateDef)(node);
				PredicateView pv = new PredicateView(this);
				pv.setPredicate(pd, itemSet, did);
				CheckBox cb = new CheckBox();
				cb.setName(count+"");
				cb.setValue(true);
				cb.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						CheckBox cb = (CheckBox)(event.getSource());
						int level = Integer.parseInt(cb.getName());
						boolean checked = cb.getValue();
						handleEnablePredicateClick(level, checked);
					}
				});
				table.setWidget(nextOpenRow, 3, cb);
				table.setWidget(nextOpenRow, 4, pv);
				nextOpenRow++;
			}
		}
		
		
		if(!lastWasPredicate && count > -1)
		{
			//add a blank predicate spot here
			CheckBox cb = new CheckBox();
			cb.setName(count+"");
			cb.setValue(false);
			cb.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					CheckBox cb = (CheckBox)(event.getSource());
					int level = Integer.parseInt(cb.getName());
					boolean checked = cb.getValue();
					handleEnablePredicateClick(level, checked);
				}
			});
			table.setWidget(nextOpenRow, 3, cb);

		}
	}
	
	/**
	 * Gets a list of unqiue node names that are found at a given zero based level down
	 * (0 is the kids of the datainstance)
	 * @param data
	 * @return
	 */
	private void getNameList(int level, DataInstanceDef dataInstance, List<String> selectorOptions)
	{
		getNameListHelper(level, dataInstance, selectorOptions, 0);
	}
	
	/**
	 * Recursive helper method to the method listed above
	 * @param level
	 * @param dataDef
	 * @return
	 */
	private void getNameListHelper(int level, DataDefBase dataDef, List<String> selectorOptions, int currentLevel)
	{
		if(level == 0) //we've hit the bottom return the current name
		{
			List<IFormElement> kids = dataDef.getChildren();
			for(IFormElement kid : kids)
			{	
				DataDefBase ddb = (DataDefBase)kid;
				if(!selectorOptions.contains(ddb.getName()))
				{
					selectorOptions.add(ddb.getName());
				}
			}
			return ;
		}
		else //recurse
		{
			List<IFormElement> kids = dataDef.getChildren();
			for(IFormElement kid : kids)
			{
				String name = itemSet.getLevelNameAtLevel(currentLevel);
				if(((DataDefBase)kid).getName().equals(name))
				{
					getNameListHelper(level-1, (DataDefBase)kid, selectorOptions, currentLevel+1);
				}
			}
		
		}		
		return;
	}//end method

	@Override
	public Object onFormItemChanged(Object formItem) {
		this.formChangeListener.onFormItemChanged(itemSet);
		return null;
	}

	@Override
	public void onDeleteChildren(Object formItem) {
		this.formChangeListener.onDeleteChildren(formItem);
		
	}
	
	/**
	 * Called when the user turns on or off a predicate
	 * @param level
	 * @param checked
	 */
	public void handleEnablePredicateClick(int level, boolean checked)
	{
		//they are enabled a predicate
		if(checked)
		{			
			//create a predicate
			PredicateDef predicate = new PredicateDef("", null, null, itemSet);
			
			//put the predicate in the itemSet
			itemSet.addPredicateAtLevel(level, predicate);
			
			setItemSet(itemSet);			
		}
		else
		{ //remove the predicate
			
			//put the predicate in the itemSet
			itemSet.removePredicateAtLevel(level);
			
			setItemSet(itemSet);
		}
		
		formChangeListener.onFormItemChanged(itemSet);
	}

	/**
	 * Handles the user selecting a selector at a given level
	 */
	public void handleSelectorSelection(int level, String selector)
	{
		itemSet.changeSelectorAtLevel(level, selector);
		setItemSet(itemSet);
		formChangeListener.onFormItemChanged(itemSet);
	}
		
}
