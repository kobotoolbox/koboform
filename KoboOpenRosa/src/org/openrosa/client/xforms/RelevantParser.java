package org.openrosa.client.xforms;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.openrosa.client.model.Condition;
import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.IFormElement;
import org.openrosa.client.model.QuestionDef;
import org.openrosa.client.model.SkipRule;
import org.purc.purcforms.client.model.ModelConstants;


/**
 * Parses relevant attributes of xforms documents and builds the skip rule objects of the model.
 * 
 * @author daniel
 *
 */
public class RelevantParser {

	/**
	 * All methods in this class are static and hence we expect no external
	 * Instantiation of this class.
	 */
	private RelevantParser(){

	}


	/**
	 * Builds skip rule object from a list of relevant attribute values.
	 * 
	 * @param formDef the form defintion object to which the skip rules belong.
	 * @param relevants the map of relevant attribute values keyed by their 
	 * 					  question definition objects.
	 * @throws Exception 
	 */
	public static void addSkipRules(FormDef formDef, HashMap relevants) throws Exception{
		Vector<SkipRule> rules = new Vector<SkipRule>();

		HashMap<String,SkipRule> skipRulesMap = new HashMap<String,SkipRule>();

		Iterator keys = relevants.keySet().iterator();
		int id = 0;
		while(keys.hasNext()){
			QuestionDef qtn = (QuestionDef)keys.next();
			String relevant = (String)relevants.get(qtn);

			//If there is a skip rule with the same relevant as the current
			//then just add this question as another action target to the skip
			//rule instead of creating a new skip rule.
			SkipRule skipRule = skipRulesMap.get(relevant);
			skipRule = buildSkipRule(formDef, qtn,relevant,++id,XformParserUtil.getAction(qtn));
			if(skipRule != null)
			{
				rules.add(skipRule);
				skipRulesMap.put(relevant, skipRule);
			}
			else
			{
				System.out.println("shouldn't ever get here addSkipRules() src/org/openrosa/client/xforms/ReleveantParser.java");
			}
		}

		formDef.setSkipRules(rules);
	}


	/**
	 * Creates a skip rule object from a relevent attribute value.
	 * 
	 * @param formDef the form definition object to build the skip rule for.
	 * @param questionId the identifier of the question which is the target of the skip rule.
	 * @param relevant the relevant attribute value.
	 * @param id the identifier for the skip rule.
	 * @param action the skip rule action to apply to the above target question.
	 * @return the skip rule object.
	 * @throws Exception 
	 */
	private static SkipRule buildSkipRule(FormDef formDef, QuestionDef questionId, String relevant, int id, int action) throws Exception{

		SkipRule skipRule = new SkipRule();
		skipRule.setId(id);
		//TODO For now we are only dealing with enabling and disabling.
		skipRule.setAction(action);
		Vector<Condition> conditions = getSkipRuleConditions(formDef,relevant,action);
		if(conditions != null)
		{
			skipRule.setConditions(conditions);
			skipRule.setConditionsOperator(XformParserUtil.getConditionsOperator(relevant));
		}
		else
		{
			skipRule.setHandCode(relevant);
		}

		//For now we only have one action target, much as the object model is
		//flexible enough to support any number of them.
		skipRule.setActionTarget(questionId);

		// If skip rule has no conditions, then its as good as no skip rule at all.
		if((skipRule.getConditions() == null || skipRule.getConditions().size() == 0) && (skipRule.getHandCode() == null))
			return null;
		return skipRule;
	}


	/**
	 * Gets a list of conditions for a skip rule as per the relevant attribute value.
	 * 
	 * @param formDef the form definition object to which the skip rule belongs.
	 * @param relevant the relevant attribute value.
	 * @param action the skip rule target action.
	 * @return the conditions list.
	 * @throws Exception 
	 */
	private static Vector<Condition> getSkipRuleConditions(FormDef formDef, String relevant, int action) throws Exception{
		Vector<Condition> conditions = new Vector<Condition>();

		Vector<String> list = XpathParser.getConditionsOperatorTokens(relevant);
		if(list == null)
		{
			return null;
		}
		
		Condition condition  = new Condition();
		for(int i=0; i < list.size(); i++){
			condition = getSkipRuleCondition(formDef,(String)list.elementAt(i),(int)(i+1),action);
			if(condition != null)
			{
				conditions.add(condition);
			}
			else
			{
				return null;
			}
		}

		return conditions;
	}


	/**
	 * Creates a skip rule condition object from a portion of the relevant attribute value.
	 * 
	 * @param formDef the form definition object to which the skip rule belongs.
	 * @param relevant the token or portion from the relevant attribute value.
	 * @param id the new condition identifier.
	 * @param action the skip rule target action.
	 * @return the new condition object.
	 * @throws Exception 
	 */
	private static Condition getSkipRuleCondition(FormDef formDef, String relevant, int id, int action) throws Exception{		
		

		//eg relevant="/data/question10='7'"
		OperatorInfo opInfo = XformParserUtil.getOperatorInfo(relevant);
		int pos = opInfo.getPosInExpression();
		if(pos < 0)
			return null;

		if (opInfo.isFunction())
		{
			return getSkipRuleConditionForFunction(opInfo, formDef, relevant, id, action);
		}
		else
		{
			return getSkipRuleConditionForMathematicalOperator(opInfo, formDef, relevant, id, action);
		}

	}
	
	
	/**
	 * Used to parse a function
	 * @param pos
	 * @param formDef
	 * @param relevant
	 * @param id
	 * @param action
	 * @return
	 * @throws Exception
	 */
	private static Condition getSkipRuleConditionForFunction(OperatorInfo opInfo, FormDef formDef, String relevant, int id, int action) throws Exception
	{
		Condition condition  = new Condition();
		condition.setId(id);
		condition.setOperator(opInfo.getOperatorIntValue());
		
		//we're going to parse each one of these differently so switch on the value
		//and pick the right parser
		switch(opInfo.getOperatorIntValue())
		{
			case ModelConstants.OPERATOR_IN_LIST:
			case ModelConstants.OPERATOR_NOT_IN_LIST:
				return getSkipRuleCondition_selectedFunction(condition, opInfo, formDef, relevant, id, action);				
		}
		
		return null;
	}
	
