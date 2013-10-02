package org.openrosa.client.model;

import java.io.Serializable;
import java.util.Vector;

import org.openrosa.client.xforms.RelevantBuilder;


/**
 * A definition for skipping or branching rules. 
 * These could for example be enabling or disabling, hiding or showing, making mandatory or optional 
 * of questions basing on values of others. In xforms, this is a relevant atttribute.
 * 
 * @author Daniel Kayiwa
 *
 */
public class SkipRule implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7481463018284855183L;

	/** The numeric identifier of a rule. This is assigned in code and hence
	 * is not known by the user.
	 */
	private int id = ModelConstants.NULL_ID;
	
	/** A list of conditions (Condition object) to be tested for a rule. 
	 * E.g. If sex is Male. If age is greatern than 4. etc
	 */
	private Vector<Condition> conditions;
	
	/** The action taken when conditions are true.
	 * Example of actions are Disable, Hide, Show, etc
	 */
	private int action = ModelConstants.ACTION_NONE;
	
	/** A list of question identifiers (int) acted upon when conditions for the rule are true. */
	private IFormElement actionTarget;
		
	/** Operator for combining more than one condition. (And, Or) only these two for now. */
	private int conditionsOperator = ModelConstants.CONDITIONS_OPERATOR_NULL;
	
	/** for those times when you just want to hand code something**/
	private String handCode = null;
		
	
	
	public String getHandCode() {
		return handCode;
	}

	public void setHandCode(String handCode) {
		this.handCode = handCode;
	}

	/** Constructs a rule object ready to be initialized. */
	public SkipRule(){

	}
	
	/** Copy constructor. */
	public SkipRule(SkipRule skipRule){
		setId(skipRule.getId());
		setAction(skipRule.getAction());
		setConditionsOperator(skipRule.getConditionsOperator());
		copyConditions(skipRule.getConditions());
		setActionTarget(skipRule.getActionTarget());
	}
	
	/** Construct a Rule object from parameters. 
	 * 
	 * @param ruleId 
	 * @param conditions 
	 * @param action
	 * @param actionTargets
	 */
	public SkipRule(int ruleId, Vector<Condition> conditions, int action, IFormElement actionTarget /*, String name*/) {
		setId(ruleId);
		setConditions(conditions);
		setAction(action);
		setActionTarget(actionTarget);
	}
	
	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public IFormElement getActionTarget() {
		return actionTarget;
	}

	public void setActionTarget(IFormElement actionTarget) {
		this.actionTarget = actionTarget;
	}

	public Vector<Condition> getConditions() {
		return conditions;
	}

	public void setConditions(Vector<Condition> conditions) {
		this.conditions = conditions;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getConditionsOperator() {
		return conditionsOperator;
	}

	public void setConditionsOperator(int conditionsOperator) {
		this.conditionsOperator = conditionsOperator;
	}
	
	public Condition getConditionAt(int index) {
		if(conditions == null)
			return null;
		return (Condition)conditions.elementAt(index);
	}
	
	public int getConditionCount() {
		if(conditions == null)
			return 0;
		return conditions.size();
	}
	
		
	public void addCondition(Condition condition){
		if(conditions == null)
			conditions = new Vector<Condition>();
		conditions.add(condition);
	}
	
	public boolean containsCondition(Condition condition){
		if(conditions == null)
			return false;
		return conditions.contains(condition);
	}
	
	public void updateCondition(Condition condition){
		for(int i=0; i<conditions.size(); i++){
			Condition cond = (Condition)conditions.elementAt(i);
			if(cond.getId() == condition.getId()){
				conditions.remove(i);
				conditions.add(condition);
				break;
			}
		}
	}
	
	public void removeCondition(Condition condition){
		conditions.remove(condition);
	}
	
	
	public void removeQuestion(IFormElement questionDef)
	{
		for(int index = 0; index < getConditionCount(); index++)
		{
			Condition condition = getConditionAt(index);
			if(condition.getQuestion() == questionDef)
			{
				removeCondition(condition);
				index++;
			}
		}
	}

	/** 
	 * Checks conditions of a rule and executes the corresponding actions
	 * 
	 * @param data
	 */
	public void fire(FormDef formDef){
		boolean trueFound = false, falseFound = false;
		
		for(int i=0; i<getConditions().size(); i++){
			Condition condition = (Condition)this.getConditions().elementAt(i);
			if(condition.isTrue(formDef,false))
				trueFound = true;
			else
				falseFound = true;
		}
		
		if(getConditions().size() == 1 || getConditionsOperator() == ModelConstants.CONDITIONS_OPERATOR_AND)
			ExecuteAction(formDef,!falseFound);
		else if(getConditionsOperator() == ModelConstants.CONDITIONS_OPERATOR_OR)
			ExecuteAction(formDef,trueFound);
		//else do nothing
	}
	
	/** Executes the action of a rule for its conditition's true or false value. */
	public void ExecuteAction(FormDef formDef,boolean conditionTrue)
	{
		
		ExecuteAction(getActionTarget().getFormDef(),conditionTrue);
	}
	
	/** Executes the rule action on the supplied question. */
	public void ExecuteAction(QuestionDef qtn,boolean conditionTrue){
		qtn.setVisible(true);
		qtn.setEnabled(true);
		qtn.setRequired(false);
		
		if((action & ModelConstants.ACTION_ENABLE) != 0)
			qtn.setEnabled(conditionTrue);
		else if((action & ModelConstants.ACTION_DISABLE) != 0)
			qtn.setEnabled(!conditionTrue);
		else if((action & ModelConstants.ACTION_SHOW) != 0)
			qtn.setVisible(conditionTrue);
		else if((action & ModelConstants.ACTION_HIDE) != 0)
			qtn.setVisible(!conditionTrue);
		
		if((action & ModelConstants.ACTION_MAKE_MANDATORY) != 0)
			qtn.setRequired(conditionTrue);
	}
	
	private void copyConditions(Vector<Condition> conditions){
		this.conditions = new Vector<Condition>();
		for(int i=0; i<conditions.size(); i++)
			this.conditions.addElement(new Condition((Condition)conditions.elementAt(i)));
	}
	

	
	public void refresh(FormDef dstFormDef, FormDef srcFormDef){
		SkipRule skipRule = new SkipRule();;
		skipRule.setConditionsOperator(getConditionsOperator());
		skipRule.setAction(getAction());
		skipRule.setId(getId());
		
		for(int index = 0; index < this.getConditionCount(); index++){
			Condition condition = getConditionAt(index);
			QuestionDef qtn = condition.getQuestion();
			if(qtn == null)
				continue;
			QuestionDef questionDef = (QuestionDef) (dstFormDef.getChild(qtn.getBinding()));
			if(questionDef == null)
				continue;
				
			condition.setQuestion(questionDef);
			skipRule.addCondition(new Condition(condition));
		}
		
		if(skipRule.getConditionCount() == 0)
			return; //No matching condition found.
		
		
			QuestionDef qtn = (QuestionDef)(actionTarget);
			if(qtn != null)
			{
				QuestionDef questionDef = (QuestionDef) (dstFormDef.getChild(qtn.getBinding()));
				if(questionDef != null)
				{
					skipRule.setActionTarget(questionDef);
				}
			}
		
		
		if(skipRule.getActionTarget() != null)
			dstFormDef.addSkipRule(skipRule);
	}
	
	public void updateConditionValue(String origValue, String newValue){
		for(int index = 0; index < this.getConditionCount(); index++)
			getConditionAt(index).updateValue(origValue, newValue);
	}
	
	/**
	 * Override the 2 string method
	 */
	public String toString()
	{
		if(handCode != null)
		{
			return handCode;			
		}
		
		String retVal = "When ";
		retVal += conditionsOperator == 1 ? " all of " : " any of ";
		int i = 0;
		if(conditions != null)
		{
			for(Condition c : conditions)
			{
				i++;
				if(i > 1)
				{
					retVal += ", ";
				}
				retVal += c.toString();
			}
		}
		
		retVal += " then ";
		
		
		if(actionTarget != null)
		{
			retVal += actionTarget.getText();
		}
		else
		{
			retVal += "**null**";
		}
		retVal += " is not skipped";
		
		return retVal;
	}
}
 