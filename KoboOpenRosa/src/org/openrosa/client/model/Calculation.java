package org.openrosa.client.model;

import java.io.Serializable;

import org.purc.purcforms.client.xforms.XformConstants;

import com.google.gwt.xml.client.Element;


/**
 * 
 * @author daniel
 *
 */
public class Calculation implements Serializable{

	/** The unique identifier of the question whose value to calculate. */
	private QuestionDef question = null;
	
	/** The calculate xpath expression. */
	private String calculateExpression = ModelConstants.EMPTY_STRING;


	public Calculation(Calculation calculation) {
		this(calculation.getQuestion(),calculation.getCalculateExpression());
	}

	public Calculation(QuestionDef question, String calculateExpression) {
		super();
		this.question = question;
		this.calculateExpression = calculateExpression;
	}

	public QuestionDef getQuestion() {
		return question;
	}

	public void setQuestion(QuestionDef question) {
		this.question = question;
	}

	public String getCalculateExpression() {
		return calculateExpression;
	}

	public void setCalculateExpression(String calculateExpression) {
		this.calculateExpression = calculateExpression;
	}
	
}