	/**
	 * Prases the "selected" function
	 * @param condition
	 * @param opInfo
	 * @param formDef
	 * @param relevant
	 * @param id
	 * @param action
	 * @return
	 * @throws Exception
	 */
	private static Condition getSkipRuleCondition_selectedFunction(Condition condition, OperatorInfo opInfo, FormDef formDef, String relevant, int id, int action) throws Exception
	{
		//first lets get the first parameter, which should be the path to a question.
		//well start by loping off the function name and the opening parentheses
		String param1 = relevant.substring(opInfo.getPosInExpression() + opInfo.getOperatorStr().length()+1);
		//now we'll chop off everything after the first comma
		param1 = param1.substring(0,param1.indexOf(",")).trim();
		IFormElement questionDef = getFormElement(param1, formDef);
		if(questionDef == null)
		{
			return null;
		}
		condition.setQuestion((QuestionDef)questionDef);
		
		
		//second lets get the 2nd value, which should be a value, most likely wrapped in single quotes
		//we'll start by chopping off everything after that first comma
		String param2 = relevant.substring(relevant.indexOf(",")+1);
		//now chop off that closing parentheses and trim
		param2 = param2.substring(0, param2.lastIndexOf(")")).trim();
		
			
		return setValue(param2, condition, formDef);
		
	}
	
	
	/**
	 * Gets the value, handles quotes and such.
	 * @param value
	 * @return
	 */
	private static Condition setValue(String value, Condition condition, FormDef formDef)
	{
		//first try a value delimited by '
		int pos2 = value.lastIndexOf('\'');
		if(pos2 > 0){
			//pos1++;
			int pos1 = value.substring(0, pos2).lastIndexOf('\'',pos2);
			if(pos1 < 0){
				System.out.println("Relevant value not closed with ' characher");
				return null;
			}
			pos1++;
			value = value.substring(pos1,pos2);
		}
		

		value = value.trim();
		if(!(value.equals("null") || value.equals(""))){
			condition.setValue(value);

			//This is just for the designer
			if(value.startsWith(formDef.getVariableName() + "/"))
				condition.setValueQtnDef((QuestionDef)formDef.getElement(value.substring(value.indexOf('/')+1)));

			if(condition.getOperator() == ModelConstants.OPERATOR_NULL)
				return null; //no operator set hence making the condition invalid
		}
		else
			condition.setOperator(ModelConstants.OPERATOR_IS_NULL);
		
		return condition;

	}//end
	
	
	/**
	 * Use this to turn an xpath into the corresponding IFormElement object.
	 * @param varName
	 * @param formDef
	 * @return
	 */
	private static IFormElement getFormElement(String varName, FormDef formDef)
	{
		IFormElement questionDef = formDef.getElement(varName.trim());
		if(questionDef == null){
			String prefix = "/" + formDef.getVariableName() + "/";
			if(varName.startsWith(prefix))
				questionDef = formDef.getElement(varName.trim().substring(prefix.length(), varName.trim().length()));
			if(questionDef == null)
				return null;
		}
			
		return questionDef;
	}
	
	
	/**
	 * Called when you need to parse out a plain old mathematical operator like < or =
	 * @param pos
	 * @param formDef
	 * @param relevant
	 * @param id
	 * @param action
	 * @return
	 * @throws Exception
	 */
	private static Condition getSkipRuleConditionForMathematicalOperator(OperatorInfo opInfo, FormDef formDef, String relevant, int id, int action) throws Exception
	{
		Condition condition  = new Condition();
		condition.setId(id);
		condition.setOperator(opInfo.getOperatorIntValue());
		
		String varName = relevant.substring(0, opInfo.getPosInExpression());
		IFormElement questionDef = getFormElement(varName, formDef);
		if(questionDef == null)
		{
			return null;
		}
		
		condition.setQuestion((QuestionDef)questionDef);

		String value;
		//first try a value delimited by '
		int pos2 = relevant.lastIndexOf('\'');
		if(pos2 > 0){
			//pos1++;
			int pos1 = relevant.substring(0, pos2).lastIndexOf('\'',pos2);
			if(pos1 < 0){
				System.out.println("Relevant value not closed with ' characher");
				return null;
			}
			pos1++;
			value = relevant.substring(pos1,pos2);
		}
		else //else we take whole value after operator	
			value = relevant.substring(opInfo.getPosInExpression()+XformParserUtil.getOperatorSize(condition.getOperator(),action),relevant.length());

		value = value.trim();
		if(!(value.equals("null") || value.equals(""))){
			condition.setValue(value);

			//This is just for the designer
			if(value.startsWith("/"+formDef.getVariableName() + "/" ))
			{
				QuestionDef question = (QuestionDef)formDef.getElement(value.substring(value.lastIndexOf('/')+1));
				condition.setValueQtnDef(question);
				value = question.getText() + "\t<"+question.getBinding()+">";
				condition.setValue(value);
				
			}

			if(condition.getOperator() == ModelConstants.OPERATOR_NULL)
				return null; //no operator set hence making the condition invalid
		}
		else
			condition.setOperator(ModelConstants.OPERATOR_IS_NULL);
		
		return condition;
	}
	
}
