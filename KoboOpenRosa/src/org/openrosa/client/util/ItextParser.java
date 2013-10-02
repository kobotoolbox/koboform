package org.openrosa.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openrosa.client.Context;
import org.openrosa.client.model.ItextModel;
import org.purc.purcforms.client.model.Locale;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.util.LanguageUtil;
import org.purc.purcforms.client.xforms.XmlUtil;

import com.extjs.gxt.ui.client.store.ListStore;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;


/**
 * Parses an xforms document and puts text in all nodes referencing an itext block for a given language.
 * The first language in the itext block is taken to be the current or default one.
 * 
 * @author daniel
 *
 */
public class ItextParser {	

	/**
	 * key could be "baseball" and list is {image|jr://images/baseball.gif, long|Baseball, short|bball}
	 */
	public static HashMap<String,List<String>> itextFormAttrList = new HashMap<String,List<String>>();

	/**
	 * A map of locale doc's xform node keyed by the locale key.
	 */
	private static HashMap<String, Element> localeXformNodeMap = new HashMap<String, Element>();
	
	
	/**
	 * Use this instance if you want to parse the iText and set the global language settings for you
	 * @param xml
	 * @param list
	 * @param formAttrMap
	 * @param itextMap
	 * @return
	 */
	public static Document parse(String xml, ListStore<ItextModel> list, HashMap<String,String> formAttrMap, 
			HashMap<String,ItextModel> itextMap){
		
		List<Locale> locales = new ArrayList<Locale>(); //New list of locals as it comes form the parsed xform.
		Locale defaultLocale = new Locale(null, null);
		HashMap<String,String> map = new HashMap<String, String>();
		
		Document doc = ItextParser.parse(xml, list, formAttrMap, itextMap, locales, defaultLocale, map);
		//set the default locale
		Context.setLocale(defaultLocale);
		Context.setDefaultLocale(Context.getLocale());
		//set the list of locals
		Context.setLocales(locales);
		
		//set this, whatever it does
		Context.getLanguageText().put(1, map);
		return doc;
	}
	

	/**
	 * Parses an xform and sets the text of various nodes based on the current a locale
	 * as represented by their itext ids. The translation element with the "default" attribute (default="") OR the first locale in the itext block
	 * (if no default attribute is found) is the one taken as the default.
	 * 
	 * @param xml the xforms xml.
	 * @param list the itext model which can be displayed in a gxt grid.
	 * @return the document where all itext refs are filled with text for a given locale.
	 */
	public static Document parse(String xml, ListStore<ItextModel> list, HashMap<String,String> formAttrMap, 
			HashMap<String,ItextModel> itextMap, List<Locale> locales, Locale defaultLocale,
			HashMap<String,String> map){
		
		
		localeXformNodeMap.clear();

		Document doc = XmlUtil.getDocument(xml);

		//Check if we have an itext block in this xform.
		NodeList nodes = doc.getElementsByTagName("itext");
		if(nodes == null || nodes.getLength() == 0)
			return doc;

		//Check if we have any translations in this itext block.
		nodes = ((Element)nodes.item(0)).getElementsByTagName("translation");
		if(nodes == null || nodes.getLength() == 0)
			return doc;

		//etherton remove this if not needed
		//List<Locale> locales = new ArrayList<Locale>(); //New list of locales as it comes form the parsed xform.
		HashMap<String,String> defaultText = null; //Map of default id and itext (for multiple values of the itext node) for the default language.

		//Map of each locale key and map of its id and itext translations.
		HashMap<String, HashMap<String,String>> translations = new HashMap<String, HashMap<String,String>>();
		//loop over the XML elements
		for(int index = 0; index < nodes.getLength(); index++){
			//get the current node
			Element translationNode = (Element)nodes.item(index);
			HashMap<String,String> itext = new HashMap<String,String>();
			HashMap<String, String> defText = new HashMap<String,String>();
			//what language is this translation for?
			String lang = translationNode.getAttribute("lang");
			//add this new language to our map of languages
			translations.put(lang, itext);
			//Drill down.
			fillItextMap(translationNode,itext,defText,lang,list,formAttrMap,itextMap);

			//an element with attribute "default" is selected as the default languages, otherwise just use the first one.
			if( ((Element)nodes.item(index)).getAttribute("default") != null || index == 0){
				defaultText = defText;
				//Context.setLocale(new Locale(lang,lang));
				//Context.setDefaultLocale(Context.getLocale());
				defaultLocale.setKey(lang);
				defaultLocale.setName(lang);				
			}

			//create a new locale object for the current translation.
			locales.add(new Locale(lang,lang));
		}

		//set the list of locales in the global list
		//ETHERTON THIS GETS TAKEN OUT
		//Context.setLocales(locales);


		//create a hash table of locale xform nodes keyed by locale.
		for(Locale locale : locales){
			//create a temp doc
			Document localeDoc = XMLParser.createDocument();
			//create a node in that tmep doc
			Element node = localeDoc.createElement(LanguageUtil.NODE_NAME_LANGUAGE_TEXT);
			//set the "lang" attribute to the name of the current locale
			node.setAttribute("lang", locale.getName());
			//add that node to the doc
			localeDoc.appendChild(node);

			//create a new element 
			Element localeXformNode = localeDoc.createElement(LanguageUtil.NODE_NAME_XFORM);
			//add that new element to the lang node from above
			node.appendChild(localeXformNode);

			//add the new element to this hashmap of local names and XML nodes
			localeXformNodeMap.put(locale.getName(), localeXformNode);
		}


		/*
		tranlateNodes("label", doc, defaultText, list, Context.getLocale().getName(),itextMap); //getKey()??????
		tranlateNodes("hint", doc, defaultText, list, Context.getLocale().getName(),itextMap); //getKey()??????
		tranlateNodes("title", doc, defaultText, list, Context.getLocale().getName(),itextMap); //getKey()??????
		*/
		tranlateNodes("label", doc, defaultText, list, defaultLocale.getName(),itextMap); //getKey()??????
		tranlateNodes("hint", doc, defaultText, list, defaultLocale.getName(),itextMap); //getKey()??????
		tranlateNodes("title", doc, defaultText, list, defaultLocale.getName(),itextMap); //getKey()??????

		//clear the language text double hashmap, not sure what this does
		//Context.getLanguageText().clear();


		//create a hashmap
		//HashMap<String,String> map = new HashMap<String,String>();
		//loop over locals
		for(Locale locale : locales){
			//get the element we just put into the localeXformNodeMap hashmap out accourding to a locale's name
			Element localeXformNode = localeXformNodeMap.get(locale.getName());
			//now put that into the new hashmap, but turn the node into a string representation of get owner 
			map.put(locale.getName(), localeXformNode.getOwnerDocument().toString());
		}

		//TODO Will the form id always be 1?
		//Context.getLanguageText().put(1, map);

		return doc;
	}


