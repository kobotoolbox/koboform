package org.openrosa.client.model;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.purc.purcforms.client.locale.LocaleText;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;

/**
 * Defines the nodes in a data instance that stores values for dynamic selection questions
 * @author etherton
 *
 */
public class DataDef extends DataDefBase {
	
	
	/** Holds the attributes*/
	private HashMap<String, String> attributes;
	/** The name of the node*/
	private String name;
	
	
	/**
	 * Constructor
	 * @param name
	 */
	public DataDef(String name, IFormElement parent)
	{
		super(parent);
		this.name = name;
		attributes = new HashMap<String, String>();		
	}
	
	public DataDef(DataDef dd, IFormElement parent)
	{
		super(parent);
		name = dd.getName();
		attributes = new HashMap<String, String>();
		//copy the attributes
		for(String attName : dd.getAttributes().keySet())
		{
			String attValue = dd.getAttributes().get(attName);
			attributes.put(attName, attValue);
		}
		
		//copy the kids
		for(IFormElement kid : dd.getChildren())
		{
			if(!(kid instanceof DataDef))
			{
				continue;
			}
			DataDef newKid = ((DataDef)kid).copy(this);
			addChild(newKid);
		}
	}
	
	public DataDef copy(IFormElement parent)
	{
		return new DataDef(this, parent);
	}


	/**
	 * Getter for the name
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the name of the data element
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getTreeViewName()
	{
		if(attributes.containsKey("label"))
		{
			return name + " - " + attributes.get("label");
		}
		return name;
	}
	
	/*******************************Deals with the attributes*************************************/
	
	/**
	 * Get the number of attributes
	 * @return
	 */
	public int getAttributeCount(){
		if(attributes == null)
			return 0;
		return attributes.size();
	}

	/**
	 * Get an attribute
	 * @param key
	 * @return
	 */
	public String getChildAt(String key){
		return attributes.get(key);
	}
	
	/**
	 * Get all the attributes
	 * @return
	 */
	public HashMap<String, String> getAttributes(){
		return attributes;
	}
	
	/**
	 * Add in an attribute
	 * @param element
	 */
	public void addAttribute(String attributeName, String attributeValue){
		attributes.put(attributeName, attributeValue);
	}
	
	/**
	 * Gets rid of an attribute
	 * @param attribute name
	 */
	public void removeAttribute(String AttributeName)
	{
		attributes.remove(AttributeName);
	}
	
	public String getAttributeValue(String attributeName)
	{
		return attributes.get(attributeName);
	}
	
	/**
	 * Get a unique name for the next attribute
	 * @return
	 */
	public String getNextAttributeName()
	{
		//start by getting the count of attributes
		int count = attributes.size();
		while(attributes.containsKey(LocaleText.get("attributeNameNoSpace")+"_"+count))
		{
			count++;
		}
		
		return LocaleText.get("attributeNameNoSpace")+"_"+count;
	}
	
	/**
	 * Generate a custom string representation of this object
	 */
	public String toString()
	{
		String attributeStr = "";
		int i = 0;
		Set<String> keys = attributes.keySet();
		for(String key : keys)
		{
			i++;
			if(i>1)
			{
				attributeStr += " ";
			}
			String value = attributes.get(key);
			attributeStr += key + ":" + value;
		}
		
		return name + " - " + attributeStr;
	}


	/**
	 * Get the relative path for this node
	 */
	public String getPath() {
		
		return name;
	}


	/**
	 * Get the absolute path for this node
	 */
	public String getAbsolutePath() {
		return ((DataDefBase)getParent()).getAbsolutePath() + "/" + name;
	}


	@Override
	public void writeToXML(Document doc, Element instanceNode, Element bindNode,
			List<Element> iTextNodes, Element UINode) 
	{
		
		//first make our little node
		Element dataNode = doc.createElement(getName());
		//make the attributes
		for(String key : getAttributes().keySet())
		{
			//get the value
			String value = getAttributes().get(key);			
			//set the attribute
			dataNode.setAttribute(key, value);
		}
		//add this to the parent
		bindNode.appendChild(dataNode);
		
		//loop over the kids
		for(IFormElement kid : getChildren())
		{
			//this shouldn't happen, but just in case
			if(!(kid instanceof DataDef))
			{
				continue;
			}
			
			((DataDef)kid).writeToXML(doc, instanceNode, dataNode, iTextNodes, UINode);
		}
	}


}
