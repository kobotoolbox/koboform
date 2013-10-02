package org.openrosa.client.xforms;

/**
 * Just a class to keep info about operators
 * @author Etherton
 *
 */
public class OperatorInfo {
	
	/**Where in the expression this operator falls*/
	private int posInExpression;
	
	/**Is this a function instead of a boring old mathematical operator*/
	private boolean isFunction;
	
	/**The string representation of the operator*/
	private String operatorStr;
	
	/**The internal integer code for the operator*/
	private int operatorIntValue;
	
	
	/**
	 * Constructor
	 * @param posInExpression
	 * @param isFunction
	 * @param operatorStr
	 */
	public OperatorInfo (int posInExpression, boolean isFunction, String operatorStr, int operatorIntValue)
	{
		this.posInExpression = posInExpression;
		this.isFunction = isFunction;
		this.operatorStr = operatorStr;
		this.operatorIntValue = operatorIntValue;
	}


	public int getPosInExpression() {
		return posInExpression;
	}


	public void setPosInExpression(int posInExpression) {
		this.posInExpression = posInExpression;
	}


	public boolean isFunction() {
		return isFunction;
	}


	public void setFunction(boolean isFunction) {
		this.isFunction = isFunction;
	}


	public String getOperatorStr() {
		return operatorStr;
	}


	public void setOperatorStr(String operatorStr) {
		this.operatorStr = operatorStr;
	}


	public int getOperatorIntValue() {
		return operatorIntValue;
	}


	public void setOperatorIntValue(int operatorIntValue) {
		this.operatorIntValue = operatorIntValue;
	}
	
	
	

}