	/**
	 * Fills a map of id and itext for a given locale as represented by a given translation node.
	 * 
	 * @param translationNode the translation node.
	 * @param itext the itext map.
	 */
	private static void fillItextMap(Element translationNode, HashMap<String,String> itext, HashMap<String,String> defaultText, String localeKey, ListStore<ItextModel> list, HashMap<String,String> formAttrMap, HashMap<String,ItextModel> itextMap){
		//get all the children of the given translation, the nodes that contain the translation for a given id
		NodeList nodes = translationNode.getChildNodes();
		//now loop over those children
		for(int index = 0; index < nodes.getLength(); index++)
		{
			//get the current child
			Node textNode = nodes.item(index);
			//skip it if it isn't an element node
			if(textNode.getNodeType() != Node.ELEMENT_NODE)
				continue;
			//call the setValueText method to 
			setValueText(itext,((Element)textNode).getAttribute("id"), textNode, defaultText,localeKey,list,formAttrMap,itextMap);
		}

	}


	/**
	 * Gets the text value of a node.
	 * 
	 * @param textNode the node.
	 * @return the text value.
	 */
	private static void setValueText(HashMap<String,String> itext, String id, Node textNode, HashMap<String,String> defaultText, String localeKey, ListStore<ItextModel> list, HashMap<String,String> formAttrMap, HashMap<String,ItextModel> itextMap){
		//setup some varialbes
		String defaultValue = null, longValue = null, shortValue = null;
		//get the child nodes of the current text node. Should be just one
		NodeList nodes = textNode.getChildNodes();
		//loop over those nodes
		for(int index = 0; index < nodes.getLength(); index++){
			//get the current value node
			Node valueNode = nodes.item(index);
			//make sure it's an element node, skip it otherwise
			if(valueNode.getNodeType() != Node.ELEMENT_NODE)
				continue;

			//get the form attribute, not sure what this is used for 
			String form = ((Element)valueNode).getAttribute("form");
			//get the text of the translation
			String text = XmlUtil.getTextValue(valueNode);
			//if it doesn't equal null
			if(text != null){
				//if the form is null then use the id as the key in itext, otherwise use <id>;<form>
				itext.put(form == null ? id : id + ";" + form, text);
				//figure out if we're dealing with a default, long, or short value and store accordingly
				if(form == null)
					defaultValue = text;
				else if(form.equalsIgnoreCase("long"))
					longValue = text;
				else if(form != null && form.equalsIgnoreCase("short"))
					shortValue = text;
				else
					defaultValue = text;
				//if the form is null then use id, otherwise use <id>;<form?
				String fullId = form == null ? id : id + ";" + form;
				//get the ItextModel for the current id
				ItextModel itextModel = itextMap.get(fullId);
				//if the model is null then add it
				if(itextModel == null){
					itextModel = new ItextModel();
					itextModel.set("id", fullId);
					itextMap.put(fullId, itextModel);
					list.add(itextModel);
				}

				//set the translation for the given language, on the ItextModel that corresponds to the current id
				itextModel.set(localeKey, text);

			}

			//if the form isn't null, then add the form and text to a list of attributes
			if(form != null)
			{
				//get current list of attributes
				List<String> attrList = itextFormAttrList.get(id);
				//if it doesn't exist, create it
				if(attrList == null){
					attrList = new ArrayList<String>();
					itextFormAttrList.put(id, attrList);
				}
				//add this form and text
				attrList.add(form + "|" + text);
			}
		}

		//if long value, then set that in the formAttrMap
		if(longValue != null)
		{
			defaultValue = longValue;
			formAttrMap.put(id, "long");
		}
		//if short value, then set that in the formAttrMap
		else if(shortValue != null)
		{
			defaultValue = shortValue;
			formAttrMap.put(id, "short");
		}
		//set the default in the defaultText hashmap.
		defaultText.put(id, defaultValue);
	}


