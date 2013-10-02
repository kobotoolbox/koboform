package org.openrosa.client.library;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.purc.purcforms.client.xforms.XmlUtil;
import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;

/**
 * Used to hold Kobo Form Items
 * Special thanks to totheriver.com for writing this article on XML parsing that I used to make this class
 * http://www.java-samples.com/showtutorial.php?tutorialid=152
 * @author etherton
 *
 */
public class KoboFormItem {

	/** What type of item is this*/
	public KoboItemTypes type;
	
	/** What's the weight of this item*/
	public int weight;
	
	/** The name of this item*/
	public String name;
	
	/**an array of tags**/
	public ArrayList<String> tags = new ArrayList<String>();
	
	/**The form data*/
	public String data;
	
	/**The number of questions in this item*/
	public int numberOfQuestions = 1;
	
	/**Sub type, only applies to questions*/
	public String subType = "N/A";
	
	/**
	 * Default constructor
	 */
	public KoboFormItem()
	{
		
	}
	
	/**
	 * Constructor from raw data types
	 * @param type
	 * @param weight
	 * @param name
	 * @param tags
	 * @param data
	 */
	public KoboFormItem(String type, int weight, String name, ArrayList<String> tags, String data)
	{
		this.type = KoboItemTypes.fromString(type);
		this.weight = weight;
		this.name = name;
		this.tags = tags;
		this.data = data;
	}
	
	/**
	 * Constructor that reads from XML string
	 * @param xml the string to read from
	 * @throws Exception throws errors if the XML is no good
	 */
	public KoboFormItem(String xml) throws Exception
	{
		readFromXML(xml);
	}
	
	/**
	 * parses XML
	 * @param xml the xml to parse
	 */
	public void readFromXML(String xml) throws Exception
	{
		
		Document doc = XmlUtil.getDocument(xml);
		Element el = doc.getDocumentElement();
	
		if(el == null)
		{
			throw new Exception("Could not find \"kobo_item\" element in file. File is not a valid Kobo Form Item");
		}
		
		String name = getTextValue(el, "name");
		
		String type = el.getAttribute("type");
		
		int weight = Integer.parseInt(el.getAttribute("weight"));
		
		String data = getTextValue(el, "item_data");
		
		ArrayList<String> tags = new ArrayList<String>();
		//parse the tags
		NodeList tagNodelist = el.getElementsByTagName("tag");
		for(int i = 0; i < tagNodelist.getLength(); i++)
		{
			Element tagElement = (Element)tagNodelist.item(i);
			String tag = tagElement.getFirstChild().getNodeValue();
			tags.add(tag);
		}
		
		this.type = KoboItemTypes.fromString(type);
		this.weight = weight;
		this.name = name;
		this.tags = tags;
		this.data = data;
		

	}//end contructor from file
	
	
	/**
	 * I take a xml element and the tag name, look for the tag and get
	 * the text content
	 * i.e for <employee><name>John</name></employee> xml snippet if
	 * the Element points to employee node and tagName is 'name' I will return John
	 */
	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}


	/**
	 * Used to see if an item has the given tag
	 * This is a soft search that just looks to see if part of the
	 * string is in there
	 * @param tag
	 * @return
	 */
	public boolean hasTag(String tag)
	{
		for(String t : tags)
		{
			if(t.toLowerCase().indexOf(tag.toLowerCase()) != -1)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Used to figure out if the object has any one of the given tags
	 * @param tags
	 * @return
	 */
	public boolean hasTagsOr(ArrayList<String> tags)
	{
		for(String tag : tags)
		{
			if(hasTag(tag))
			{
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Used to figure out if the object has all of the given tags
	 * @param tags
	 * @return
	 */
	public boolean hasTagsAnd(ArrayList<String> tags)
	{
		for(String tag : tags)
		{
			if(!hasTag(tag))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Gets a comma separated list of strings
	 * @return
	 */
	public String getTagString()
	{
		int i = 0;
		String retVal = "";
		//populate the tags
		for(String t : tags)
		{
			i++;
			if(i > 1)
			{
				retVal += ",  ";
			}
			retVal += t;
		}
		return retVal;
	}
	
	/**
	 * Generates the search key
	 * @return
	 */
	public String getSearchKey()
	{
		return name + "\t" + type.toString() + "\t" + getTagString();
	}
	
	/**
	 * a toString method for easier reading
	 */
	public String toString()
	{
		return getSearchKey();
	}
	
	/**
	 * Check and sees if all of the terms are in the name
	 * @param terms the terms we're searching on
	 * @return true if they're all there, false otherwise
	 */
	public boolean nameHasTermsAnd(ArrayList<String> terms)
	{
		//loop over all the terms
		for(String term : terms)
		{
			//if it's not here, it's false
			if(name.toLowerCase().indexOf(term.toLowerCase()) == -1)
			{
				return false;
			}
		}
		//if we made it this far it's true.
		return true;
	}//end nameHasTermsAnd
	
	/**
	 * Check and sees if at least one of the terms are in the name
	 * @param terms the terms we're searching on
	 * @return true if just one is there, false otherwise
	 */
	public boolean nameHasTermsOr(ArrayList<String> terms)
	{
		//loop over all the terms
		for(String term : terms)
		{
			//if just one is here then it's a match
			if(name.toLowerCase().indexOf(term.toLowerCase()) != -1)
			{
				return true;
			}
		}
		//if we made it this far then nothing matched
		return false;
	}//end nameHasTermsOr


}

