package org.openrosa.client.util;

import java.util.HashMap;
import java.util.StringTokenizer;


public class CSVParser {
	
	private static char DELIMITER = ',';
	private static char QUOTE = '"';
	private static String NEW_LINE_CHARS = "\r\n";
	/**
	 * Parses a string of CSV data. This does not account for multi line entries, or 
	 * quotes inside of other quotes, if you want to do that I'd recomend using 
	 * fancy UTF-8 quotes, like so: “, butdoes account for commas inside of strings 
	 * wrapped in double quotes
	 * @param csvSource
	 * @return
	 */
	public static HashMap<Integer, HashMap<Integer, String>> parse(String csvSource)
	{
		//return value
		HashMap<Integer, HashMap<Integer, String>> retVal = new HashMap<Integer, HashMap<Integer, String>>();
		
		//Split on lines
		String[] lines = csvSource.split("\\r?\\n");

		int row = 0;
		//loop over the lines		
		for(String currentLine : lines) 
		{
			HashMap<Integer, String> lineMap = parseLine(currentLine);
	        retVal.put(row, lineMap);
			row++;
		}
		return retVal;
	}
	
	/**
	 * Prases one line of a CSV at a time
	 * @param line one line of a CSV
	 * @return a hash table giving the column index and the value at that index
	 */
	private static HashMap<Integer, String> parseLine(String line)
	{
		int column = 0;
		int currentChar = 0;
		int lastDelim = 0;
		boolean inAQuote = false;
		boolean wasInQuotes = false;
		boolean handledItem = false;
		
		HashMap<Integer, String> lineMap = new HashMap<Integer, String>();
		//System.out.println("Parser: \""+line+"\"");
		//iterate through the line and see what you find
		while(currentChar < line.length())
		{
			handledItem = false;
			//System.out.println("LastDelim: " + lastDelim+" currentChar: " + currentChar + " Character at currChar: "+line.charAt(currentChar)+" length: " + line.length());
			
			//if we see a quote and we're not currently in a quote
			if(line.charAt(currentChar) == QUOTE && !inAQuote && (currentChar == 0 || line.charAt(currentChar-1) == DELIMITER) )
			{
				inAQuote = true;
			}
			else if(line.charAt(currentChar) == QUOTE && inAQuote && ((currentChar < line.length()-1 && line.charAt(currentChar+1) == DELIMITER) || currentChar == line.length()-1))
			{
				inAQuote = false;
				wasInQuotes = true;
			}
			else if(line.charAt(currentChar) == DELIMITER && !inAQuote )
			{
				
				String item;
				if(wasInQuotes)
				{
					item = line.substring(lastDelim+1, currentChar-1);					
				}
				else
				{
					item = line.substring(lastDelim, currentChar);
				}
				lastDelim = currentChar+1;
				lineMap.put(column, item);
				//System.out.println("Column:"+column+" - "+item);
				wasInQuotes = false;
				column++;
				handledItem = true;
			}
			
			//if we're at the end of a line
			if(currentChar == line.length()-1 && !handledItem)
			{
				String item;
				if(wasInQuotes)
				{
					item = line.substring(lastDelim+1, currentChar);					
				}
				else
				{					
					item = line.substring(lastDelim, currentChar+1);
				}
				lastDelim = currentChar+1;
				lineMap.put(column, item);
				//System.out.println("Column:"+column+" - "+item);
				
				column++;
				handledItem = true;
			}
			
			currentChar++;
		}
		
		return lineMap;
	}
	

}
