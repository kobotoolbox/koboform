package org.openrosa.client.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.purc.purcforms.client.xforms.XformConstants;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;


/** 
 * Definition of an answer option or one of the possible answers of a question
 * with a given set of allowed answers..
 * 
 * @author Daniel Kayiwa
 *
 */
public class OptionDef implements IFormElement, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3343434586872362837L;

	
	/** The display text of the answer option. */
	private String text = ModelConstants.EMPTY_STRING;
	
	/** The option value. */
	private String defaultValue = ModelConstants.EMPTY_STRING;
	
	/** The unique text identifier of an answer option. */
	//this is also the same as the iText ID since we don't need both
	private String binding = ModelConstants.EMPTY_STRING;
	
	private String oldBinding = ModelConstants.EMPTY_STRING;
	
	public static final char SEPARATOR_CHAR = ',';
	
	/** The question to which this option belongs. */
	private IFormElement parent;

	
	
	/** Constructs the answer option definition object where
	 * initialization parameters are not supplied. */
	public OptionDef(QuestionDef parent) {  
		this.parent = parent;
	}
	
	/** The copy constructor  */
	public OptionDef(OptionDef optionDef,QuestionDef parent) { 
		 this(parent);
		 setText(optionDef.getText());
		 setBinding(optionDef.getBinding());
		 setItextId(optionDef.getItextId());
		 setDefaultValue(optionDef.getDefaultValue());
		 //setParent(parent /*optionDef.getParent()*/);
	}
	
	/** The copy constructor  */
	public OptionDef(OptionDef optionDef) { 

		 setText(optionDef.getText());
		 setBinding(optionDef.getBinding());
		 setItextId(optionDef.getItextId());
		 setDefaultValue(optionDef.getDefaultValue());
	}

	
	/** Constructs a new option answer definition object from the following parameters.
	 * 
	 * @param id
	 * @param text
	 * @param variableName
	 */
	public OptionDef(String text, String variableName, String val, QuestionDef parent) {
		this(parent);
		setText(text);
		setBinding(variableName);
		setDefaultValue(val);
	}
	

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setDefaultValue(String value) {
		this.defaultValue = value;
	}
	
	public String getBinding() {
		return binding;
	}
	
	public String getOldBinding()
	{
		return oldBinding;
	}
	
	public void setBinding(String variableName) {
		this.oldBinding = this.binding;
		this.binding = variableName;
	}

	public IFormElement getParent() {
		return parent;
	}

	public void setParent(IFormElement parent) {
		this.parent = parent;
	}

	public String getItextId() {
		return binding;
	}

	public void setItextId(String itextId) {
		this.binding = itextId;
	}



	public String toString() {
		return getText();
	}
	
    
    
    public int getDataType(){
    	return QuestionDef.QTN_TYPE_NULL;
    }
    
    public void setDataType(int dataType){
    	
    }
	
    public List<IFormElement> getChildren(){
    	return null;
    }
    
    public void setChildren(List<IFormElement> children){
    	
    }
	
	
	public IFormElement copy(IFormElement parent){
		return new OptionDef(this, (QuestionDef)parent);
	}
	
	public void clearChangeListeners(){
		
	}
	
	public String getDisplayText(){
		return text;
	}
	
	public void addChild(IFormElement element){
		
	}
	
	public String getHelpText(){
		return null;
	}
	
	public void setHelpText(String helpText){
		
	}
	
	public Element getHintNode(){
		return null;
	}
	
	public void setHintNode(Element hintNode){
		
	}
	
	public boolean removeChild(IFormElement element){
		return false;
	}
	
	public int getChildCount(){
		return 0;
	}
	

	public FormDef getFormDef(){
		IFormElement element = getParent();
		if(parent instanceof FormDef)
			return (FormDef)element;
		
		return element.getFormDef();
	}
	
	
	public boolean isLocked(){
		return false;
	}
	
	public boolean isRequired(){
		return false;
	}
	
	public boolean isEnabled(){
		return true;
	}
	
	public boolean isVisible(){
		return true;
	}
	
	public String getDefaultValue(){
		return defaultValue;
	}
	
	
	/**
	 * Used to find out what the index of this element is 
	 */
	public int getIndex() {
		IFormElement element = getParent();
		return element.getIndexOfChild(this);
		
		
	}

	/**
	 * Silly rabbit, options don't have kids
	 */
	public void insert(int index, IFormElement element) 
	{
		
	}
	
	/**
	 * Silly rabbit, options don't have kids
	 */
	public int getIndexOfChild(IFormElement element)
	{
		return -1;
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public String getPath() {
	
		return getParent().getPath() + "/" + getBinding();
	}

	@Override
	public void writeToXML(Document doc, Element instanceNode, Element bindNode,
			List<Element> iTextNodes, Element UINode) 
	{
		//next the iTextNode
		writeToXmliText(doc, iTextNodes);
		//now the UI
		UINode = writeToXmlUi(doc, UINode);
			
	}
	
	
	/**
	 * This is used to create the user interface for a question
	 * @param doc
	 * @param UINode
	 * @return
	 */
	private Element writeToXmlUi(Document doc, Element UINode)
	{
		//create an item element
		Element itemNode = doc.createElement(XformConstants.NODE_NAME_ITEM);
		//now attach the item to the UI node
		UINode.appendChild(itemNode);
		
		//now add the label
		Element labelNode = doc.createElement(XformConstants.NODE_NAME_LABEL_MINUS_PREFIX);
		labelNode.setAttribute(XformConstants.ATTRIBUTE_NAME_REF, "jr:itext('" + getBinding() + "')");
		itemNode.appendChild(labelNode);
		
		//now add the value
		Element valueNode = doc.createElement(XformConstants.NODE_NAME_VALUE);
		valueNode.appendChild(doc.createTextNode(getDefaultValue()));
		itemNode.appendChild(valueNode);
		return UINode;
	}

	/**
	 * This will flesh out the iText
	 * @param doc
	 * @param iTextNode
	 */
	private void writeToXmliText(Document doc, List<Element> iTextNodes)
	{
		//we're assuming that all languages have already been fleshed out in the
		//itext node
		//the mapping of binds to language strings
		HashMap<String, ItextModel> itextMap = getFormDef().getITextMap();		
		//loop the itext children, which should be translation elements
		for(Element translationNode : iTextNodes)			
		{
			//get the language name
			String languageName = translationNode.getAttribute(XformConstants.ATTRIBUTE_NAME_LANG);
			//make sure it's not null
			if(languageName == null)				
			{
				continue;
			}
			//get the ItextModel for this question
			ItextModel itext = itextMap.get(getBinding());
			//if there's nothing there, then this is probably a hidden question
			if(itext == null)
			{
				continue;
			}
			//get the string
			String text = itext.get(languageName);
			//make sure it's not null
			if(text == null)
			{
				continue;
			}
			//add it to the translation node
			//create the text for the form title
			Element titleTextNode = doc.createElement(XformConstants.NODE_NAME_TEXT);
			//set the id
			titleTextNode.setAttribute(XformConstants.ATTRIBUTE_NAME_ID, getBinding());
			//create the value node for the form title
			Element titleValueNode = doc.createElement(XformConstants.NODE_NAME_VALUE);
			//set the text itself
			titleValueNode.appendChild(doc.createTextNode(text));
			//now add the value node to the text node
			titleTextNode.appendChild(titleValueNode);
			//add the text node to the translation node
			translationNode.appendChild(titleTextNode);
			
		}
	}

}
