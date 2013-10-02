package org.openrosa.client.model;

import java.util.ArrayList;
import java.util.List;

import org.purc.purcforms.client.xforms.XformConstants;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;

/**
 * Itemsets are used to specify selections for select questions that are determined at run time.
 * @author etherton
 *
 */
public class ItemSetDef extends OptionDef {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8104696127069979380L;

	/** The attribute that will set the value of things in the item set*/
	String valueAttribute = null;
	
	/** The attribute that will set the label of things in the item set*/
	String labelAttribute = null;
	
	/** Keeps track of where to pull nodes to populate this itemset*/
	List<Object> nodeSet = null;
	
	/** FormDef, you just never know when you'll need one*/
	FormDef formDef = null;
	
	/**
	 * Default constructor
	 */
	public ItemSetDef(QuestionDef questionDef)
	{
		super(questionDef);
		nodeSet = new ArrayList<Object>();
	}
	
	/**
	 * Constructor that lets you set the value attribute and the label attribute.
	 * @param valueAt
	 * @param labelAt
	 */
	public ItemSetDef(String valueAt, String labelAt, FormDef formDef, QuestionDef questionDef)
	{
		super(questionDef);
		this.formDef = formDef;
		this.valueAttribute = valueAt;
		this.labelAttribute = labelAt;
		nodeSet = new ArrayList<Object>();
	}
	
	/**
	 * The classic copy constructor
	 * @param itemSet copies this itemSet into this new item set
	 */
	public ItemSetDef(ItemSetDef itemSet)
	{
		this(itemSet.getValueAttribute(),
				itemSet.getLabelAttribute(),
				itemSet.getFormDef(),
				(QuestionDef)(itemSet.getParent()));
		//deep copy the nodeSet
		this.nodeSet = new ArrayList<Object>();
		for(Object o : itemSet.getNodeSet())
		{
			if(o instanceof String)
			{
				String s = (String)o;
				this.nodeSet.add(new String(s));
			}
			else if(o instanceof PredicateDef)
			{
				PredicateDef pd = new PredicateDef((PredicateDef)o);
				this.nodeSet.add(pd);
			}
			else
			{
				this.nodeSet.add(o);
			}
		}
	}
	
	/**
	 * Makes a copy of the current item set with a new question as the parent
	 * @param question
	 * @return
	 */
	public ItemSetDef copy(QuestionDef question)
	{
		ItemSetDef isd = new ItemSetDef(this);
		isd.setParent(question);
		return isd;
	}

	/**
	 * Set the whole node set
	 * @param nodeSet
	 */
	public void setNodeSet(List<Object> nodeSet)
	{
		this.nodeSet = nodeSet;
	}
	
	/**
	 * Get the whole node set
	 * @return
	 */
	public List<Object> getNodeSet()
	{
		return nodeSet;
	}
	
	/**
	 * Get the number of nodes in there
	 * @return
	 */
	public int getNodeSetLength()
	{
		return nodeSet.size();
	}
	

	/**
	 * Calculates the number of levels in this node set, pretty much just nodeSet.size() minus the predicates
	 * @return
	 */
	public int getNodeSetDepth()
	{
		int count = -1;
		for(Object obj : nodeSet)
		{
			if (obj instanceof String)
			{
				count++;
			}
		}
		
		return count;
	}
	
	/**
	 * gets the name we're using at a given level
	 * @param level
	 */
	public String getNameAtLevel(int level)
	{
		int count = -1;
		for(Object obj : nodeSet)
		{
			if (obj instanceof Object[])
			{
				count++;
				if(count == level)
				{
					Object[] node = (Object[])obj;
					return (String)node[1];
				}
			}
		}
		
		return null;
	}
	
