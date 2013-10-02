package org.openrosa.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.openrosa.client.controller.QuestionChangeListener;
import org.openrosa.client.xforms.ConstraintBuilder;
import org.openrosa.client.xforms.RelevantBuilder;
import org.openrosa.client.xforms.XformConstants;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.ModelConstants;
import org.purc.purcforms.client.xforms.XformBuilderUtil;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;


/** 
 * This is the question definition.
 * 
 * @author Daniel Kayiwa
 *
 */
public class QuestionDef implements IFormElement, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3118016106422360452L;

	/** The value to save for boolean questions when one selects the yes option. */
	public static final String TRUE_VALUE = "true";

	/** The value to save for the boolean questions when one selects the no option. */
	public static final String FALSE_VALUE = "false";

	/** The text to display for boolean questions for the yes option. */
	public static final String TRUE_DISPLAY_VALUE = LocaleText.get("yes");

	/** The text to display for boolean questions for the no option. */
	public static final String FALSE_DISPLAY_VALUE = LocaleText.get("no");

	/** The prompt text. The text the user sees. */
	private String text = ModelConstants.EMPTY_STRING;

	/** The help text. */
	private String helpText = ModelConstants.EMPTY_STRING;

	/** The type of question. eg Numeric,Date,Text etc. */
	private int dataType = QTN_TYPE_TEXT;

	/** The value supplied as answer if the user has not supplied one. */
	private String defaultValue;

	/** The question answer of value. */
	private String answer;
	
	/** The preload string, if any */
	private String preload;
	
	/** The preload param string, if any */
	private String preloadParam;

	//TODO For a smaller payload, may need to combine (mandatory,visible,enabled,locked) 
	//into bit fields forming one byte. This would be a saving of 3 bytes per question.
	/** A flag to tell whether the question is to be answered or is optional. */
	private boolean required = true;

	/** A flag to tell whether the question should be shown or not. */
	private boolean visible = true;

	/** A flag to tell whether the question should be enabled or disabled. */
	private boolean enabled = true;

	/** A flag to tell whether a question is to be locked or not. A locked question 
	 * is one which is visible, enabled, but cannot be edited.
	 */
	private boolean locked = false;
	
	private Element bindNode = null;

	//TODO We have a bug here when more than one question, on a form, have the 
	//same variable names.
	//TODO May not need to serialize this property for smaller pay load. Then we would just rely on the id.
	/** The text indentifier of the question. This is used by the users of the questionaire 
	 * but in code we use the dynamically generated numeric id for speed. 
	 */
	private String variableName = ModelConstants.EMPTY_STRING;
	
	private String oldVariableName = ModelConstants.EMPTY_STRING;

	/** The allowed set of values (OptionDef) for an answer of the question. 
	 * This also holds repeat sets of questions (RepeatQtnsDef) for the QTN_TYPE_REPEAT.
	 * This is an optimization aspect to prevent storing these guys differently as 
	 * they can't both happen at the same time. The internal storage implementation of these
	 * repeats is hidden from the user by means of getRepeatQtnsDef() and setRepeatQtnsDef().
	 */
	private Object options;

	
	/** The id of the block this question belongs to, if any*/
	private String blockId = null;
	
	public static final int QTN_TYPE_NULL = 0;

	/** Text question type. */
	public static final int QTN_TYPE_TEXT = 1;

	/** Numeric question type. These are numbers without decimal points*/
	public static final int QTN_TYPE_NUMERIC = 2;

	/** Decimal question type. These are numbers with decimals */
	public static final int QTN_TYPE_DECIMAL = 3;

	/** Date question type. This has only date component without time. */
	public static final int QTN_TYPE_DATE = 4;

	/** Time question type. This has only time element without date*/
	public static final int QTN_TYPE_TIME = 5;

	/** This is a question with alist of options where not more than one option can be selected at a time. */
	public static final int QTN_TYPE_LIST_EXCLUSIVE = 6;

	/** This is a question with alist of options where more than one option can be selected at a time. */
	public static final int QTN_TYPE_LIST_MULTIPLE = 7;

	/** Date and Time question type. This has both the date and time components*/
	public static final int QTN_TYPE_DATE_TIME = 8;

	/** Question with true and false answers. */
	public static final int QTN_TYPE_BOOLEAN = 9;

	/** Question with repeat sets of questions. */
	public static final int QTN_TYPE_REPEAT = 10;

	/** Question with image. */
	public static final int QTN_TYPE_IMAGE = 11;

	/** Question with recorded video. */
	public static final byte QTN_TYPE_VIDEO = 12;

	/** Question with recoded audio. */
	public static final byte QTN_TYPE_AUDIO = 13;

	/** Question with GPS cordinates. */
	public static final int QTN_TYPE_GPS = 15;
	
	/** Question with barcode cordinates. */
	public static final int QTN_TYPE_BARCODE = 16;
	
	/** Question which is a group. */
	public static final int QTN_TYPE_GROUP = 17;
	
	/** Question which is a group. */
	public static final int QTN_TYPE_TRIGGER = 18;
	
	public static final String MULTI_SUFFIX = "_MULTI_";

	


	/** A list of interested listeners to the question change events. */
	private List<QuestionChangeListener> changeListeners = new ArrayList<QuestionChangeListener>();

	/** The parent object for this question. It could be a page or
	 * just another question as for repeat question kids. 
	 */
	private IFormElement parent;

	private String itextId;
	
	private int newQuestionId = 1;
	
	


	/** This constructor is used mainly during deserialization. */
	public QuestionDef(IFormElement parent){
		this.parent = parent;
	}

	/** The copy constructor. */
	public QuestionDef(QuestionDef questionDef, IFormElement parent){
		this(parent);
		setText(questionDef.getText());
		setHelpText(questionDef.getHelpText());
		setDataType(questionDef.getDataType());
		setDefaultValue(questionDef.getDefaultValue());
		setVisible(questionDef.isVisible());
		setEnabled(questionDef.isEnabled());
		setLocked(questionDef.isLocked());
		setRequired(questionDef.isRequired());
		setBinding(questionDef.getBinding());
		setItextId(questionDef.getItextId());

		if(getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE || getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE)
			copyQuestionOptions(questionDef.getOptions());
		else if(getDataType() == QuestionDef.QTN_TYPE_REPEAT)
		{
			this.options = new RepeatQtnsDef(questionDef.getRepeatQtnsDef());
			//we need to set the new repeat items to have this as their parent
			for(IFormElement element : ((RepeatQtnsDef)options).getChildren())
			{
				element.setParent(this);
			}
		}
	}

	public QuestionDef(String text,  int type, String variableName, IFormElement parent) {
		this(parent);
		setText(text);
		setDataType(type);
		setBinding(variableName);
	}

	/**
	 * Constructs a new question definition object from the supplied parameters.
	 * For String type parameters, they should NOT be NULL. They should instead be empty,
	 * for the cases of missing values.
	 * 
	 * @param id
	 * @param text
	 * @param helpText - The hint or help text. Should NOT be NULL.
	 * @param mandatory
	 * @param type
	 * @param defaultValue
	 * @param visible
	 * @param enabled
	 * @param locked
	 * @param variableName
	 * @param options
	 */
	public QuestionDef(String text, String helpText, boolean mandatory, int type, String defaultValue, boolean visible, boolean enabled, boolean locked, String variableName, Object options,IFormElement parent) {
		this(parent);
		setText(text);
		setHelpText(helpText);
		setDataType(type);
		setDefaultValue(defaultValue);
		setVisible(visible);
		setEnabled(enabled);
		setLocked(locked);
		setRequired(mandatory);		
		setBinding(variableName);
		setOptions(options);
	}
	
	public void setBindNode(Element bindNode)
	{
		this.bindNode = bindNode;
	}
	
	public Element getBindNode()
	{
		return bindNode;
	}
	
	public String getPreload() {
		return preload;
	}

	public void setPreload(String preload) {
		this.preload = preload;
	}

	public String getPreloadParam() {
		return preloadParam;
	}

	public void setPreloadParam(String preloadParam) {
		this.preloadParam = preloadParam;
	}

	
	public int getNewQuestionId()
	{
		return newQuestionId;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}

	public static boolean isDateFunction(String value){
		if(value == null)
			return false;

		return (value.contains("now()") || value.contains("date()")
				||value.contains("getdate()") || value.contains("today()"));
	}

	public static Date getDateFunctionValue(String function){
		return new Date();
	}

	public void setDefaultValue(String defaultValue) {	
		this.defaultValue = defaultValue;
		this.answer =  defaultValue;
	}
	

	public String getAnswer() {
		return answer;
	}


	public void setAnswer(String answer) {
		//if(defaultValue != null && defaultValue.trim().length() > 0)
		this.answer = answer;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		boolean changed = this.enabled != enabled;

		this.enabled = enabled;

		if(changed){
			for(int index = 0; index < changeListeners.size(); index++)
				changeListeners.get(index).onEnabledChanged(this,enabled);
		}
	}

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		boolean changed = this.locked != locked;

		this.locked = locked;

		if(changed){
			for(int index = 0; index < changeListeners.size(); index++)
				changeListeners.get(index).onLockedChanged(this,locked);
		}
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		boolean changed = this.required != required;

		this.required = required;

		if(changed){
			for(int index = 0; index < changeListeners.size(); index++)
				changeListeners.get(index).onRequiredChanged(this,required);
		}
	}

	public Object getOptions() {
		return options;
	}

	public void setOptions(Object options) {
		this.options = options;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {	
		this.text = text;
	}

	public int getDataType() {
		return dataType;
	}

	public String getItextId() {
		return itextId;
	}

	public void setItextId(String itextId) {
		this.itextId = itextId;
	}
	
	/**
	 * Sets the block id of a question.
	 * Use this if the question belongs to a block
	 * @param blockId the name of the block
	 */
	public void setBlockId(String blockId)
	{
		this.blockId = blockId;
	}
	
	/**
	 * Returns the block id of a question
	 * @return returns the block id of a question, null if there isn't one
	 */
	public String getBlockId()
	{
		return this.blockId;
	}

	public void setDataType(int dataType) {
		boolean changed = this.dataType != dataType;
		
		int oldDataType = this.dataType;
		this.dataType = dataType;

		if(changed){

			for(int index = 0; index < changeListeners.size(); index++)
				changeListeners.get(index).onDataTypeChanged(this,dataType);
			
			//change the options if need be			
			if(dataType == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE || dataType == QuestionDef.QTN_TYPE_LIST_MULTIPLE)
			{
				//only zero out the options if the question wasn't a list question to start with
				if(oldDataType != QuestionDef.QTN_TYPE_LIST_EXCLUSIVE && oldDataType != QuestionDef.QTN_TYPE_LIST_MULTIPLE)
				{
					options = new ArrayList<OptionDef>();
				}
			}
			else if(dataType == QuestionDef.QTN_TYPE_REPEAT)
			{
				options = new RepeatQtnsDef();
				((RepeatQtnsDef)options).setQtnDef(this);
			}
		}
		
		//Since a GPS can malfunction we don't want these to be required by default.
		if(dataType == QuestionDef.QTN_TYPE_GPS)
		{
			this.setRequired(false);
		}
	}

	public String getBinding() {
		return variableName;
	}

	public String getOldBinding()
	{
		return this.oldVariableName;
	}

	public IFormElement getParent() {
		return parent;
	}

	public void setParent(IFormElement parent) {
		this.parent = parent;
	}



	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		boolean changed = this.visible != visible;

		this.visible = visible;

		if(changed){
			for(int index = 0; index < changeListeners.size(); index++)
				changeListeners.get(index).onVisibleChanged(this,visible);
		}
	}

	public void removeChangeListener(QuestionChangeListener changeListener) {
		changeListeners.remove(changeListener);
	}

	public void addChangeListener(QuestionChangeListener changeListener) {
		if(!changeListeners.contains(changeListener))
			changeListeners.add(changeListener);
	}

	public void clearChangeListeners(){
		if(changeListeners != null)
			changeListeners.clear();
	}

	public void addOption(OptionDef optionDef){
		addOption(optionDef,true);
	}
	
	public void insertOption(OptionDef optionDef, int index)
	{
		if(options == null || !(options instanceof ArrayList))
		{
			options = new ArrayList<IFormElement>();
		}
		((List<IFormElement>)options).add(index, optionDef);
	}

	public void addOption(OptionDef optionDef, boolean setAsParent){
		if(options == null || !(options instanceof ArrayList))
			options = new ArrayList();
		((List)options).add(optionDef);

		if(setAsParent)
			optionDef.setParent(this);
	}

	public RepeatQtnsDef getRepeatQtnsDef(){
		if(options instanceof RepeatQtnsDef)
		{
			return (RepeatQtnsDef)options;
		}
		return null;
	}

	public void addRepeatQtnsDef(IFormElement qtn){
		newQuestionId++;
		if(options == null)
			options = new RepeatQtnsDef(this);
		else if (!(options instanceof RepeatQtnsDef))
			options = new RepeatQtnsDef(this);
		((RepeatQtnsDef)options).addQuestion(qtn);
		qtn.setParent(this);
	}

	public void setRepeatQtnsDef(RepeatQtnsDef repeatQtnsDef){
		options = repeatQtnsDef;
	}

	public String toString() {
		String retVal = getText() + " (" + getBinding() + ")";
		if(getDataType() == QuestionDef.QTN_TYPE_REPEAT)
		{
			retVal += " " + options.toString();
		}
		return retVal;
	}

	private void copyQuestionOptions(Object copyOptions){
		if(copyOptions == null)
			return;
		//if we're dealing with a repeat question def
		if(copyOptions instanceof RepeatQtnsDef)
		{
			this.options = ((RepeatQtnsDef)copyOptions).copy(this);
		}
		//if we're dealing with a list
		else if (copyOptions instanceof List<?>)
		{
			this.options = new ArrayList<Object>();
			for(Object o : (List<?>)copyOptions)
			{
				if (o instanceof ItemSetDef)
				{
					((List<Object>)this.options).add(((ItemSetDef)o).copy(this));
				}
				else if(o instanceof OptionDef)
				{
					((List<Object>)this.options).add(o);
				}
				
				
			}
		}
		
	}

	public boolean removeOption(IFormElement optionDef)
	{
		if(options instanceof List)
		{ //Could be a RepeatQtnsDef
			if(!((List)options).remove((OptionDef)optionDef))
				return false;
		}
		else if(options instanceof RepeatQtnsDef)
		{ //Could be a 
			if(!((RepeatQtnsDef)options).removeChild(optionDef))
				return false;
		}
		return true;
	}

	/**
	 * Gets the option with a given display text.
	 * 
	 * @param text the option text.
	 * @return the option definition object.
	 */
	public OptionDef getOptionWithText(String text){
		if(options == null || text == null)
			return null;

		List list = (List)options;
		for(int i=0; i<list.size(); i++){
			OptionDef optionDef = (OptionDef)list.get(i);
			if(optionDef.getText().equals(text))
				return optionDef;
		}
		return null;
	}

	

	/**
	 * Gets the option with a given variable name or binding.
	 * 
	 * @param value the variable name or binding.
	 * @return the option definition object.
	 */
	public OptionDef getOptionWithValue(String value){
		if(options == null || value == null)
			return null;

		List list = (List)options;
		for(int i=0; i<list.size(); i++){
			OptionDef optionDef = (OptionDef)list.get(i);
			if(optionDef.getDefaultValue().equals(value))
				return optionDef;
		}
		return null;
	}

	public int getOptionIndex(String varName)
	{
		if(options == null)
			return -1;
		if(! (options instanceof List<?>))
		{
			return -1;
		}
		List<?> list = (List<?>)(getOptions());
		for(int i=0; i< list.size(); i++){
			if(list.get(i) instanceof OptionDef)
			{
				OptionDef def = (OptionDef)list.get(i);
				if(def.getBinding().equals(varName))
					return i;
			}
		}

		return -1;
	}

	/**
	 * Gets the number of options for this questions.
	 * 
	 * @return the number of options.
	 */
	public int getOptionCount(){
		if(options == null)
			return 0;
		return ((List)options).size();
	}

	/**
	 * Gets the option at a given position (zero based).
	 * 
	 * @param index the position.
	 * @return the option definition object.
	 */
	public OptionDef getOptionAt(int index){
		return (OptionDef)((List)options).get(index);
	}


	public void setBinding(String binding)
	{
		boolean changed = this.variableName != binding;

		if(changed){
			this.oldVariableName = this.variableName;
			this.variableName = binding;
			for(int index = 0; index < changeListeners.size(); index++)
				changeListeners.get(index).onBindingChanged(this,binding);
		}
	}

	public List<IFormElement> getChildren(){

		if(options instanceof List<?>)
		{
			return (List<IFormElement>)options;
		}
		if(options instanceof RepeatQtnsDef)
		{
			return (List<IFormElement>)(((RepeatQtnsDef)options).getQuestions());
		}
		return new ArrayList<IFormElement>(); //there aren't any children
	}

	public void setChildren(List<IFormElement> children){
		this.options = children;
	}
	
	
	public IFormElement copy(IFormElement parent){
		return new QuestionDef(this, parent);
	}
	
	public void addChild(IFormElement element){	
		if(element instanceof OptionDef)
		{
			addOption((OptionDef)element);
		}
		else if(element instanceof QuestionDef || element instanceof GroupDef)
		{
			addRepeatQtnsDef(element);
		}
	}
	
	public boolean removeChild(IFormElement element){
		if(element instanceof OptionDef)
		{
			return this.removeOption((OptionDef)element);
		}
		else if (element instanceof QuestionDef)
		{
			((RepeatQtnsDef)options).removeChild(element);
			return true;
		}
		return false;
	}
	
	public int getChildCount(){
		return getOptionCount();
	}
	

	public FormDef getFormDef(){
		IFormElement element = getParent();
		if(parent instanceof FormDef)
			return (FormDef)element;
		
		return element.getFormDef();
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
		if(element instanceof OptionDef)
		{
			OptionDef optionDef = (OptionDef)element;
			if(options == null || !(options instanceof ArrayList<?>))
				options = new ArrayList<IFormElement>();
			
			if(index > ((List)options).size())
			{
				index = ((List)options).size();
			}

			((List)options).add(index, optionDef);

		}
	}
	
	/**
	 * Used to get the index of one of my children
	 */
	public int getIndexOfChild(IFormElement element)
	{
		return((List)options).indexOf(element);
	}
	
	/**
	 * Gets any question that's part of this form by it's binding
	 * 
	 * @param binding the binding to look for.
	 * @return the question of found, else null.
	 */
	public IFormElement getQuestionWithBinding(String binding){
		if(options instanceof RepeatQtnsDef)
		{
			
			for(int i=0; i<((RepeatQtnsDef)options).size(); i++){
				IFormElement element = ((RepeatQtnsDef)options).getQuestionAt(i);
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
		}
		return null;
	}
	
	/**
	 * Checks to see if the options in this question are itemsets
	 * @return
	 */
	public boolean usesItemSet()
	{
		if(options == null || !(options instanceof ArrayList))
		{
			return false;
		}
		ArrayList options = (ArrayList)this.options;
		
		for(int i = 0; i < options.size(); i++)
		{
			if(options.get(i) instanceof ItemSetDef)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * gets the item set this question uses
	 * @return the question set this question uses, null if it doesn't have one
	 */
	public ItemSetDef getItemSet()
	{
		if(options == null || !(options instanceof ArrayList))
		{
			return null;
		}
		ArrayList options = (ArrayList)this.options;
		
		for(int i = 0; i < options.size(); i++)
		{
			if(options.get(i) instanceof ItemSetDef)
			{
				return (ItemSetDef)(options.get(i));
			}
		}
		return null;
	}
	
//////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public String getPath() {
	
		return getParent().getPath() + "/" + getBinding();
	}

	@Override
	public void writeToXML(Document doc, Element instanceNode, Element bindNode,
			List<Element> iTextNodes, Element UINode) 
	{
		//is it a repeat question?
		if(getDataType() == QuestionDef.QTN_TYPE_REPEAT)
		{
			getRepeatQtnsDef().writeToXML(doc, instanceNode, bindNode, iTextNodes, UINode);
			return;
		}
		
		//first lets handle the instance stuff
		instanceNode = writeToXmlInstance(doc, instanceNode);
		//next let's handle binding
		writeToXmlBind(doc, bindNode);
		//next the iTextNode
		writeToXmliText(doc, iTextNodes);
		//now the UI
		UINode = writeToXmlUi(doc, UINode);
		
		for(IFormElement element : getChildren())
		{
			element.writeToXML(doc, instanceNode, bindNode, iTextNodes, UINode);
		}
				
	}
	
	
	/**
	 * This is used to create the user interface for a question
	 * @param doc
	 * @param UINode
	 * @return
	 */
	private Element writeToXmlUi(Document doc, Element UINode)
	{
		//is this question even visible?
		if(!isVisible())
		{
			return UINode;
		}
		
		//determine the kind of question it is
		String type = "";
		//what attributes might we need
		HashMap<String, String> attributes = new HashMap<String, String>();
		
		switch(getDataType())
		{
			case QuestionDef.QTN_TYPE_LIST_EXCLUSIVE:
				type = XformConstants.NODE_NAME_SELECT1;
				break;
			case QuestionDef.QTN_TYPE_TEXT:
			case QuestionDef.QTN_TYPE_NUMERIC:
			case QuestionDef.QTN_TYPE_DECIMAL:
			case QuestionDef.QTN_TYPE_DATE:
			case QuestionDef.QTN_TYPE_TIME:
			case QuestionDef.QTN_TYPE_DATE_TIME:
			case QuestionDef.QTN_TYPE_GPS:
			case QuestionDef.QTN_TYPE_BARCODE:
				type = XformConstants.NODE_NAME_INPUT;
				break;
			case QuestionDef.QTN_TYPE_LIST_MULTIPLE:
				type = XformConstants.NODE_NAME_SELECT;
				break;
			case QuestionDef.QTN_TYPE_IMAGE:
				attributes.put(XformConstants.ATTRIBUTE_NAME_MEDIATYPE, "image/*");
				type = XformConstants.NODE_NAME_UPLOAD;
				break;
			case QuestionDef.QTN_TYPE_VIDEO:
				attributes.put(XformConstants.ATTRIBUTE_NAME_MEDIATYPE, "video/*");
				type = XformConstants.NODE_NAME_UPLOAD;
				break;
			case QuestionDef.QTN_TYPE_AUDIO:
				attributes.put(XformConstants.ATTRIBUTE_NAME_MEDIATYPE, "audio/*");
				type = XformConstants.NODE_NAME_UPLOAD;
				break;
			case QuestionDef.QTN_TYPE_TRIGGER:
				type = XformConstants.NODE_NAME_TRIGGER;
				break;
			
		}
		//create the root of the instance
		Element questionUiNode = doc.createElement(type);
		//set the ID, not sure why we do this, but we do
		questionUiNode.setAttribute(XformConstants.ATTRIBUTE_NAME_BIND, getBinding());
		//now handle any special, one off, attributes
		for(String key : attributes.keySet())
		{
			String value = attributes.get(key);
			questionUiNode.setAttribute(key, value);
		}
		//now attach the instance root to the instance node
		UINode.appendChild(questionUiNode);
		//now return the instance root as the new baseline for all things instance related
		
		//now add the label
		Element labelNode = doc.createElement(XformConstants.NODE_NAME_LABEL);
		labelNode.setAttribute(XformConstants.ATTRIBUTE_NAME_REF, "jr:itext('" + getBinding() + "')");
		questionUiNode.appendChild(labelNode);
		
		//add a hint, if there is one
		if(getHelpText() != null && getHelpText().length() > 0)
		{
			//create the hint node
			Element hintNode = doc.createElement(XformConstants.NODE_NAME_HINT);
			hintNode.setAttribute(XformConstants.ATTRIBUTE_NAME_REF, "jr:itext('" + getBinding() + "-hint')");
			questionUiNode.appendChild(hintNode);
		}
		
		return questionUiNode;
	}
	
	/**
	 * Used to write the instance data	
	 * @param doc
	 * @param instanceNode
	 */
	private Element writeToXmlInstance(Document doc, Element instanceNode)
	{
		//create a node for this question
		Element questionNode = doc.createElement(getBinding());
		//add this to the parent node
		instanceNode.appendChild(questionNode);
		//return the parent
		return instanceNode;
	}
	
	/**
	 * Since binds are flat, we don't need to return the node
	 * @param doc the document being written to
	 * @param bindNode the bind node
	 */
	private void writeToXmlBind(Document doc, Element bindNode)
	{
		//create a bind for this question
		Element questionNode = doc.createElement(XformConstants.NODE_NAME_BIND);
		//set the ID
		questionNode.setAttribute(XformConstants.ATTRIBUTE_NAME_ID, getBinding());
		//set the nodeset
		questionNode.setAttribute(XformConstants.ATTRIBUTE_NAME_NODESET, getPath());
		//date type
		questionNode.setAttribute(XformConstants.ATTRIBUTE_NAME_TYPE, XformBuilderUtil.getXmlType(dataType,questionNode));
		//set required
		if(isRequired())
			questionNode.setAttribute(XformConstants.ATTRIBUTE_NAME_REQUIRED,XformConstants.XPATH_VALUE_TRUE);
		else
			questionNode.removeAttribute(XformConstants.ATTRIBUTE_NAME_REQUIRED);
		//is there a preload
		if(getPreload() != null && getPreload().length() > 0)
		{
			questionNode.setAttribute(XformConstants.ATTRIBUTE_NAME_PRELOAD, getPreload());
		}
		else
		{
			questionNode.removeAttribute(XformConstants.ATTRIBUTE_NAME_PRELOAD);
		}
		//is there a preload param?
		if(getPreloadParam() != null && getPreloadParam().length() > 0)
		{
			questionNode.setAttribute(XformConstants.ATTRIBUTE_NAME_PRELOAD_PARAM, getPreloadParam());
		}
		else
		{
			questionNode.removeAttribute(XformConstants.ATTRIBUTE_NAME_PRELOAD_PARAM);
		}
		//is this question enabled
		if(!isEnabled())
			questionNode.setAttribute(XformConstants.ATTRIBUTE_NAME_READONLY,XformConstants.XPATH_VALUE_TRUE);
		else
			questionNode.removeAttribute(XformConstants.ATTRIBUTE_NAME_READONLY);
		//is this question visible
		if(!isVisible())
			questionNode.setAttribute(XformConstants.ATTRIBUTE_NAME_VISIBLE,XformConstants.XPATH_VALUE_FALSE);
		else
			questionNode.removeAttribute(XformConstants.ATTRIBUTE_NAME_VISIBLE);
		//if we're bot binary then remove the name foramt?
		if(!(getDataType() == QuestionDef.QTN_TYPE_IMAGE || getDataType() == QuestionDef.QTN_TYPE_AUDIO ||
				getDataType() == QuestionDef.QTN_TYPE_VIDEO || getDataType() == QuestionDef.QTN_TYPE_GPS))
			questionNode.removeAttribute(XformConstants.ATTRIBUTE_NAME_FORMAT);
		else //if we're binary
		{
			if(getDataType() == QuestionDef.QTN_TYPE_GPS) //if we're GPS
				questionNode.setAttribute(XformConstants.ATTRIBUTE_NAME_TYPE,"geopoint");
			else
				questionNode.setAttribute(XformConstants.ATTRIBUTE_NAME_TYPE,"binary");

			questionNode.removeAttribute(XformConstants.ATTRIBUTE_NAME_FORMAT);
		}
		//not really sure what this does
		/*
		if(!(getDataType() == QuestionDef.QTN_TYPE_IMAGE || getDataType() == QuestionDef.QTN_TYPE_AUDIO ||
				getDataType() == QuestionDef.QTN_TYPE_VIDEO))
		{
			if(controlNode != null)
			{
				controlNode.removeAttribute(XformConstants.ATTRIBUTE_NAME_MEDIATYPE);
			}
		}
		else
		{
			if(controlNode != null)
			{
				UiElementBuilder.setMediaType(controlNode, dataType);
			}
		}
		*/
		
		//handle relevants
		writeToXmlRelevant(doc, questionNode);
		//handle the validates
		writeToXmlValidate(doc, questionNode);
		//handle calculations
		writeToXmlCalculations(doc, questionNode);

		bindNode.appendChild(questionNode);
		
	}//end writeToXmlBind()
	
	
	/**
	 * This used to create the necessary releveant xml for a question
	 * 
	 * @param doc
	 * @param questionNode
	 */
	private void writeToXmlRelevant(Document doc, Element questionNode)
	{
		//get the form def
		FormDef formDef = getFormDef();
		//make sure the skip rules aren't null
		if(formDef.getSkipRules() == null)
		{
			return;
		}
		//loop over the skip rules
		for(SkipRule skipRule : formDef.getSkipRules())
		{
			//is the current question the target of this skip rule
			if(skipRule.getActionTarget() != this)
			{
				continue;
			}
			//so this is the question, now build the skip rule
			//the relevant text
			String relevant = "";
			if(skipRule.getHandCode() != null)
			{
				//use that hand coded goodness
				relevant = skipRule.getHandCode();
			}
			else
			{
				//nope it's all machine made smoothness, use that instead.
				//get all the conditions
				Vector<Condition> conditions  = skipRule.getConditions();
				//loop over the conditions
				for(Condition condition : conditions)
				{
					if(relevant.length() > 0)
						relevant += XformBuilderUtil.getConditionsOperatorText(skipRule.getConditionsOperator());
					relevant += RelevantBuilder.fromSkipCondition2Xform(condition,formDef,skipRule.getAction());
				}
			}

			
	
			if(relevant.trim().length() == 0)
			{
				questionNode.removeAttribute(XformConstants.ATTRIBUTE_NAME_RELEVANT);
				questionNode.removeAttribute(XformConstants.ATTRIBUTE_NAME_ACTION);
			}
			else
			{
				questionNode.setAttribute(XformConstants.ATTRIBUTE_NAME_RELEVANT, relevant);
			}
			
			
		}//loop over all skip rules
	}
	
	
	/**
	 * This used to create the necessary validate xml for a question
	 * 
	 * @param doc
	 * @param questionNode
	 */
	private void writeToXmlValidate(Document doc, Element questionNode)
	{
		//get the form def
		FormDef formDef = getFormDef();
		//make sure the validation rules are there
		if(formDef.getValidationRules() == null)
		{
			return;
		}
		//loop over the skip rules
		for(ValidationRule validationRule : formDef.getValidationRules())
		{
			//is the current question the target of this skip rule
			if(validationRule.getQuestion() != this)
			{
				continue;
			}
			
			//now run the normal constraint building code
			
			Vector<Condition> conditions  = validationRule.getConditions();
			//if there are no conditions, then bounce.
			if(conditions == null || conditions.size() == 0)
			{
				questionNode.removeAttribute(XformConstants.ATTRIBUTE_NAME_CONSTRAINT);
				questionNode.removeAttribute(XformConstants.ATTRIBUTE_NAME_CONSTRAINT_MESSAGE);
				return;
			}

			//the string that will contain the constraints
			String constraint = "";
			for(Condition condition : conditions)
			{
				if(condition.getValue() == null && conditions.size() == 1)
				{
					questionNode.removeAttribute(XformConstants.ATTRIBUTE_NAME_CONSTRAINT);
					questionNode.removeAttribute(XformConstants.ATTRIBUTE_NAME_CONSTRAINT_MESSAGE);
					formDef.removeValidationRule(validationRule);
					return; //This could happen if say data type changed from text to single select.
				}
				
				if(constraint.length() > 0)
				{
					constraint += XformBuilderUtil.getConditionsOperatorText(validationRule.getConditionsOperator());
				}
				constraint += ConstraintBuilder.fromValidationRuleCondition2Xform(condition,formDef,ModelConstants.ACTION_ENABLE, this);
				 
			}

			questionNode.setAttribute(XformConstants.ATTRIBUTE_NAME_CONSTRAINT, constraint);
			questionNode.setAttribute(XformConstants.ATTRIBUTE_NAME_CONSTRAINT_MESSAGE, validationRule.getErrorMessage());
			
		}//loop over all validation rules
	}
	
	
	/**
	 * This is used to create the necessary calculation xml for a question
	 * @param doc
	 * @param questionNode
	 */
	private void writeToXmlCalculations(Document doc, Element questionNode)
	{
		//get the parent form
		FormDef formDef = getFormDef();
		//are there even any calculations?
		if(formDef.getCalculations() == null)
		{
			return;
		}
		//Build calculates for calculations
		for(Calculation calculation : formDef.getCalculations())
		{

			//make sure we have the right question
			if(calculation.getQuestion() != this)
			{
				continue;
			}
			
			questionNode.setAttribute(XformConstants.ATTRIBUTE_NAME_CALCULATE, calculation.getCalculateExpression());
		}
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
			
			/////////////////////////////////////////////////////////////////////////////////////////////
			//make an iText entry for hints
			/////////////////////////////////////////////////////////////////////////////////////////////
			
			//get the ItextModel for this question
			ItextModel itextHint = itextMap.get(getBinding()+"-hint");
			//if there's nothing there, then this is probably a hidden question
			if(itextHint == null)
			{
				continue;
			}
			//get the string
			String textHint = itextHint.get(languageName);
			//make sure it's not null
			if(textHint == null)
			{
				continue;
			}
			//add it to the translation node
			//create the text for the form title
			Element hintTextNode = doc.createElement(XformConstants.NODE_NAME_TEXT);
			//set the id
			hintTextNode.setAttribute(XformConstants.ATTRIBUTE_NAME_ID, getBinding() + "-hint");
			//create the value node for the form title
			Element hintValueNode = doc.createElement(XformConstants.NODE_NAME_VALUE);
			//set the text itself
			hintValueNode.appendChild(doc.createTextNode(textHint));
			//now add the value node to the text node
			hintTextNode.appendChild(hintValueNode);
			//add the text node to the translation node
			translationNode.appendChild(hintTextNode);
			
		}
	}
}

