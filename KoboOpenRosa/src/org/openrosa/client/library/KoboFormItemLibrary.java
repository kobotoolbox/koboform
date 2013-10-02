package org.openrosa.client.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.IFormElement;
import org.openrosa.client.model.QuestionDef;
import org.openrosa.client.util.FormHandler;
import org.purc.purcforms.client.locale.LocaleText;


/**
 * The Library of form items
 * @author etherton
 *
 */
public class KoboFormItemLibrary {
	
	/** A list of all the items */
	private ArrayList<KoboFormItem> items = null;
	
	/** A map of tags and the items that have the given tags */	
	private HashMap<String, ArrayList<KoboFormItem>> tags = null;
	
	/** A map of types and the items that have the given types */
	private HashMap<KoboItemTypes, ArrayList<KoboFormItem>> types = null;
	
	/** maps data types by int, to data types strings*/
	private String[] subTypes = {"null", //0
		LocaleText.get("qtnTypeText"), //1
		LocaleText.get("qtnTypeNumber"), //2
		LocaleText.get("qtnTypeDecimal"),//3
		LocaleText.get("qtnTypeDate"),//4
		LocaleText.get("qtnTypeTime"),//5
		LocaleText.get("qtnTypeSingleSelect"),//6
		LocaleText.get("qtnTypeMultSelect"),//7
		LocaleText.get("qtnTypeDateTime"),//8
		LocaleText.get("qtnTypeBoolean"),//9		
		LocaleText.get("qtnTypeRepeat"),//10
		LocaleText.get("qtnTypePicture"),//11
		LocaleText.get("qtnTypeVideo"),//12
		LocaleText.get("qtnTypeAudio"),//13
		"blank",//14
		LocaleText.get("qtnTypeGPS"),//15
		LocaleText.get("qtnTypeBarcode")};//16
	
	
	/**
	 * This method initialize everything
	 */
	public KoboFormItemLibrary()
	{
		//initialize are lists and maps
		items = new ArrayList<KoboFormItem>();
		tags = new HashMap<String, ArrayList<KoboFormItem>>();
		types = new HashMap<KoboItemTypes, ArrayList<KoboFormItem>>();
				
	}//end init()
	
	/**
	 * Used to insert items into the library
	 * @param kfi
	 */
	public void addItem(KoboFormItem kfi)
	{
		//add to the list of items
		items.add(kfi);
		//add the tags
		for(String tag : kfi.tags)
		{
			//check to make sure the tag exists
			if(!tags.containsKey(tag))
			{
				tags.put(tag, new ArrayList<KoboFormItem>());
			}
			//put the kfi under the given tag
			tags.get(tag).add(kfi);
		}
		//make sure the given type has been initialized
		if(!types.containsKey(kfi.type))
		{
			types.put(kfi.type, new ArrayList<KoboFormItem>());
		}
		//now add the kfi for it's given type
		types.get(kfi.type).add(kfi);
		
		//this is extra, so that we can get questions from blocks
		if(kfi.type == KoboItemTypes.block)
		{
			//load the block
			try
			{
				FormDef form = FormHandler.parseXml(kfi.data);
				//now lets pull all the questions out of this
				List<QuestionDef> questions = getQuestionsFromForm(form.getChildren());
				//set the number of questions
				kfi.numberOfQuestions = questions.size();
				//now add these questions to the library
				for(QuestionDef question : questions)
				{
					KoboFormItem qkfi = new KoboFormItem();
					//set the data
					List<IFormElement> tempQuestionList = new ArrayList<IFormElement>();
					tempQuestionList.add(question);
					qkfi.data = FormHandler.copyQuestionsToNewForm(tempQuestionList, form, new HashMap<String,String>());
					//set the name
					qkfi.name = "*"+kfi.name + "* - " + question.getText();
					//set type
					qkfi.type = KoboItemTypes.questionInBlock;
					//set weight
					qkfi.weight = 5;
					//set tags same as parent
					qkfi.tags = kfi.tags;
					//set the sub type
					qkfi.subType = subTypes[question.getDataType()];
					//now add to the library
					items.add(qkfi);
					//add to the right type
					if(!types.containsKey(KoboItemTypes.question))
					{
						types.put(KoboItemTypes.question, new ArrayList<KoboFormItem>());
					}
					//now add the kfi for it's given type
					types.get(KoboItemTypes.question).add(qkfi);
					//now for tags
					for(String tag : qkfi.tags)
					{
						//check to make sure the tag exists
						if(!tags.containsKey(tag))
						{
							tags.put(tag, new ArrayList<KoboFormItem>());
						}
						//put the kfi under the given tag
						tags.get(tag).add(qkfi);
					}
				}
			}
			catch(Exception e)
			{
				//we just fail silently since we don't want to mess up the
				//process of loading stuff from the library
			}			
		}//end special stuff if it's a block
		else if (kfi.type == KoboItemTypes.question)
		{
			try
			{
				FormDef form = FormHandler.parseXml(kfi.data);
				IFormElement element = form.getChildAt(0);
				if(element instanceof QuestionDef)
				{
					QuestionDef q = (QuestionDef)element;
					kfi.subType = subTypes[q.getDataType()];
				}
			}
			catch(Exception e)
			{
				//we just fail silently since we don't want to mess up the
				//process of loading stuff from the library
			}	
		}
		
	}//end addItem();
	
