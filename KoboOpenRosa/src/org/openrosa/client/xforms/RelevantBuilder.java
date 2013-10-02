package org.openrosa.client.xforms;

import java.util.Vector;

import org.openrosa.client.model.Condition;
import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.IFormElement;
import org.openrosa.client.model.QuestionDef;
import org.openrosa.client.model.SkipRule;
import org.purc.purcforms.client.model.ModelConstants;
import org.purc.purcforms.client.xforms.XformBuilderUtil;
import org.purc.purcforms.client.xforms.XformConstants;

import com.google.gwt.xml.client.Element;


/**
 * Builds relevant attributes of xforms documents from skip rule definition objects.
 * 
 * @author daniel
 *
 */
public class RelevantBuilder {
	
	private static final String LIST_SEPARATOR = " , ";
	private static final String AND = " and ";
	private static final String OR = " or ";
	private static final String SELECTED = "selected";
	private static final String NOT = "not";
	
	/**
	 * All methods in this class are static and hence we expect no external
	 * Instantiation of this class.
	 */
	private RelevantBuilder(){

	}
	
	
	/**
	 * Converts a skip rule definition object to xforms.
	 * 
	 * @param rule the skip rule definition object
	 * @param formDef the form definition.
	 */
	public static void fromSkipRule2Xform(SkipRule rule, FormDef formDef){
		String relevant = "";
		Vector<Condition> conditions  = rule.getConditions();
		for(int i=0; i<conditions.size(); i++){
			if(relevant.length() > 0)
				relevant += XformBuilderUtil.getConditionsOperatorText(rule.getConditionsOperator());
			relevant += fromSkipCondition2Xform((Condition)conditions.elementAt(i),formDef,rule.getAction());
		}

		IFormElement actionTarget =  rule.getActionTarget();
		
		QuestionDef questionDef = (QuestionDef)(actionTarget);
		if(questionDef != null)
		{	
			Element node = questionDef.getBindNode();
			//if(node == null)
			//	node = questionDef.getControlNode();
	
			if(relevant.trim().length() == 0){
				node.removeAttribute(XformConstants.ATTRIBUTE_NAME_RELEVANT);
				node.removeAttribute(XformConstants.ATTRIBUTE_NAME_ACTION);
				//node.removeAttribute(XformConstants.ATTRIBUTE_NAME_REQUIRED); //A question can still be required even if doesn't have a relavent
			}
			else{
				node.setAttribute(XformConstants.ATTRIBUTE_NAME_RELEVANT, relevant);
			}
		}
		
	}
	
	
	/**
	 * Creates an xforms representation of a skip rule condition.
	 * 
	 * @param condition the condition object.
	 * @param formDef the form definition object to which the skip rule belongs.
	 * @param action the skip rule action to its target questions.
	 * @return the condition xforms representation.
	 */
	public static String fromSkipCondition2Xform(Condition condition, FormDef formDef, int action){
		String binding = "";
		String relavent = "";

		QuestionDef questionDef = condition.getQuestion();
		if(questionDef != null){
			
			
			binding = questionDef.getPath();
			
			if(questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE)
			{
				String value = condition.getValue();
				String[] options = value.split(LIST_SEPARATOR);
				if(options == null || options.length == 0)
					return "";
				else
				{
					for(int i=0; i<options.length; i++)
					{
						if(relavent.length() > 0)
						{
							relavent += condition.getOperator() == ModelConstants.OPERATOR_IN_LIST ? AND : OR;
						}
						switch(condition.getOperator())
						{
							case ModelConstants.OPERATOR_IS_NULL:
								relavent += binding + " = ''";
								break;
							case ModelConstants.OPERATOR_IS_NOT_NULL:
								relavent += binding + " != ''";
								break;
							case ModelConstants.OPERATOR_IN_LIST:
								relavent += SELECTED + "("+binding+",'"+options[i]+"')";
								break;
							case ModelConstants.OPERATOR_NOT_IN_LIST:
								relavent += NOT + "(" + SELECTED + "("+binding+",'"+options[i]+"'))";
								break;
								
						}
					}
				}
			}
			else
			{
				//if we're using a value question then don't put it in single quotes
				String value = "";
				if(condition.getValueQtnDef() != null)
				{
					 value = " " + condition.getValueQtnDef().getPath();
				}
				else
				{
					//if we're using a numeric operator, then don't enclose in single quotes
					if(condition.isOperatorNumeric() )
					{
						value = " " + condition.getValue();
					}
					else
					{	
						value = " '" + condition.getValue() + "'";
					}
					if(questionDef.getDataType() == QuestionDef.QTN_TYPE_BOOLEAN || questionDef.getDataType() == QuestionDef.QTN_TYPE_DECIMAL || questionDef.getDataType() == QuestionDef.QTN_TYPE_NUMERIC)
						value = " " + condition.getValue();
				}
	
				relavent += binding + " " + XformBuilderUtil.getXpathOperator(condition.getOperator(),action)+value;
			}
			
			
		}
		return relavent;
	}
}
