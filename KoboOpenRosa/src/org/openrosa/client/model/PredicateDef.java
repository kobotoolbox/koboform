package org.openrosa.client.model;

import com.google.gwt.xml.client.Element;

/**
 * Used to specify a predicate.
 * Predicates are used to specify a certain set of nodes that have a given value in an attribute
 * @author etherton
 *
 */
public class PredicateDef {
	
	/** Stores the attribute that we want to key off of*/
	private String attributeName = null;
	
	/** Stores the question that we want to match the attribute with*/
	private IFormElement questionDef = null;
	
	/** Stores the value that we want to match the attribute with */
	private String value = null;
	
	private IFormElement parent = null;
		
	public PredicateDef (String attributeName, IFormElement questionDef, String value, IFormElement parent)
	{
		this.attributeName = attributeName;
		this.questionDef = questionDef;
		this.value = value;
		this.parent = parent;
	}
	
	/**
	 * copy constructor
	 * @param pd
	 */
	public PredicateDef (PredicateDef pd)
	{
		this(pd.getAttributeName(), pd.getQuestionDef(), pd.getValue(), pd.getParent());
	}
	
	
	/**
	 * Getter for attribute name
	 * @return
	 */
	public String getAttributeName() {
		return attributeName;
	}


	public IFormElement getParent() {
		return parent;
	}


	public void setParent(IFormElement parent) {
		this.parent = parent;
	}


	/**
	 * Setter for attribute name
	 * @param attributeName
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}


	/**
	 * Getter for quesdtion def
	 * @return
	 */
	public IFormElement getQuestionDef() {
		return questionDef;
	}


	/**
	 * setter for question def
	 * @param questionDef
	 */
	public void setQuestionDef(IFormElement questionDef) {
		this.questionDef = questionDef;
		this.value = null;
	}
	
	/**
	 * setter for the value
	 * @param value
	 */
	public void setValue(String value)
	{
		this.value = value;
		this.questionDef = null;
	}
	
	/**
	 * Getter for the value
	 * @return String
	 */
	public String getValue()
	{
		return value;
	}


	/**
	 * Creates the string that represents all of this
	 */
	public String toString()
	{
		String retVal = "[@" + attributeName + " = ";
		if(questionDef == null && value != null)
		{
			retVal += "'"+value+"'";
		}
		else if(questionDef != null && value == null)
		{			
			//get the absolute binding
			String questionBinding = questionDef.getPath();
			
			retVal += questionBinding;
		}
		else
		{
			retVal += "---NULL---";
		}
		return retVal + "]";		
	}

}