	/**
	 * For a given xforms document, fills the text of all nodes having a given name with their 
	 * corresponding text based on the itext id in the ref attribute.
	 * 
	 * @param name the name of the nodes to look for.
	 * @param doc the xforms document.
	 * @param itext the id to itext map.
	 * @param list the itext model as required by gxt grids.
	 */
	private static void tranlateNodes(String name, Document doc, HashMap<String,String> itext, ListStore<ItextModel> list, String localeKey, HashMap<String,ItextModel> itextMap){
		//get all the XML nodes that correspond to the given name
		NodeList nodes = doc.getElementsByTagName(name);
		//if there aren't any then leave
		if(nodes == null || nodes.getLength() == 0)
			return;

		//Map for detecting duplicates in itext. eg if id yes=Yes , we should not have information more than once.
		HashMap<String,String> duplicatesMap = new HashMap<String, String>();

		//loop over nodes with the given name
		for(int index = 0; index < nodes.getLength(); index++){
			Element node = (Element)nodes.item(index);
			
			//if the id isn't specified then skip
			String id = getItextId(node);
			if(id == null || id.trim().length() == 0)
				continue;

			String text = itext.get(id);

			//If the text node does not already exist, add it, else just update itx text.
			if(!XmlUtil.setTextNodeValue(node, text))
				node.appendChild(doc.createTextNode(text));

			//Skip the steps below if we have already processed this itext id.
			if(duplicatesMap.containsKey(id))
				continue;
			else
				duplicatesMap.put(id, id);

			Element parentNode = (Element)node.getParentNode();
			String idname = "bind";
			String ref = parentNode.getAttribute("ref");
			if(ref != null)
				idname = "ref";
			else
				ref = parentNode.getAttribute("bind");
			
			if(ref == null){
				ref = parentNode.getAttribute("id");
				if(ref != null)
					idname = "id";
			}

			//Create and add an itext model object as required by the gxt grid.

			ItextModel itextModel = itextMap.get(id);
			if(itextModel == null)
				itextModel = itextMap.get(id+";long");
			if(itextModel == null)
				itextModel = itextMap.get(id+";short");

			if(itextModel == null){
				itextModel = new ItextModel();
				itextMap.put(id, itextModel);
				itextModel.set("id", id);
				list.add(itextModel);
			}

			String xpath = itextModel.get("xpath");
			if(xpath == null){//TODO Check to confirm that this null check does not cause bugs. Meaning do we have to rebuild the xpath everytime?
				xpath = FormUtil.getNodePath(parentNode) + "[@" + idname + "='" + ref /*id*/ + "']" + "/" + name;
				if(ref == null)
					xpath = FormUtil.getNodePath(parentNode) + "/" + name;

				itextModel.set("xpath", xpath);
			}

			//itextModel.set("id", id);
			itextModel.set(localeKey, text);
			//list.add(itextModel);

			for(Locale locale : Context.getLocales()){
				if(!localeXformNodeMap.containsKey(locale.getName()))
				{
					continue;
				}
				Element localeXformNode = localeXformNodeMap.get(locale.getName());
				Element textNode = localeXformNode.getOwnerDocument().createElement("text");
				textNode.setAttribute("xpath", xpath);
				textNode.setAttribute("value", (String)itextModel.get(locale.getName()));
				localeXformNode.appendChild(textNode);
			}
		}
	}


	public static String getItextId(Element node) {		
		//Check if node has a ref attribute.
		String ref = node.getAttribute("ref");
		if(ref == null)
			return null;

		//Check if node has jr:itext value in the ref attribute value.
		int pos = ref.indexOf("jr:itext('");
		if(pos < 0)
			return null;

		//Get the itext id which starts at the 11th character.
		return ref.substring(10,ref.lastIndexOf("'"));
	}
}