	/**
	 * gets the name we're using at a given level
	 * @param level
	 */
	public String getLevelNameAtLevel(int level)
	{
		int count = -1;
		for(Object obj : nodeSet)
		{
			if (obj instanceof String)
			{
				count++;
				if(count == level)
				{					
					return (String)obj;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Get an item in the nodeset
	 * @param index
	 * @return
	 */
	public Object getNodeSetAtIndex(int index)
	{
		return nodeSet.get(index);
	}
	
	/**
	 * Add an item to the end of the nodeset
	 * @param obj
	 */
	public void addNodeSet(Object obj)
	{
		nodeSet.add(obj);
	}
	
	/**
	 * add an item in the node set at a specified position
	 * @param obj
	 * @param index
	 */
	public void insertIntoNodeSet(Object obj, int index)
	{
		if(nodeSet.size() == 0)
		{
			nodeSet.add(obj);
		}
		else
		{
			nodeSet.add(index, obj);
		}
	}
	
	
	/**
	 * Remove the specified item
	 * @param obj
	 */
	public void removeItemFromNodeSet(Object obj)
	{
		nodeSet.remove(obj);
	}
	
	/**
	 * Remove an item from the specified index
	 * @param index
	 */
	public void removeItemAtIndexFromNodeSet(int index)
	{
		nodeSet.remove(index);
	}
	
	public String toString()
	{
		String nodeSetStr = getNodeSetStr();
		return "Label: " + this.labelAttribute + " Value: " + this.valueAttribute + " " + nodeSetStr;
	}
	
	/**
	 * Returns the nodeset string needed in the XML
	 * @return
	 */
	public String getNodeSetStr()
	{
		String temp = "";
		int count = 0;
		for(int i = 0; i < nodeSet.size(); i++)
		{
			count++;
			Object o = nodeSet.get(i);
			if(o instanceof DataInstanceDef)
			{
				DataInstanceDef did = (DataInstanceDef)o;
				temp += "instance('" + did.instanceId + "')/" + did.rootNodeName;
				continue;
			}
			else if(o instanceof String && count > 1)
			{				
				temp += "/";
			}
			temp += o.toString();
		}
		return temp;
	}
	
	/**
	 * Gets the name, which is the same as toString
	 * @return
	 */
	public String getName()
	{
		return toString();
	}
	
	/**
	 * Returns the data instance that this item set references
	 * @return returns the first node set item that's a DataInstanceDef, otherwise returns null
	 */
	public DataInstanceDef getDataInstance()
	{
		//loop over the things in the node set
		for(Object o : nodeSet)
		{
			if(o instanceof DataInstanceDef)
			{
				return (DataInstanceDef)o;
			}
		}
		
		return null;
	}
	
	public void setDataInstance(DataInstanceDef did)
	{
		int i = 0;
		//loop over the nodeSet and find the did
		for(i = 0; i < nodeSet.size(); i++)
		{
			if(nodeSet.get(i) instanceof DataInstanceDef)
			{
				break;
			}
		}
		
		nodeSet.remove(i);
		nodeSet.set(i, did);
	}

	/**
	 * Getter
	 * @return
	 */
	public String getValueAttribute() {
		return valueAttribute;
	}

	/**
	 * Setter
	 * @param valueAttribute
	 */
	public void setValueAttribute(String valueAttribute) {
		this.valueAttribute = valueAttribute;
	}

	/**
	 * Getter
	 * @return
	 */
	public String getLabelAttribute() {
		return labelAttribute;
	}

	/**
	 * Setter
	 * @param valueAttribute
	 */
	public void setLabelAttribute(String labelAttribute) {
		this.labelAttribute = labelAttribute;
	}
	
	/**
	 * Removes a level from the nodeset
	 */
	public void removeLevel()
	{
		int currentLevel = this.getNodeSetDepth();
		
		int level = -1;
		int count = 0;
		for(Object obj : nodeSet)
		{
			count++;
			if (obj instanceof String)
			{
				level++;
				if(level == currentLevel) // start nuking stuff
				{
					//remove everything past count
					while(nodeSet.size() >= count)
					{
						nodeSet.remove(count-1);
					}
					return;
				}
			}
		}		
	}//end method
	
	/**
	 * Insert a predicate at a given level
	 * @param level
	 * @param predicate
	 */
	public void addPredicateAtLevel(int level, PredicateDef predicate)
	{
		int count = -1;
		for(Object obj : nodeSet)
		{
			count++;
			if (obj instanceof String)
			{
				level--;
				if(level == -1)
				{					
					nodeSet.add(count+1, predicate);
					return;
				}
			}
		}
	}
	
	/**
	 * If there's a preidcate at the given level, this nukes it, like gone man.
	 * @param level
	 */
	public void removePredicateAtLevel(int level)
	{		
		int count = -1;
		for(Object obj : nodeSet)
		{
			count++;
			if (obj instanceof String)
			{
				level--;
				if(level == -1)
				{					
					//now the next node items should be predicates, if they're not, bounce.
					count++;
					while(nodeSet.size() > (count))
					{
						Object o = nodeSet.get(count);
						if(o instanceof PredicateDef)
						{
							nodeSet.remove(o);
							return;
						}
						else
						{
							return;
						}
					}
				}
			}
		}
	}
	
	/**
	 * changes the selector at a given level
	 * @param level
	 * @param selector
	 */
	public void changeSelectorAtLevel(int level, String selector)
	{
		int count = -1;
		for(Object obj : nodeSet)
		{
			count++;
			if (obj instanceof String)
			{
				level--;
				if(level == -1)
				{				
					nodeSet.set(count, selector);
					return;
				}
			}
		}
	}
	
	/**
	 * Over ride the default method
	 */
	public String getText()
	{
		return toString();
	}
	
	
	
	@Override
	public void writeToXML(Document doc, Element instanceNode, Element bindNode,
			List<Element> iTextNodes, Element UINode) 
	{
		//write the UI
		writeToXmlUi(doc, UINode);
			
	}
	
	/**
	 * Writes the User Interface XML for an item set
	 * @param doc the main document
	 * @param UINode the node that this should be attached to
	 */
	private void writeToXmlUi(Document doc, Element UINode)
	{
		//write out the itemsetNode
		Element itemSetNode = doc.createElement(XformConstants.NODE_NAME_ITEMSET);
		//set the node set attribute
		itemSetNode.setAttribute(XformConstants.ATTRIBUTE_NAME_NODESET, getNodeSetStr());
		//set label node
		Element labelNode = doc.createElement(XformConstants.NODE_NAME_LABEL);
		//set the ref
		labelNode.setAttribute(XformConstants.ATTRIBUTE_NAME_REF, "@"+getLabelAttribute());
		//add to the item set node
		itemSetNode.appendChild(labelNode);
		//create the value node
		Element valueNode = doc.createElement(XformConstants.NODE_NAME_VALUE);
		//set the ref
		valueNode.setAttribute(XformConstants.ATTRIBUTE_NAME_REF, "@"+getValueAttribute());
		//add to the item set node
		itemSetNode.appendChild(valueNode);
		
		//add the itemset to the UINode
		UINode.appendChild(itemSetNode);
	}
	
	
}
