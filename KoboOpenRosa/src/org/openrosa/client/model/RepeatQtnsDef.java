package org.openrosa.client.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.purc.purcforms.client.xforms.XformConstants;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;


/**
 * Definition for repeat sets of questions. Basically this is just a specialized collection
 * of a set of repeating questions, together with reference to their parent question.
 * 
 * @author daniel
 *
 */
public class RepeatQtnsDef extends GroupDef implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7729924833162245814L;

	/** A list of questions (QuestionDef objects) on a repeat questions row. */
	private Vector<IFormElement> questions;
	
	/** Reference to the parent question. */
	private QuestionDef qtnDef;
	
	/** The maximum number of rows that this repeat questions definition can have. */
	private byte maxRows = -1;
	
	
	/**
	 * Creates a new repeat questions definition object.
	 */
	public RepeatQtnsDef() {
		 
	}
	
	/** Copy Constructor. */
	public RepeatQtnsDef(RepeatQtnsDef repeatQtnsDef) {
		//setQtnDef(new QuestionDef(repeatQtnsDef.getQtnDef()));
		setQtnDef(repeatQtnsDef.getQtnDef());
		copyQuestions(repeatQtnsDef.getQuestions());
	}
	
	public RepeatQtnsDef(QuestionDef qtnDef) {
		setQtnDef(qtnDef);
	}
	
	public RepeatQtnsDef(QuestionDef qtnDef,Vector<IFormElement> questions) {
		this(qtnDef);
		setQuestions(questions);
	}
	
	public QuestionDef getQtnDef() {
		return qtnDef;
	}

	public void setQtnDef(QuestionDef qtnDef) {
		this.qtnDef = qtnDef;
	}
	
	public IFormElement getParent()
	{
		return getQtnDef();
	}

	public Vector<IFormElement> getQuestions() {
		return questions;
	}
	
	public int size(){
		if(questions == null)
			return 0;
		return questions.size();
	}

	public void addQuestion(IFormElement qtn){
		if(questions == null)
			questions = new Vector<IFormElement>();
		
		//qtn.setId((byte)(questions.size()+1)); id should be set somewhere else
		questions.addElement(qtn);
	}
	
	/**
	 * Remove a kid
	 */
	public boolean removeChild(IFormElement element)
	{
		return questions.remove(element);
	}
	
	public void removeQuestion(QuestionDef qtnDef, FormDef formDef){
		
		if(formDef != null)
			formDef.removeQtnFromRules(qtnDef);
		
		questions.removeElement(qtnDef);
	}
	
	public void setQuestions(Vector<IFormElement> questions) {
		this.questions = questions;
	}
	
	public String getText(){
		if(qtnDef != null)
			return qtnDef.getText();
		return null;
	}
	
		
	public QuestionDef getQuestionAt(int index){
		return (QuestionDef)questions.elementAt(index);
	}
	
	public int getQuestionsCount(){
		if(questions == null)
			return 0;
		return questions.size();
	}
	
	private void copyQuestions(Vector<IFormElement> questions){
		if(questions == null)
			return;
		
		this.questions = new Vector<IFormElement>();
		for(int i=0; i<questions.size(); i++)
			this.questions.addElement(new QuestionDef((QuestionDef)questions.elementAt(i),qtnDef));
	}
	
	
	/**
	 * This copies the current repeat question def with the given parent
	 */
	public RepeatQtnsDef copy(QuestionDef parent)
	{
		RepeatQtnsDef retVal = new RepeatQtnsDef(this);
		retVal.setQtnDef(parent);
		return null;
	}
	
		
	/**
	 * Gets a question identified by a variable name.
	 * 
	 * @param varName - the string identifier of the question. 
	 * @return the question reference.
	 */
	public QuestionDef getQuestion(String varName){
		if(varName == null || questions == null)
			return null;
		
		for(int i=0; i<questions.size(); i++){
			QuestionDef def = (QuestionDef)questions.elementAt(i);
			if(def.getBinding().equals(varName))
				return def;
		}
		
		return null;
	}
	
	
	public IFormElement getElement(String varName){
		if(questions == null)
			return null;

		for(int i=0; i<questions.size(); i++){
			IFormElement def = (IFormElement)questions.get(i);
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

	
	public QuestionDef getQuestionWithText(String text){
		if(text == null || questions == null)
			return null;
		
		for(int i=0; i<questions.size(); i++){
			QuestionDef questionDef = (QuestionDef)questions.elementAt(i);
			if(questionDef.getText().equals(text))
				return questionDef;
		}
		return null;
	}
	
	public void setMaxRows(byte maxRows){
		this.maxRows = maxRows;
	}
	
	public byte getMaxRows(){
		return maxRows;
	}
	
	
		
	public void setText(String text){
		qtnDef.setText(text);
	}
	
	public String getBinding(){
		return qtnDef.getBinding();
	}
	
	public void setBinding(String binding){
		qtnDef.setBinding(binding);
	}
	
	public List<IFormElement> getChildren(){
		return questions;
	}
	
	public void setChildren(List<IFormElement> children){
		this.questions = (Vector<IFormElement>)children;
	}
	
	public String toString()
	{
		String retVal = "- Kids: []";
		if(questions != null)
		{
			retVal = " - Kids: " + questions.toString();
		}
		return "Repeat" + retVal;
	}
	
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	

	@Override
	public void writeToXML(Document doc, Element instanceNode, Element bindNode,
			List<Element> iTextNodes, Element UINode) 
	{
		//first lets handle the instance stuff
		instanceNode = writeToXmlInstance(doc, instanceNode);
		//next the iTextNode
		writeToXmliText(doc, iTextNodes);
		//now the UI
		UINode = writeToXmlUi(doc, UINode);
		
		//now that our work is done, lets call the kids
		//what if the kids are null, those are the worst
		if(getChildren() != null)
		{
			for(IFormElement element : getChildren())
			{
				element.writeToXML(doc, instanceNode, bindNode, iTextNodes, UINode);
			}
		}
				
	}
	
	
	/**
	 * Used to write the instance data	
	 * @param doc
	 * @param instanceNode
	 */
	private Element writeToXmlInstance(Document doc, Element instanceNode)
	{
		//create a node for this question
		Element repeatNode = doc.createElement(getBinding());
		repeatNode.setAttribute(XformConstants.ATTRIBUTE_NAME_JR_TEMPLATE, "");
		//add this to the parent node
		instanceNode.appendChild(repeatNode);
		//return the parent
		return repeatNode;
	}
	
	
	/**
	 * This is used to create the user interface for a question
	 * @param doc
	 * @param UINode
	 * @return
	 */
	protected Element writeToXmlUi(Document doc, Element UINode)
	{
	
		UINode = super.writeToXmlUi(doc, UINode);
		//create the group element
		Element repeatUiNode = doc.createElement(XformConstants.NODE_NAME_REPEAT);
		//set the ID for reference purposes
		repeatUiNode.setAttribute(XformConstants.ATTRIBUTE_NAME_NODESET, getPath());
		//add the group node to the UINode
		UINode.appendChild(repeatUiNode);
						
		return repeatUiNode;
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