	/**
	 * Used to get the questions out of a form
	 * @param elements
	 * @return
	 */
	private List<QuestionDef> getQuestionsFromForm(List<IFormElement> elements)
	{
		List<QuestionDef> retVal = new ArrayList<QuestionDef>();
		
		//loop over the elements
		for(IFormElement element : elements)
		{
			if(element instanceof QuestionDef)
			{
				retVal.add((QuestionDef)element);
				//recurse
				retVal.addAll(getQuestionsFromForm(element.getChildren()));
			}
		}
		
		return retVal;
	}
	
	/**
	 * This uses tags to search for form items
	 * @param tags
	 * @return
	 */
	public ArrayList<KoboFormItem> Query(ArrayList<String> searchTerms, ArrayList<KoboItemTypes> searchTypes, boolean isAnd)
	{

		ArrayList<KoboFormItem> retVal = new ArrayList<KoboFormItem>();
		ArrayList<KoboFormItem> retVal0 = new ArrayList<KoboFormItem>();
		//loop over the types
		for(KoboItemTypes type : searchTypes)
		{
			if(types.containsKey(type))
			{
				retVal0.addAll(types.get(type));
			}
		}
		//if search terms is empty, then assume they want to see everything
		if(searchTerms.size() == 0)
		{
			//sort our results according to the way they were added
			for(int i = 0; i < items.size(); i++)
			{
				if(retVal0.contains(items.get(i)))
				{
					retVal.add(items.get(i));
				}
			}
			return retVal;
		}
		
		//loop over tags
		for(KoboFormItem kfi : retVal0)
		{
			//if we're anding our terms
			if(isAnd)
			{
				//if the terms are in the tag, add it
				if(kfi.hasTagsAnd(searchTerms))
				{
					retVal.add(kfi);
				}
				else
				{ //if the terms aren't in the tag, see if they're in the name
					if(kfi.nameHasTermsAnd(searchTerms))
					{
						retVal.add(kfi);
					}
				}
			}
			else //if we're oring our terms
			{
				//if the terms are in the tags, add them
				if(kfi.hasTagsOr(searchTerms))
				{
					retVal.add(kfi);
				}
				else
				{//if the terms aren't in the tag, see if they're in the name
					if(kfi.nameHasTermsOr(searchTerms))
					{
						retVal.add(kfi);
					}
				}
			}
			
		}
		
		//sort our results according to the way they were added
		retVal0.clear();
		for(int i = 0; i < items.size(); i++)
		{
			if(retVal.contains(items.get(i)))
			{
				retVal0.add(items.get(i));
			}
		}
		return retVal0;
		
	}//end query() method
	
}
