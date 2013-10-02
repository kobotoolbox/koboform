package org.openrosa.client.model;

import java.util.List;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;

/**
 * 
 * @author daniel
 *
 */
public interface IFormElement {


	String getText();
	void setText(String text);
	
	String getHelpText();
	void setHelpText(String helpText);
	
	int getDataType();
	void setDataType(int dataType);
	
	String getBinding();
	void setBinding(String binding);
	
	List<IFormElement> getChildren();
	void setChildren(List<IFormElement> children);
	void addChild(IFormElement element);
	int getIndex();
	int getIndexOfChild(IFormElement element);
	void insert(int index, IFormElement element);
	
	
	
	IFormElement getParent();
	void setParent(IFormElement parent);
	
	
	IFormElement copy(IFormElement parent);
	
	void clearChangeListeners();
	
	
	
	String getItextId();
	void setItextId(String id);
	
	boolean removeChild(IFormElement element);
	
	int getChildCount();
	
	public FormDef getFormDef();
	
	boolean isLocked();
	boolean isRequired();
	boolean isEnabled();
	boolean isVisible();
	String getDefaultValue();
	
	/**
	 * This will be called recusivley through out a form to render the XML
	 * @param doc - the XML document, mainly just there for creating nodes
	 * @param instanceNode - the XML node that stores instance data
	 * @param bindNode - the XML node that stores bind data
	 * @param iTextNode - the XML node that stores iText data
	 * @param UINode - the XML node that stores UI data
	 */
	public void writeToXML(Document doc, Element instanceNode, Element bindNode, List<Element> iTextNodes, Element UINode);
	

	/**
	 * Returns the fully qualified path of an element
	 * @return the fully qualified path of the element in question
	 */
	public String getPath();
}
