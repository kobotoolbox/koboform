package org.openrosa.client.xforms;

import java.util.Vector;

import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.xforms.XformConstants;

import com.google.gwt.user.client.Window;


/**
 * Parses xpath expression in relevant and constraint attribute values
 * and get out the list of condition tokens.
 * 
 * @author daniel
 *
 */
public class XpathParser {

	/**
	 * All methods in this class are static and hence we expect no external
	 * Instantiation of this class.
	 */
	private XpathParser(){

	}
	
	/**
	 * Used to get the starting and ending index of string literals in an expression
	 * For our puroses a string literal is any set of characters that appear between a starting
	 * and ending single quote "'". The single quotes can be escaped with a backslash.
	 * 
	 * @param expression The string that we want to find string literals 
	 * @return a vector where the value at index [n2] is the start index and the value at [n2 + 1] is the end index
	 *  if there isn't a corresponding [2n+1] index, then that means the string literal was malformed.
	 * @throws Exception 
	 */
	public static Vector<Integer> getStringLiterals(String expression) throws Exception
	{
		Vector<Integer> retVal = new Vector<Integer>();
		boolean lastCharIsBackslash = false;
		boolean insideStringLiteral = false;
		
		for(int i = 0; i < expression.length(); i++)
		{
			switch(expression.charAt(i))
			{
				case '\'':
					if(!insideStringLiteral)
					{
						insideStringLiteral = true;
						retVal.add(i);
					}
					else if(insideStringLiteral && !lastCharIsBackslash)
					{
						insideStringLiteral = false;
						retVal.add(i);					
					}
					lastCharIsBackslash = false;
					break;
				case '\\':
					lastCharIsBackslash = true;
					break;
				default:
					lastCharIsBackslash = false;
					break;
			}
		}
		
		if(insideStringLiteral)
		{
			throw new Exception("A string literal in the string, \""+expression+"\" is never closed. A \"'\" is missing.");
		}
		
		return retVal;
	}
	
	
	/**
	 * Gets a list of xpath conditions as separated by AND or OR, in a given xpath expression.
	 * E.g if we have: constraint=". > 0 and . < 10", then the list will have
	 * ". > 0", ". < 10"
	 * 
	 * @param expression the xpath expression.
	 * @return the xpath condition list.
	 * @throws Exception 
	 */
	public static Vector<String> getConditionsOperatorTokens(String expression) throws Exception{

		
		//find out where the string literals are
		Vector<Integer> strLitPos = getStringLiterals(expression);
		

		return extractConditionsOperatorTokens(expression, strLitPos);
	}
	
	
	/**
	 * Use this to see if an index in a string falls within a string literal in the given string
	 * @param index index to check
	 * @param strLitPos a vector of string literals
	 * @return
	 */
	public static boolean isIndexInsideStringLiteral(int index, Vector<Integer> strLitPos)
	{
		for(int i = 0; i < strLitPos.size() / 2; i = i + 2)
		{
			if(strLitPos.get(i) <= index && strLitPos.get(i+1) >= index)
			{
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Gets an xpath condition, starting at a given position in an xpath expression 
	 * and puts it in a list.
	 * 
	 * @param expression the xpath expression.
	 * @param startPos the position, in the expression, from which to start the condition search.
	 * @param list the list of xpath conditions.
	 * @return the position or index from which the next condition search should begin.
	 */
	private static Vector<String> extractConditionsOperatorTokens(String expression, Vector<Integer> StrLitPos){
		
		Vector<String> retVal = new Vector<String>();
		//check for paranthesis, we don't handle those very well either.
		int andPos = -2;
		int orPos = -2;
		int lastCutPosition = 0;
		boolean foundOr = false;
		boolean foundAnd = false;
		
		boolean orPosInStrLit = true;
		boolean andPosInStrLit = true;
		boolean searchedWholeStr = false;
		while(!searchedWholeStr)
		{	
			//look for OR
			while(orPos != -1 && orPosInStrLit)
			{
				int startFromIndex = lastCutPosition;
				if(orPos != -2)
				{
					startFromIndex = orPos+1;
				}
				orPos = expression.toLowerCase().indexOf(XformConstants.CONDITIONS_OPERATOR_TEXT_OR, startFromIndex);				
				orPosInStrLit = isIndexInsideStringLiteral(orPos, StrLitPos);
			}
			
			//look for AND
			while(andPos != -1 && andPosInStrLit)
			{
				int startFromIndex = lastCutPosition;
				if(andPos != -2)
				{
					startFromIndex = andPos+1;
				}
				andPos = expression.toLowerCase().indexOf(XformConstants.CONDITIONS_OPERATOR_TEXT_AND, startFromIndex);
				andPosInStrLit = isIndexInsideStringLiteral(andPos, StrLitPos);
			}
			
			
			//now we want to see which operator came first in the string, or if there was any operator at all
			
			//check for no operator at all
			if(andPos == -1 && orPos == -1)
			{
				//so add the whole thing to the vector
				retVal.add(expression.substring(lastCutPosition, expression.length()));
				searchedWholeStr = true;
			}
			
			//the and came first
			else if (orPos == -1 || (andPos < orPos && andPos != -1))
			{
				//so add everything to the left of the and to the vector
				retVal.add(expression.substring(lastCutPosition, andPos));
				lastCutPosition = andPos + XformConstants.CONDITIONS_OPERATOR_TEXT_AND.length();
				foundAnd = true;
			}
			else // or came first
			{
				//so add everything to the left of the or to the vector
				retVal.add(expression.substring(lastCutPosition, orPos));
				lastCutPosition = orPos + XformConstants.CONDITIONS_OPERATOR_TEXT_OR.length();
				foundOr = true;
			}
			
			andPos = -2;
			orPos = -2;
			orPosInStrLit = true;
			andPosInStrLit = true;
			
		}
		
		if(foundAnd && foundOr)
		{
			return null;
		}
		
		
		return retVal;
	}

}
