package org.openrosa.client.xforms;

import java.util.HashMap;
import java.util.Vector;

import org.openrosa.client.model.Condition;
import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.QuestionDef;
import org.openrosa.client.model.ValidationRule;
import org.purc.purcforms.client.model.ModelConstants;
import org.purc.purcforms.client.xforms.XformConstants;

import com.google.gwt.xml.client.Element;


/**
 * Parses constraint attributes of xforms documents and builds the validation
 *  rule objects of the model.
 * 
 * @author daniel
 *
 */
public class ConstraintParser {

	/**
	 * All methods in this class are static and hence we expect no external
	 * Instantiation of this class.
	 */
	private ConstraintParser(){

	}
	
	
	/**
	 * Builds validation rule object from a list of constraint attribute values.
	 * 
	 * @param formDef the form defintion object to which the validation rules belong.
	 * @param constraints the map of constraint attribute values keyed by their 
	 * 					  question definition objects.
	 * @throws Exception 
	 */
	public static void addValidationRules(FormDef formDef, HashMap<QuestionDef, Element> constraints) throws Exception{
		Vector<ValidationRule> rules = new Vector<ValidationRule>();

		
		for(QuestionDef qtn : constraints.keySet())
		{
			ValidationRule validationRule = buildValidationRule(formDef, qtn, constraints.get(qtn));
			if(validationRule != null)
				rules.add(validationRule);
		}
		

		formDef.setValidationRules(rules);
	}
	
	
	/**
	 * Creates a validation rule object from a constraint attribute value.
	 * 
	 * @param formDef the form definition object to build the validation rule for.
	 * @param questionId the identifier of the question which is the target of the validation rule.
	 * @param constraint the constraint attribute value.
	 * @return the validation rule object.
	 * @throws Exception 
	 */
	private static ValidationRule buildValidationRule(FormDef formDef, QuestionDef questionId, Element constraint) throws Exception{

		String constraintStr = constraint.getAttribute(XformConstants.ATTRIBUTE_NAME_CONSTRAINT);
		String constraintMsg = constraint.getAttribute(XformConstants.ATTRIBUTE_NAME_CONSTRAINT_MESSAGE);
		
		ValidationRule validationRule = new ValidationRule(questionId,formDef);
		validationRule.setConditions(getValidationRuleConditions(formDef,constraintStr,questionId));
		validationRule.setConditionsOperator(XformParserUtil.getConditionsOperator(constraintStr));
		if(constraintMsg != null)
		{
			validationRule.setErrorMessage(constraintMsg);
		}

		// If the validation rule has no conditions, then its as good as no rule at all.
		if(validationRule.getConditions() == null || validationRule.getConditions().size() == 0)
			return null;
		return validationRule;
	}
	
	
	/**
	 * Gets a list of conditions for a validation rule as per the constraint attribute value.
	 * 
	 * @param formDef the form definition object to which the validation rule belongs.
	 * @param constraint the relevant attribute value.
	 * @param questionId the identifier of the question which is the target of the validation rule.
	 * @return the conditions list.
	 * @throws Exception 
	 */
	private static Vector<Condition> getValidationRuleConditions(FormDef formDef, String constraint, QuestionDef questionId) throws Exception{
		Vector<Condition> conditions = new Vector<Condition>();
		Vector<String> list = XpathParser.getConditionsOperatorTokens(constraint);

		Condition condition  = new Condition();
		for(int i=0; i<list.size(); i++){
			condition = getValidationRuleCondition(formDef,(String)list.elementAt(i),questionId);
			if(condition != null)
				conditions.add(condition);
		}

		return conditions;
	}
	
	
	/**
	 * Creates a validation rule condition object from a portion of the constraint attribute value.
	 * 
	 * @param formDef the form definition object to which the validation rule belongs.
	 * @param constraint the token or portion from the constraint attribute value.
	 * @param questionId the identifier of the question that has the validation rule.
	 * @return the new condition object.
	 * @throws Exception 
	 */
	private static Condition getValidationRuleCondition(FormDef formDef, String constraint, QuestionDef questionId) throws Exception{
		
		OperatorInfo opInfo = XformParserUtil.getOperatorInfo(constraint);

		if(opInfo == null)
		{
			return null;
		}
		
		Condition condition  = new Condition();
		condition.setOperator(opInfo.getOperatorIntValue());
		condition.setQuestion(questionId);

		//eg . &lt;= 40"
		int pos = opInfo.getPosInExpression();
		if(pos < 0)
			return null;

		QuestionDef questionDef = questionId;
		if(questionDef == null)
			return null;

		String value;
		//first try a value delimited by '
		int pos2 = constraint.lastIndexOf('\'');
		if(pos2 > 0){
			//pos1++;
			int pos1 = constraint.substring(0, pos2).lastIndexOf('\'',pos2);
			if(pos1 < 0){
				System.out.println("constraint value not closed with ' characher");
				return null;
			}
			pos1++;
			value = constraint.substring(pos1,pos2);
		}
		else //else we take whole value after operator	
			value = constraint.substring(pos+XformParserUtil.getOperatorSize(condition.getOperator(),ModelConstants.ACTION_ENABLE),constraint.length());

		value = value.trim();
		if(!(value.equals("null") || value.equals(""))){
			condition.setValue(value);

			//This is just for the designer
			if(value.startsWith("/" + formDef.getVariableName() + "/"))
			{
				condition.setValueQtnDef((QuestionDef)formDef.getElement(value.substring(value.lastIndexOf('/')+1)));
				QuestionDef qd = condition.getValueQtnDef();
				condition.setValue(qd.getText() + "\t<"+qd.getBinding()+">");
			}

			if(condition.getOperator() == ModelConstants.OPERATOR_NULL)
				return null; //no operator set hence making the condition invalid
		}
		else
			condition.setOperator(ModelConstants.OPERATOR_IS_NULL);

		if(constraint.contains("length(.)") || constraint.contains("count(.)"))
			condition.setFunction(ModelConstants.FUNCTION_LENGTH);

		return condition;
	}
}
