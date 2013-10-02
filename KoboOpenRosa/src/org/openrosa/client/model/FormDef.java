package org.openrosa.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.purc.purcforms.client.model.Locale;
import org.purc.purcforms.client.model.ModelConstants;
import org.purc.purcforms.client.xforms.XformConstants;

import com.extjs.gxt.ui.client.store.ListStore;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;

/**
 * Definition of a form. This has some meta data about the form definition and  
 * a collection of pages together with question branching or skipping rules.
 * A form is sent as defined in one language. For instance, those using
 * Swahili would get forms in that language, etc. We don't support runtime
 * changing of a form language in order to have a more efficient implementation
 * as a trade off for more flexibility which may not be used most of the times.
 * 
 * @author Daniel Kayiwa
 *
 */
public class FormDef implements IFormElement, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3157225585072144325L;

	//TODO May not need to serialize this property for smaller pay load. Then we may just rely on the id.
	//afterall it is not even guaranteed to be unique.
	/** The string unique identifier of the form definition. */
	private String variableName = ModelConstants.EMPTY_STRING;

	/** The display name of the form. */
	private String name = ModelConstants.EMPTY_STRING;

	private String formKey = ModelConstants.EMPTY_STRING;

	/** The numeric unique identifier of the form definition. */
	private int id = ModelConstants.NULL_ID;

	/** The collection of rules (SkipRule objects) for this form. */
	private Vector<SkipRule> skipRules;

	/** The collection of rules (ValidationRule objects) for this form. */
	private Vector<ValidationRule> validationRules;

	/** The collection of calculations (Calculation objects) for this form. */
	private Vector<Calculation> calculations;

	/** A string consisting for form fields that describe its data. eg description-template="${/data/question1}$ Market" */
	private String descriptionTemplate =  ModelConstants.EMPTY_STRING;

	/** The xforms document.(for easy syncing between the object model and actual xforms document. */
	private Document doc;

	/** 
	 * The data node of the xform that this form represents.
	 * This is the node immediately under the instace node.
	 */
	private Element dataNode;

	/** The top level node of the xform that this form represents. */
	private Element xformsNode;

	/** The model node of the xform that this form represents. */
	private Element modelNode;
	
	/** The body node. */
	private Element bodyNode;

	/** The layout xml for this form. */
	private String layoutXml;

	/** The javascript source for this form. */
	private String javaScriptSource;

	/** The xforms xml for this form. */
	private String xformXml;

	/** The language xml for this form. */
	private String languageXml;
	
	private int newQuestionId = 1;

	

	/** 
	 * Flag to determine if we can change the form structure.
	 * For a read only form, we can only change the Text and Help Text.
	 * 
	 */
	private boolean readOnly = false;

	private String itextId;


	List<IFormElement> children;
	
	private HashMap<String, DataInstanceDef> dataInstances = null;

	private String questionStartLetter = "A";
	
	
	/** The itextmap*/
	private HashMap<String, ItextModel> iTextMap = new HashMap<String, ItextModel>();
	
	/** itextList*/
	private ListStore<ItextModel> iTextList = new ListStore<ItextModel>();

	/** Locales*/
	private List<Locale> locales = new ArrayList<Locale>();

	/** Constructs a form definition object. */
	public FormDef() {
		dataInstances = new HashMap<String, DataInstanceDef>();
	}

	/**
	 * Creates a new copy of the form from an existing one.
	 * 
	 * @param formDef the form to copy from.
	 */
	public FormDef(FormDef formDef) {
		this(formDef,true);
	}

	/**
	 * Creates a new copy of the form from an existing one, with a flag which
	 * tells whether we should copy the validation rules too.
	 * 
	 * @param formDef the form to copy from.
	 * @param copyValidationRules set to true if you also want to copy the validation rules, else false.
	 */
	public FormDef(FormDef formDef, boolean copyValidationRules) {
		setId(formDef.getId());
		setName(formDef.getName());
		setFormKey(formDef.getFormKey());

		//I just don't think we need this in addition to the id
		setVariableName(formDef.getVariableName());

		setDescriptionTemplate(formDef.getDescriptionTemplate());
		copyChildren(formDef.getChildren());
		copySkipRules(formDef.getSkipRules());
		copyCalculations(formDef.getCalculations());

		//This is a temporary fix for an infinite recursion that happens when validation
		//rule copy constructor tries to set a formdef using the FormDef copy constructor.
		if(copyValidationRules)
			copyValidationRules(formDef.getValidationRules());
		
		dataInstances = new HashMap<String, DataInstanceDef>();
	}

	/**
	 * Constructs a form definition object from these parameters.
	 * 
	 * @param name - the numeric unique identifier of the form definition.
	 * @param name - the display name of the form.
	 * @param variableName - the string unique identifier of the form definition.
	 * @param pages - collection of page definitions.
	 * @param rules - collection of branching rules.
	 */
	public FormDef(int id, String name, String formKey, String variableName,List<IFormElement> children, Vector<SkipRule> skipRules, Vector<ValidationRule> validationRules, String descTemplate, Vector<Calculation> calculations) {
		setId(id);
		setName(name);
		setFormKey(formKey);

		//I just don't think we need this in addition to the id
		setVariableName(variableName);

		setChildren(children);
		setSkipRules(skipRules);
		setValidationRules(validationRules);
		setDescriptionTemplate((descTemplate == null) ? ModelConstants.EMPTY_STRING : descTemplate);
		setCalculations(calculations);
		
		dataInstances = new HashMap<String, DataInstanceDef>();
	}

	public SkipRule getSkipRuleAt(int index) {
		if(skipRules == null)
			return null;
		return (SkipRule)skipRules.elementAt(index);
	}
	
	
	/**
	 * Gets what the starting letter(letters) are
	 * @return
	 */
	public String getQuestionStartLetter() {
		return questionStartLetter;
	}

	/**
	 * Sets what the starting letter is
	 * @param questionStartLetter
	 */
	public void setQuestionStartLetter(String questionStartLetter) {
		this.questionStartLetter = questionStartLetter;
		setNewQuestionId(2); //reset the question IDs
	}
	
	
	/**
	 * Gets a new question ID 
	 * @return
	 */
	public int getNewQuestionId() {
		
		return newQuestionId;
	}
	
	/**
	 * Used to set the new question ID. Do this when we're creating a form from XML
	 * or something like that
	 * @param newQuestionId
	 */
	public void setNewQuestionId(int newQuestionId) {
		
		this.newQuestionId = newQuestionId;
	}
	
	public void incrementNewQuestionId()
	{
		setNewQuestionId(getNewQuestionId()+1);
	}
	
	public void decrementNewQuestionId()
	{
		setNewQuestionId(getNewQuestionId()-1);
	}

	public ValidationRule getValidationRuleAt(int index) {
		if(validationRules == null)
			return null;
		return (ValidationRule)validationRules.elementAt(index);
	}

	public Calculation getCalculationAt(int index) {
		if(calculations == null)
			return null;
		return (Calculation)calculations.elementAt(index);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFormKey() {
		return formKey;
	}

	public void setFormKey(String formKey) {
		this.formKey = formKey;
	}

	//I just don't think we need this in addition to the id
	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getItextId() {
		return itextId;
	}

	public void setItextId(String itextId) {
		this.itextId = itextId;
	}

	public Vector<SkipRule> getSkipRules() {
		return skipRules;
	}

	public void setSkipRules(Vector<SkipRule> skipRules) {
		this.skipRules = skipRules;
	}

	public Vector<ValidationRule> getValidationRules() {
		return validationRules;
	}

	public void setValidationRules(Vector<ValidationRule> validationRules) {
		this.validationRules = validationRules;
	}

	public Vector<Calculation> getCalculations() {
		return calculations;
	}

	public void setCalculations(Vector<Calculation> calculations) {
		this.calculations = calculations;
	}

	public String getDescriptionTemplate() {
		return descriptionTemplate;
	}

	public void setDescriptionTemplate(String descriptionTemplate) {
		this.descriptionTemplate = descriptionTemplate;
	}

	public String getJavaScriptSource() {
		return javaScriptSource;
	}

	public void setJavaScriptSource(String javaScriptSource) {
		this.javaScriptSource = javaScriptSource;
	}

	public String getLayoutXml() {
		return layoutXml;
	}

	public void setLayoutXml(String layout) {
		this.layoutXml = layout;
	}

	public String getLanguageXml() {
		return languageXml;
	}

	public void setLanguageXml(String languageXml) {
		this.languageXml = languageXml;
	}

	public String getXformXml() {
		return xformXml;
	}

	public void setXformXml(String xform) {
		this.xformXml = xform;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * Gets the first skip rule which has a given question as one of its targets.
	 * 
	 * @param questionDef the question.
	 * @return the skip rule.
	 */
	public SkipRule getSkipRule(IFormElement questionDef){
		if(skipRules == null)
			return null;

		for(int i=0; i<skipRules.size(); i++){
			SkipRule rule = (SkipRule)skipRules.elementAt(i);
			IFormElement target = rule.getActionTarget();
			if(target == questionDef)
				return rule;
		}

		return null;
	}

	public Calculation getCalculation(QuestionDef questionDef){
		if(calculations == null)
			return null;

		for(int i=0; i<calculations.size(); i++){
			Calculation calculation = (Calculation)calculations.elementAt(i);
			if(calculation.getQuestion() == questionDef)
				return calculation;
		}

		return null;
	}

	/**
	 * Gets the validation rule for a given question.
	 * 
	 * @param questionDef the question.
	 * @return the validation rule.
	 */
	public ValidationRule getValidationRule(QuestionDef questionDef){
		if(validationRules == null)
			return null;

		for(int i=0; i<validationRules.size(); i++){
			ValidationRule rule = (ValidationRule)validationRules.elementAt(i);
			if(questionDef == rule.getQuestion())
				return rule;
		}

		return null;
	}



	public String toString() {
		String retVal = getName();
		if(children != null)
		{
			retVal += ": " + children.toString();
		}
		return retVal;
	}

	/**
	 * Gets a question identified by a variable name.
	 * 
	 * @param varName - the string identifier of the question. 
	 * @return the question reference.
	 */
	public IFormElement getElement(String varName)
	{
		if(varName == null || children == null)
			return null;

		for(int i=0; i<children.size(); i++)
		{
			IFormElement def = children.get(i);
			if(varName.equals(def.getBinding()))
				return def;
			//recurse into groups
			if(def instanceof GroupDef)
			{
				def = ((GroupDef)def).getElement(varName);
				if(def != null)
					return def;
			}
			//recurse into repeats
			if(def instanceof QuestionDef && ((QuestionDef)def).getDataType() == QuestionDef.QTN_TYPE_REPEAT)
			{
				RepeatQtnsDef rqd  = ((QuestionDef)def).getRepeatQtnsDef();
				if(rqd != null)
				{
					def = rqd.getElement(varName);
					if(def != null)
						return def;
				}
			}
			
		}

		return null;
	}
	
	public IFormElement getChild(String varName){
		return getElement(varName);
	}
	
	

	/**
	 * Adds a new question to the form.
	 * 
	 * @param qtn the new question to add.
	 */
	public void addElement(IFormElement qtn){
		if(children == null)
			children = new ArrayList<IFormElement>();

		children.add(qtn);
		qtn.setParent(this);
		
		// all of this is to make sure that the preload questions that we recently added
		// don't get counted toward incrementing the new question ID since the preload
		// questions don't show up in the UI. It's a bit of a hack, but we have to get things
		// roling for this confrence.
		if(qtn instanceof QuestionDef)
		{
			QuestionDef questionDef = (QuestionDef)qtn;
			if(questionDef.getBinding() == null)
			{
				return;
			}
			String id = questionDef.getBinding();
			if(id != null)
			{
				incrementNewQuestionId();
			}
		}
		else
		{
			incrementNewQuestionId();
		}
	}

	/**
	 * Copies a given list of pages into this form.
	 * 
	 * @param pages the pages to copy.
	 */
	private void copyChildren(List<IFormElement> children){
		if(children != null){
			this.children =  new ArrayList<IFormElement>();
			for(int i=0; i<children.size(); i++) //Should have atleast one page is why we are not checking for nulls.
				this.children.add(children.get(i).copy(this));
		}
	}

	/**
	 * Copies a given list of skip rules into this form.
	 * 
	 * @param rules the skip rules.
	 */
	private void copySkipRules(Vector<SkipRule> rules){
		if(rules != null)
		{
			this.skipRules =  new Vector<SkipRule>();
			for(int i=0; i<rules.size(); i++)
				this.skipRules.addElement(new SkipRule((SkipRule)rules.elementAt(i)));
		}
	}

	/**
	 * Copies a given list of validation rules into this form.
	 * 
	 * @param rules the validation rules.
	 */
	private void copyValidationRules(Vector<ValidationRule> rules){
		if(rules != null)
		{
			this.validationRules =  new Vector<ValidationRule>();
			for(int i=0; i<rules.size(); i++)
				this.validationRules.addElement(new ValidationRule((ValidationRule)rules.elementAt(i)));
		}
	}

	private void copyCalculations(Vector<Calculation> calculations){
		if(calculations != null)
		{
			this.calculations =  new Vector<Calculation>();
			for(int i=0; i<calculations.size(); i++)
				this.calculations.addElement(new Calculation((Calculation)calculations.elementAt(i)));
		}
	}

	/*private void copyDynamicOptions(HashMap<Integer,DynamicOptionDef>){

	}*/

	/**
	 * Removes a page from the form.
	 * 
	 * @param pageFef the page to remove.
	 */
	public boolean removeChild(IFormElement element){
		return children.remove(element);
	}

	/**
	 * Sets the xforms document represented by this form.
	 * @param doc
	 */
	public void setDoc(Document doc){
		this.doc = doc;
	}

	/**
	 * Gets the xforms document represented by this form.
	 * @return
	 */
	public Document getDoc(){
		return doc;
	}

	/**
	 * @return the dataNode
	 */
	public Element getDataNode() {
		return dataNode;
	}

	/**
	 * @param dataNode the dataNode to set
	 */
	public void setDataNode(Element dataNode) {
		this.dataNode = dataNode;
	}

	/**
	 * @return the xformsNode
	 */
	public Element getXformsNode() {
		return xformsNode;
	}

	/**
	 * @param xformsNode the xformsNode to set
	 */
	public void setXformsNode(Element xformsNode) {
		this.xformsNode = xformsNode;
	}

	/**
	 * @return the modelNode
	 */
	public Element getModelNode() {
		return modelNode;
	}

	/**
	 * @param modelNode the modelNode to set
	 */
	public void setModelNode(Element modelNode) {
		this.modelNode = modelNode;
		
		if(modelNode != null){
			String prefix = modelNode.getPrefix();
			if(prefix != null && prefix.trim().length() > 0)
				XformConstants.updatePrefixConstants(prefix);
		}
	}

	public Element getBodyNode() {
		return bodyNode;
	}

	public void setBodyNode(Element bodyNode) {
		this.bodyNode = bodyNode;
	}



	/**
	 * Removes a question from the form.
	 * 
	 * @param qtnDef the question to remove.
	 * @return true if the question has been found and removed, else false.
	 */
	public boolean removeQuestion(IFormElement qtnDef, boolean delete){
		for(int i=0; i<children.size(); i++){
			IFormElement element = children.get(i);
			if(element == qtnDef){
				children.remove(qtnDef);
				return true;
			}
			else if(element instanceof GroupDef){
				if(((GroupDef)element).removeElement(element, this, delete))
					return true;
			}
		}
		return false;
	}

	/**
	 * Removes a question for the validation rules list.
	 * 
	 * @param questionDef the question to remove.
	 */
	private void removeQtnFromValidationRules(IFormElement questionDef){
		for(int index = 0; index < this.getValidationRuleCount(); index++){
			ValidationRule validationRule = getValidationRuleAt(index);
			validationRule.removeQuestion(questionDef);
			if(validationRule.getConditionCount() == 0){
				removeValidationRule(validationRule);
				index++;
			}
		}
	}

	/**
	 * Removes a question from skip rules list.
	 * 
	 * @param questionDef the question to remove.
	 */
	private void removeQtnFromSkipRules(IFormElement questionDef)
	{		
		for(int index = 0; index < getSkipRuleCount(); index++)
		{
			SkipRule skipRule = getSkipRuleAt(index);
			skipRule.removeQuestion(questionDef);
			if(skipRule.getConditionCount() == 0 || skipRule.getActionTarget() == questionDef)
			{
				removeSkipRule(skipRule);
				index++;
			}			
		}
	}

	/**
	 * Removes a question from the validation rules which are referencing it.
	 * 
	 * @param qtnDef the question to remove.
	 */
	public void removeQtnFromRules(IFormElement qtnDef){
		removeQtnFromValidationRules(qtnDef);
		removeQtnFromSkipRules(qtnDef);
	}

	/**
	 * Check if a question is referenced by any dynamic selection list relationship
	 * and if so, removes the relationship.
	 * 
	 * @param questionDef the question to check.
	 */
	public void removeQtnFromDynamicLists(IFormElement questionDef){
		if(!(questionDef instanceof QuestionDef))
			return; //only QuestionDefs can be referenced by DynamicOptionDefs
	}


	public void removeQtnFromCalculations(QuestionDef questionDef)
	{
		for(int index = 0; index < getCalculationCount(); index++)
		{
			Calculation calculation = getCalculationAt(index);
			if(calculation.getQuestion() == questionDef)
			{
				calculations.remove(index);
				return;
			}
		}
	}


	public void updateCalculation(QuestionDef questionDef, String calculateExpression){
		if(calculateExpression == null || calculateExpression.trim().length() == 0)
			removeQtnFromCalculations(questionDef);
		else{
			Calculation calculation = getCalculation(questionDef);
			if(calculation == null)
				addCalculation(new Calculation(questionDef,calculateExpression));
			else
				calculation.setCalculateExpression(calculateExpression);
		}
	}

	/**
	 * Gets the number of skip rules in the form.
	 * 
	 * @return the number of skip rules.
	 */
	public int getSkipRuleCount(){
		if(skipRules == null)
			return 0;
		return skipRules.size();
	}

	public int getCalculationCount(){
		if(calculations == null)
			return 0;
		return calculations.size();
	}

	/**
	 * Gets the number of validation rules in the form.
	 * 
	 * @return the number of validation rules.
	 */
	public int getValidationRuleCount(){
		if(validationRules == null)
			return 0;
		return validationRules.size();
	}

	/**
	 * Gets questions with given display text.
	 * 
	 * @param text the display text to look for.
	 * @return the question of found, else null.
	 */
	public IFormElement getQuestionWithText(String text){
		for(int i=0; i<children.size(); i++){
			IFormElement element = children.get(i);
			if(text.equals(element.getText()))
				return element;
			
			if(element instanceof GroupDef){
				element = ((GroupDef)element).getQuestionWithText(text);
				if(element != null)
					return element;
			}
		}
		return null;
	}
	
	
	
	
	/**
	 * Gets any question that's part of this form by it's binding
	 * 
	 * @param binding the binding to look for.
	 * @return the question of found, else null.
	 */
	public IFormElement getQuestionWithBinding(String binding){
		for(int i=0; i<children.size(); i++){
			IFormElement element = children.get(i);
			if(binding.equals(element.getBinding()))
				return element;
			
			if(element instanceof GroupDef){
				element = ((GroupDef)element).getQuestionWithBinding(binding);
				if(element != null)
					return element;
			}
			else if(element instanceof QuestionDef && element.getDataType() == QuestionDef.QTN_TYPE_REPEAT )
			{
				element = ((QuestionDef)element).getQuestionWithBinding(binding);
				if(element != null)
					return element;
			}
		}
		return null;
	}
	
	
	
	
	
	
	
	


	/**
	 * Checks if the form has a particular skip rule.
	 * 
	 * @param skipRule the skip rule to check.
	 * @return true if the skip rule has been found, else false.
	 */
	public boolean containsSkipRule(SkipRule skipRule){
		if(skipRules == null)
			return false;
		return skipRules.contains(skipRule);
	}


	/**
	 * Checks if a form has a particular validation rule.
	 * 
	 * @param validationRule the validation rule to check.
	 * @return true if the validation rule has been found, else false.
	 */
	public boolean containsValidationRule(ValidationRule validationRule){
		if(validationRules == null)
			return false;
		return validationRules.contains(validationRule);
	}

	/**
	 * Adds a new skip rule to the form.
	 * 
	 * @param skipRule the new skip rule to add.
	 */
	public void addSkipRule(SkipRule skipRule){
		if(skipRules == null)
			skipRules = new Vector<SkipRule>();
		skipRules.addElement(skipRule);
	}

	/**
	 * Adds a new validation rule to the form.
	 * 
	 * @param validationRule the new validation rule to add.
	 */
	public void addValidationRule(ValidationRule validationRule){
		if(validationRules == null)
			validationRules = new Vector<ValidationRule>();
		validationRules.addElement(validationRule);
	}

	public void addCalculation(Calculation calculation){
		if(calculations == null)
			calculations = new Vector<Calculation>();
		calculations.addElement(calculation);
	}


	/**
	 * Removes a skip rule from the form.
	 * 
	 * @param skipRule the skip rule to remove.
	 * @return true if the skip rule has been found and removed, else false.
	 */
	public boolean removeSkipRule(SkipRule skipRule){
		if(skipRules == null)
			return false;

		boolean ret = skipRules.remove(skipRule);
		return ret;
	}

	/**
	 * Removes a validation rule from the form.
	 * 
	 * @param validationRule the validation rule to remove.
	 * @return true if the validation rule has been found and removed.
	 */
	public boolean removeValidationRule(ValidationRule validationRule){
		if(validationRules == null)
			return false;

		boolean ret = validationRules.remove(validationRule);
		
		return ret;
	}

	/**
	 * Updates this formDef (as the main from the refresh source) with the parameter one
	 * 
	 * @param formDef the old formDef to copy from.
	 */
	public void refresh(FormDef formDef){
		this.id = formDef.getId();

		if(variableName.equals(formDef.getVariableName()))
			name = formDef.getName();

		for(int index = 0; index < formDef.getChildren().size(); index++){
			IFormElement element = formDef.getChildren().get(index);
			if(element instanceof GroupDef)
				;//((GroupDef)element).refresh(groupDef);

			//refresh((PageDef)formDef.getPageAt(index));
		}

		//Clear existing skip rules if any. Already existing skip rules will always
		//overwrite those from the refresh source.
		skipRules = new Vector<SkipRule>();
		for(int index = 0; index < formDef.getSkipRuleCount(); index++)
			formDef.getSkipRuleAt(index).refresh(this, formDef);

		//Clear existing validation rules if any. Already existing validation rules 
		//will always overwrite those from the refresh source.
		validationRules = new Vector<ValidationRule>();
		for(int index = 0; index < formDef.getValidationRuleCount(); index++)
			formDef.getValidationRuleAt(index).refresh(this, formDef);

		//add calculations for questions that still exist.
		calculations = new Vector<Calculation>();
		for(int index = 0; index < formDef.getCalculationCount(); index++){
			Calculation calculation = formDef.getCalculationAt(index);
			QuestionDef questionDef = calculation.getQuestion();
			if(questionDef != null)
				addCalculation(new Calculation(questionDef,calculation.getCalculateExpression()));
		}
	}

	public void refresh(IFormElement element){
		//for(int index = 0; index < children.size(); index++){
		//	((PageDef)children.get(index)).refresh(pageDef);
		//}
	}

	public int getChildCount(){
		if(children == null)
			return 0;

		return children.size();
	}

	public IFormElement getChildAt(int index){
		return children.get(index);
	}
	
	/**
	 * Get a data Instance
	 * @param name
	 * @return
	 */
	public DataInstanceDef getDataInstance(String instanceId){
		return dataInstances.get(instanceId);
	}
	
	/**
	 * Get all the data instances
	 * @return
	 */
	public HashMap<String, DataInstanceDef> getDataInstances()
	{
		return dataInstances;
	}
	
	
	/**
	 * Add a new data instance
	 * @param name
	 * @param did
	 */
	public void addDataInstance(String instanceId, DataInstanceDef did)
	{
		if(dataInstances == null)
		{
			dataInstances = new HashMap<String, DataInstanceDef>();
		}
		dataInstances.put(instanceId, did);
	}
	
	
	/**
	 * removes a data instances from the map of data instances.
	 * @param name
	 */
	public void removeDataInstance(String instanceId){
		dataInstances.remove(instanceId);
	}

	/**
	 * Gets the total number of questions contained in the form.
	 * 
	 * @return the number of questions.
	 */
	public int getQuestionCount(){
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
	 * Gets the element at a given position on the first level.
	 * 
	 * @param index the element position.
	 * @return the element object.
	 */
	public IFormElement getElementAt(int index){
		if(children == null)
			return null;

		return  children.get(index);
	}

	public void updateRuleConditionValue(String origValue, String newValue){
		for(int index = 0; index < getSkipRuleCount(); index++)
			getSkipRuleAt(index).updateConditionValue(origValue, newValue);

		for(int index = 0; index < getValidationRuleCount(); index++)
			getValidationRuleAt(index).updateConditionValue(origValue, newValue);
	}

	

	/**
	 * Gets the form to which a particular item (PageDef,QuestionDef,OptionDef) belongs.
	 * 
	 * @param formItem the item.
	 * @return the form.
	 */
	public static FormDef getFormDef(IFormElement formItem){
		if(formItem == null)
			return null;
		
		if(formItem instanceof FormDef)
			return (FormDef)formItem;
		else
			return getFormDef(formItem.getParent());
	}

	/**
	 * Removes all question change event listeners.
	 */
	public void clearChangeListeners(){
		if(children == null)
			return;

		for(int i=0; i<children.size(); i++)
			children.get(i).clearChangeListeners();
	}


	public String getText(){
		return name;
	}

	public void setText(String text){
		setName(text);
	}

	public String getBinding(){
		return variableName;
	}

	public void setBinding(String binding){
		setVariableName(binding);
	}

	public List<IFormElement> getChildren(){
		return children;
	}

	public void setChildren(List<IFormElement> children){
		this.children = children;
	}
	
	public void addChild(IFormElement element){
		//increase our internal counter

		if(children == null)
			children = new ArrayList<IFormElement>();
		children.add(element);
		element.setParent(this);
		
		// all of this is to make sure that the preload questions that we recently added
		// don't get counted toward incrementing the new question ID since the preload
		// questions don't show up in the UI. It's a bit of a hack, but we have to get things
		// roling for this confrence.
		if(element instanceof QuestionDef)
		{
			QuestionDef questionDef = (QuestionDef)element;
			if(questionDef.getBinding() == null)
			{
				incrementNewQuestionId();
			}
			else
			{
				String id = questionDef.getBinding();
				if(id != null)
				{
					incrementNewQuestionId();
				}
			}
		}
		else
		{
			incrementNewQuestionId();
		}
	}

	public IFormElement getParent(){
		return null;
	}

	public void setParent(IFormElement parent){

	}

	public Element getControlNode(){
		return null;
	}

	public void setControlNode(Element controlNode){

	}

	public Element getBindNode(){
		return null;
	}

	public void setBindNode(Element bindNode){

	}

	public int getDataType(){
		return QuestionDef.QTN_TYPE_NULL;
	}

	public void setDataType(int dataType){

	}

	public void updateDataNodes(Element parentDataNode){

	}

	public IFormElement copy(IFormElement parent){
		return null;
	}
	
	public String getDisplayText(){
		return name;
	}
	
	public String getHelpText(){
		return null;
	}
	
	public void setHelpText(String helpText){
		
	}
	
	public Element getLabelNode(){
		return null;
	}
	
	public void setLabelNode(Element labelNode){
		
	}
	
	public Element getHintNode(){
		return null;
	}
	
	public void setHintNode(Element hintNode){
		
	}
	
	public FormDef getFormDef(){
		return this;
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
		return 1;
		
	}

	/**
	 * Used to insert an element at a specified index
	 */
	public void insert(int index, IFormElement element) {
		if(children == null)
			children = new ArrayList<IFormElement>();
		
		if(index > children.size())
		{
			index = children.size();
		}

		element.setParent(this);
		children.add(index, element);
		incrementNewQuestionId();
		
	}
	
	/**
	 * Used to get the index of one of my children
	 */
	public int getIndexOfChild(IFormElement element)
	{
		return children.indexOf(element);
	}
	
	
	/**
	 * get the iText map
	 * @return
	 */
	public HashMap<String, ItextModel>	getITextMap()
	{
		return iTextMap;
	}
	
	/**
	 * set the iText Map
	 * @param iTextMap the iText Map
	 */
	public void setITextMap(HashMap<String, ItextModel> iTextMap)
	{
		this.iTextMap = iTextMap;
	}
	
	/**
	 * get the iText List
	 * @return
	 */
	public ListStore<ItextModel> getITextList()
	{
		return iTextList;
	}
	
	/**
	 * set the iText List
	 * @param iTextList the iText List
	 */
	public void setITextList(ListStore<ItextModel> iTextList)
	{
		this.iTextList = iTextList;
	}
	
	/**
	 * Set locales
	 * @param locales
	 */
	public void setLocales(List<Locale> locales)
	{
		this.locales = locales;
	}
	
	/**
	 * get Locales
	 */
	public List<Locale> getLocales()
	{
		return locales;		
	}
	
	/**
	 * Adds a new locale to the list of locales
	 */
	public void addLocale(Locale locale)
	{
		locales.add(locale);
	}
	
	
	/**
	 * Is the below Locale listed?
	 * @param locale
	 * @return
	 */
	public boolean hasLocale(Locale locale)
	{
		boolean found = false;
		for(Locale l : locales)
		{
			if(l.equals(locale))
			{
				found = true;
				break;
			}
		}
		return found;
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	@Override
	public void writeToXML(Document doc, Element instanceNode, Element bindNode,
			List<Element> iTextNodes, Element UINode) 
	{
		
				
		//So first lets handle the writing of the instance.
		instanceNode = writeToXmlInstance(doc, instanceNode);
		//forms don't have any binds, itexts, or UI, so we'll just iterate over the kids now.
		for(IFormElement element : getChildren())
		{
			element.writeToXML(doc, instanceNode, bindNode, iTextNodes, UINode);
		}
				
		//handle the data instances
		for(String dataId : getDataInstances().keySet())
		{
			DataInstanceDef did = getDataInstances().get(dataId);
			did.writeToXML(doc, instanceNode, bindNode, iTextNodes, UINode);
		}
		
		
	}//end createXml()

	/**
	 * Used to write the instance data	
	 * @param doc
	 * @param instanceNode
	 */
	private Element writeToXmlInstance(Document doc, Element instanceNode)
	{
		//create the root of the instance
		Element instanceRootNode = doc.createElement(getBinding());
		//set the ID, not sure why we do this, but we do
		instanceRootNode.setAttribute(XformConstants.ATTRIBUTE_NAME_ID, getBinding());
		//now attach the instance root to the instance node
		instanceNode.appendChild(instanceRootNode);
		//now return the instance root as the new baseline for all things instance related
		return instanceRootNode;
		
	}//end writeToXmlInstance()

	@Override
	public String getPath() {
		return "/" + getBinding();
	}
}
