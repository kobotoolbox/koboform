package org.openrosa.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openrosa.client.util.ItextParser;
import org.purc.purcforms.client.model.ModelConstants;
import org.purc.purcforms.client.xforms.XformConstants;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;


/** The definition of a group of questions on a questionaire. 
 * 
 * @author Daniel Kayiwa
 *
 */
public class GroupDef implements IFormElement, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5759169688044921856L;

	/** List of children for this group. */
	private List<IFormElement> children;

	/** The name of the group. */
	private String name = ModelConstants.EMPTY_STRING;

	/** The help text of the group. */
	private String helpText = ModelConstants.EMPTY_STRING;

	/** The xforms label node for this group. */
	private Element labelNode;

	/** The xforms hint node for this group. */
	private Element hintNode;

	/** The xforms group node for this page. */
	private Element groupNode;

	private Element bindNode;

	private Element dataNode;

	/** The parent definition to which this group belongs. */
	private IFormElement parent;

	private String binding;

	private int id;

	private String itextId;

	private int dataType = QuestionDef.QTN_TYPE_GROUP;

	private int newQuestionId = 1;
	
	public GroupDef(){

	}

	/**
	 * Constructs a new page.
	 * 
	 * @param parent the parent element to which the page belongs.
	 */
	public GroupDef(IFormElement parent) {
		this.parent = parent;
	}

	/**
	 * Creates a new copy of a page from an existing one.
	 * 
	 * @param pageDef the page to copy.
	 * @param parent the form to which the page belongs.
	 */
	public GroupDef(GroupDef pageDef,IFormElement parent) {
		this(parent);
		setName(pageDef.getName());		
		setItextId(pageDef.getItextId());
		setBinding(pageDef.getBinding());
		setDataType(pageDef.getDataType());
		setHelpText(pageDef.getHelpText());
		//do a deep copy of the kids
		for(IFormElement kid : pageDef.getChildren())
		{
			IFormElement newkid = kid.copy(this);
			addChild(newkid);
		}

	}

	/**
	 * Constructs a page object with the following parameters.
	 * 
	 * @param name the name of the page.
	 * @param pageNo the number of the page.
	 * @param parent the form to which the page belongs.
	 */
	public GroupDef(String name,IFormElement parent) {
		this(parent);
		setName(name);
		setChildren(children);
	}

	/**
	 * Constructs a page with the following parameters.
	 * 
	 * @param name the name of the page.
	 * @param pageNo the number of the page.
	 * @param questions a list of questions in the page.
	 * @param parent the form to which the page belongs.
	 */
	public GroupDef(String name,List<IFormElement> children, IFormElement parent) {
		this(parent);
		setName(name);
		setChildren(children);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IFormElement getParent() {
		return parent;
	}

	public void setParent(IFormElement parent) {
		this.parent = parent;
	}

	/**
	 * @return the labelNode
	 */
	public Element getLabelNode() {
		return labelNode;
	}

	/**
	 * @param labelNode the labelNode to set
	 */
	public void setLabelNode(Element labelNode) {
		this.labelNode = labelNode;

		if(itextId == null)
			setItextId(ItextParser.getItextId(labelNode));
	}
	
	/**
	 * Returns the next numeric id a question should have relative to this group
	 * @return
	 */
	public int getNewQuestionId()
	{
		return newQuestionId;
	}

	/**
	 * @return the groupNode
	 */
	public Element getGroupNode() {
		return groupNode;
	}

	/**
	 * @param groupNode the groupNode to set
	 */
	public void setGroupNode(Element groupNode) {
		this.groupNode = groupNode;
	}

	/**
	 * Gets the number of questions on this page.
	 * 
	 * @return the number of questions.
	 */
	public int getChildCount(){
		if(children == null)
			return 0;

		int count = 0;
		for(int index = 0; index < children.size(); index++){
			IFormElement element = children.get(index);
			if(element instanceof GroupDef)
				count += ((GroupDef)element).getChildCount();
			else{
				assert(element instanceof QuestionDef);
				count += 1;
			}
		}

		return count;
	}


	/**
	 * Gets the question at a given position on this page.
	 * 
	 * @param index the position.
	 * @return the question.
	 */
	public IFormElement getChildAt(int index){
		if(children == null)
			return null;
		return (IFormElement)children.get(index);
	}


	/**
	 * Adds a question to the page.
	 * 
	 * @param qtn the question to add.
	 */
	public void addChild(IFormElement child){
		newQuestionId++;
		if(children == null)
			children = new ArrayList<IFormElement>();
		children.add(child);
		child.setParent(this);
	}


	/**
	 * Gets a question with a given variable name.
	 * 
	 * @param varName the question variable name.
	 * @return the question.
	 */
	public IFormElement getElement(String varName){
		if(children == null)
			return null;

		for(int i=0; i<children.size(); i++){
			IFormElement def = children.get(i);
			if(varName.equals(def.getBinding()))
				return def;

			//Without this, then we have not validation and skip rules in repeat questions.
			if(def instanceof GroupDef){
				IFormElement elem = ((GroupDef)def).getElement(varName);
				if(elem != null)
					return elem;
			}
			if(def.getDataType() == QuestionDef.QTN_TYPE_REPEAT && ((QuestionDef)def).getRepeatQtnsDef() != null){
				def = ((QuestionDef)def).getRepeatQtnsDef().getElement(varName);
				if(def != null)
					return def;
			}
		}

		return null;
	}

	public QuestionDef getQuestion(String varName){
		return (QuestionDef)getElement(varName);
	}



	public int getElementIndex(String varName){
		if(children == null)
			return -1;

		for(int i=0; i<children.size(); i++){
			IFormElement def = children.get(i);
			if(varName.equals(def.getBinding()))
				return i;
		}

		return -1;
	}



	@Override
	public String toString() {
		return getName();
	}



	/**
	 * Removes a question from this page.
	 * 
	 * @param qtnDef the question to remove.
	 * @param formDef the form to which this page belongs.
	 * @return true if the question was found and removed successfully, else false.
	 */
	public boolean removeElement(IFormElement qtnDef, FormDef formDef, boolean delete){
		return children.remove(qtnDef);
	}

	public static void removeElement2(IFormElement qtnDef, FormDef formDef, boolean delete)
	{
		assert false;
	}


	/**
	 * Removes all questions from this page.
	 * 
	 * @param formDef the form to which the page belongs.
	 */
	public void removeAllElements(FormDef formDef){
		if(children == null)
			return;

		while(children.size() > 0)
			removeElement((QuestionDef)children.get(0),formDef, true);
	}


	/**
	 * Gets the number of questions on this page.
	 * 
	 * @return the number of questions.
	 */
	public int size(){
		if(children == null)
			return 0;
		return children.size();
	}




	


	/**
	 * Checks if this page contains a particular question.
	 * 
	 * @param qtn the question to check.
	 * @return true if it contains, else false.
	 */
	public boolean contains(IFormElement qtn){
		return children.contains(qtn);
	}






	/**
	 * Gets a question with a given text.
	 * 
	 * @param text the text.
	 * @return the question.
	 */
	public IFormElement getQuestionWithText(String text){
		if(children == null)
			return null;

		for(int i=0; i<children.size(); i++){
			IFormElement questionDef = children.get(i);
			if(text.equals(questionDef.getText()))
				return questionDef;

			if(questionDef instanceof GroupDef){
				IFormElement elem = ((GroupDef)questionDef).getQuestionWithText(text);
				if(elem != null)
					return elem;
			}

			/*else if(questionDef.getDataType() == QuestionDef.QTN_TYPE_REPEAT){ //TODO Need to make sure this new addition does not introduce bugs
				questionDef = questionDef.getRepeatQtnsDef().getQuestionWithText(text);
				if(questionDef != null)
					return questionDef;
			}*/
		}
		return null;
	}
	
	
	
	/**
	 * Gets a question with a given binding
	 * 
	 * @param text of the binding we want
	 * @return the question.
	 */
	public IFormElement getQuestionWithBinding(String text){
		if(children == null)
			return null;

		for(int i=0; i<children.size(); i++){
			IFormElement questionDef = children.get(i);
			if(text.equals(questionDef.getBinding()))
				return questionDef;

			if(questionDef instanceof GroupDef){
				IFormElement elem = ((GroupDef)questionDef).getQuestionWithBinding(text);
				if(elem != null)
					return elem;
			}

			else if(questionDef.getDataType() == QuestionDef.QTN_TYPE_REPEAT)
			{ 
				IFormElement elem = ((QuestionDef)questionDef).getQuestionWithBinding(text);
				if(elem != null)
					return elem;
			}
		}
		return null;
	}
	
	

	/**
	 * Removes all question change event listeners.
	 */
	public void clearChangeListeners(){
		if(children == null)
			return;

		for(int i=0; i<children.size(); i++)
			((QuestionDef)children.get(i)).clearChangeListeners();
	}

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public String getText(){
		return name;
	}

	public void setText(String text){
		this.name = text;
	}

	public int getDataType(){
		return dataType;
	}

	public void setDataType(int dataType){
		this.dataType = dataType;
	}

	public String getBinding(){
		return binding;
	}

	public void setBinding(String binding){
		this.binding = binding;
	}

	public List<IFormElement> getChildren(){
		return children;
	}

	public void setChildren(List<IFormElement> children){
		this.children = children;
		if(children != null)
		{
			newQuestionId = children.size();
		}
	}

	public Element getBindNode(){
		return bindNode;
	}

	public void setBindNode(Element bindNode){
		this.bindNode = bindNode;
	}

	public Element getDataNode(){
		return dataNode;
	}

	public void setDataNode(Element dataNode){
		this.dataNode = dataNode;
	}

	public void refresh(IFormElement element){

	}

	public Element getControlNode(){
		return groupNode;
	}

	public void setControlNode(Element controlNode){
		groupNode = controlNode;
	}

	public IFormElement copy(IFormElement parent){
		return new GroupDef(this, parent);
	}

	public String getDisplayText(){
		return name;
	}

	public String getItextId() {
		return itextId;
	}

	public void setItextId(String itextId) {
		this.itextId = itextId;
	}

	public String getHelpText(){
		return helpText;
	}

	public void setHelpText(String helpText){
		this.helpText = helpText;
	}

	public Element getHintNode(){
		return hintNode;
	}

	public void setHintNode(Element hintNode){
		this.hintNode = hintNode;
	}

	public boolean removeChild(IFormElement element){
		if(children == null)
			return false;

		if(children.remove(element))
			return true;

		for(IFormElement child : children){
			if(child.removeChild(element))
				return true;
		}

		return false;
		//return this.removeElement(qtnDef, formDef);
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
		return null;
	}
	
	/**
	 * Used to find out what the index of this element is 
	 */
	public int getIndex() {
		IFormElement element = getParent();
		return element.getIndexOfChild(this);
		
		
	}

	/**
	 * Used to insert an element at a specified index
	 */
	public void insert(int index, IFormElement element) 
	{
		newQuestionId++;
		if(children == null)
			children = new ArrayList<IFormElement>();
		if(index > children.size())
		{
			index = children.size();
		}
		children.add(index, element);
		element.setParent(this);
	}
	
	/**
	 * Used to get the index of one of my children
	 */
	public int getIndexOfChild(IFormElement element)
	{
		return children.indexOf(element);
	}
	
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String getPath() {
	
		return getParent().getPath();
	}	

	@Override
	public void writeToXML(Document doc, Element instanceNode, Element bindNode,
			List<Element> iTextNodes, Element UINode) 
	{
		//next the iTextNode
		writeToXmliText(doc, iTextNodes);
		//now the UI
		UINode = writeToXmlUi(doc, UINode);
		
		//now that our work is done, lets call the kids, if there are kids. What if there aren't any kids?!?! Maybe they'll adopt
		if(getChildren() != null)
		{
			for(IFormElement element : getChildren())
			{
				element.writeToXML(doc, instanceNode, bindNode, iTextNodes, UINode);
			}
		}
				
	}
	
	
	/**
	 * This is used to create the user interface for a question
	 * @param doc
	 * @param UINode
	 * @return
	 */
	protected Element writeToXmlUi(Document doc, Element UINode)
	{
		
		//create the group element
		Element groupUiNode = doc.createElement(XformConstants.NODE_NAME_GROUP);
		//set the ID for reference purposes
		groupUiNode.setAttribute(XformConstants.ATTRIBUTE_NAME_ID, getBinding());
		//add the group node to the UINode
		UINode.appendChild(groupUiNode);
		
		//now add the label
		Element labelNode = doc.createElement(XformConstants.NODE_NAME_LABEL);
		labelNode.setAttribute(XformConstants.ATTRIBUTE_NAME_REF, "jr:itext('" + getBinding() + "')");
		groupUiNode.appendChild(labelNode);
		
		return groupUiNode;
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
