package org.openrosa.client.util;

import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.xml.client.Document;

/**
 * A simple class that'll make it easier for me to debug XML documents by using the pretty print for the toString method
 * @author etherton
 *
 */
public class DebugDoc {

	/**
	 * The doc in question
	 */
	private Document doc;
	
	public DebugDoc(Document doc)
	{
		this.doc = doc;
	}
	
	
	public String toString()
	{
		return FormUtil.formatXml(doc.toString());
	}
	
	
	
}
