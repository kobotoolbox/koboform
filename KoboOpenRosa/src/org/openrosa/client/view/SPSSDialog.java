package org.openrosa.client.view;

import java.util.ArrayList;
import java.util.List;

import org.openrosa.client.Context;
import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.IFormElement;
import org.openrosa.client.model.ItextModel;
import org.openrosa.client.model.OptionDef;
import org.openrosa.client.model.QuestionDef;
import org.openrosa.client.util.FormHandler;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.Locale;
import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is ripped off from org.purc.purcforms.client.view.LoginDialog
 * This login dialog will let our users log themselves in with out
 * having to be online by using HTML5 storage. Pretty cool huh
 * 
 * @author daniel
 *
 */
public class SPSSDialog extends DialogBox {


	/** The widget for organizing widgets in a table format. */
	public FlexTable table = new FlexTable();
	
	/** lets us know how things are going**/
	private Label statusLabel;
		
	/** The widget for acquiring the weights from the user*/
	private ListBox listBoxLanguages = new ListBox(false);

	/** Used to store the spssText until we get around to wiring this up to the save to file stuff*/
	private TextArea spssText = new TextArea(); 
	
	/**The form we're creating the SPSS file for*/
	private FormDef form;
	
	/** To keep track of this dialog when we're writing things to a file*/
	private static SPSSDialog instance = null;
	
	/** Used to hold the applet*/
	private HTML appletHtml = new HTML();
	
	/**
	 * The classic default constructor, so elegant, so simple.
	 */
	private SPSSDialog(FormDef form)
	{
		this.form = form;
		setup();
	}
	
	/**
	 * Grab a copy of the instance
	 * @param form the form we're making the SPSS file for
	 * @return the dialog
	 */
	public static SPSSDialog getNewInstance(FormDef form)
	{
		instance = new SPSSDialog(form);
		return instance;
	}
	
	/**
	 * Return the current instance
	 * @return
	 */
	public static SPSSDialog getCurrentInstance()
	{
		return instance;
	}

	
	/**
	 * Sets up the login widget.
	 */
	private void setup(){
		
		//set the dialog title
		setText(LocaleText.get("createSPSSFileDialog"));
		//create the status text
		statusLabel = new Label();
		
		//create the buttons
		Button btnCancel = new Button(LocaleText.get("close"), new ClickHandler(){
			public void onClick(ClickEvent event){
				cancel();
			}
		});
		
		Button btnCreate = new Button(LocaleText.get("createSPSSFileDialog"), new ClickHandler(){
			public void onClick(ClickEvent event){
				createSPSSFile();
			}
		});
		
		Button btnSave = new Button(LocaleText.get("save"), new ClickHandler(){
			public void onClick(ClickEvent event){
				save();
			}
		});
		
				
		
		//update the languages in the form.
		FormHandler.updateLanguage(Context.getLocale(), Context.getLocale(), form);
		//add the languages to the language drop down
		for(Locale l : form.getLocales())
		{
			listBoxLanguages.addItem(l.getName());
		}
		
		//setup the text area
		spssText.setCharacterWidth(30);
		spssText.setPixelSize(300, 300);
		
		
		FlexCellFormatter formatter = table.getFlexCellFormatter();
		
		//now add stuff to the UI
		int row = 0;
		table.setWidget(row, 0, new Label(LocaleText.get("language")));
		table.setWidget(row, 1, listBoxLanguages);
		row++;
		table.setWidget(row, 0, spssText);
		formatter.setColSpan(row, 0, 2);
		formatter.setHeight(row, 0, "300px");
		formatter.setWidth(row, 0, "300px");
		row++;
		table.setWidget(row, 0, statusLabel);
		row++;
		table.setWidget(row, 0, btnCreate);
		row++;
		table.setWidget(row, 0, btnSave);
		table.setWidget(row, 1, btnCancel);
		row++;
		table.setWidget(row, 0, appletHtml);
		
						
		//some random UI stuff to make everything nice and neat
		VerticalPanel panel = new VerticalPanel();
		FormUtil.maximizeWidget(panel);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.add(table);
		
		setWidget(panel);
		
		setWidth("200px");		
	}
	
	/**
	 * Method to save stuff 
	 */
	private void createSPSSFile()
	{
		//init some variables
		String variablesText = "VARIABLE LABELS\r\n";
		String valuesText = "\r\nVALUE LABELS\r\n";
		int line = 1;
		
		//What weight was selected
		String language = listBoxLanguages.getItemText(listBoxLanguages.getSelectedIndex());
		//get a list of questions
		List<QuestionDef> questions = getQuestions(form.getChildren());
		//loop over the questions
		for(QuestionDef q : questions)
		{
			String questionName = "";
			if(form.getITextMap().containsKey(q.getBinding()))
			{
				ItextModel iText = form.getITextMap().get(q.getBinding());
				questionName = iText.get(language);
			}
			else
			{
				questionName = q.getText();
			}
			
			questionName = questionName.replace("\"", "\"\"");
			//if it's too long truncate
			if(questionName.length() > 120)
			{
				questionName = questionName.substring(0, 116) + "...";
			}
			//do we need to add the /
			if(line > 1)
			{
				variablesText += "/";
			}
			//define the variable label
			variablesText += q.getBinding() + " \""  + questionName + "\"\r\n";
			//advance our line count
			line++;
			//now handle the values
			//is this a select question?
			if(q.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE || q.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE)
			{
				String values = "/" + q.getBinding();
				//loop over the kids
				if(q.getOptions() instanceof List<?>)
				{
					List<Object> list = (List<Object>)(q.getOptions());
					for(Object o : list)
					{
						if(!(o instanceof OptionDef))
						{
							continue;
						}
						OptionDef option = (OptionDef)o;
						//make sure the value is in there and fail gracefully
						if(!form.getITextMap().containsKey(option.getBinding()))
						{
							continue;
						}
						String optionName = form.getITextMap().get(option.getBinding()).get(language);
						optionName = optionName.replace("\"", "\"\"");
						if(optionName.length() > 120)
						{
							optionName = optionName.substring(0, 116) + "...";
						}
						values += " " + option.getDefaultValue() + " \"" + optionName + "\"";
					}
				}
				//make sure there's something there
				if(!values.equals("/" + questionName))
				{
					values += "\r\n";
					valuesText += values;
				}
			}
			//loop ov
		}
		//now we'll define the values
		
		spssText.setText(variablesText + valuesText);
	}
	
	/**
	 * Used to recursively get a list of questions from a form.
	 * @param elements
	 * @return
	 */
	private List<QuestionDef> getQuestions(List<IFormElement> elements)
	{
		//define the return value
		ArrayList<QuestionDef> questions = new ArrayList<QuestionDef>();
		//loop over elements
		if(elements == null)
		{
			return questions;
		}
		for(IFormElement element: elements)
		{
			//is it a question
			if(element instanceof QuestionDef)
			{
				questions.add((QuestionDef)element);
			}
			//recurse
			questions.addAll(getQuestions(element.getChildren()));
		}
		
		return questions;
	}
	
	
	
	
	
	public static native void saveToJS(String xml)/*-{
		$wnd.formXmlToSave = xml;
	}-*/;
	
	
	
	
	
	/**
	 * Called when the user selects the CANCEL button.
	 */
	private void cancel(){
		SPSSDialog.instance = null;
		hide();
	}
	
	/**
	 * Used to save the contents of the text area to file
	 */
	private void save()
	{
		//get the text
		String spssTxt = spssText.getText();
		//save it to a JS place where Java can find it
		saveToJS(spssTxt);
		//make up a file name
		String fileName = form.getText().replace(" ", "_").replace("\\", "").replace("/","").replace("*", "").replace("\"", "")
				.replace("<", "").replace(">", "").replace("#", "").replace("'", "") + ".spss";		
		
		
		
		appletHtml.setHTML("<APPLET codebase=\"fileioapplets/\"+" +
				" archive=\"kobo_fileIOApplets.jar, plugin.jar\" "+
				" code=\"org.oyrm.kobo.fileIOApplets.ui.FileSaveApplet.class\" "+
				" width=\"5\" HEIGHT=\"5\" MAYSCRIPT>  "+
				"<param name=\"formName\" value=\""+fileName+"\"/>"+
				"<param name=\"save\" value=\""+LocaleText.get("SaveSPSSFile")+"\"/>"+
				"</APPLET>");
	}
	
		
	/**
	 * Displays the dialog box at the center of the browser window.
	 */
	public void center(){
		
		//If there is any progress dialog box, close it.
		FormUtil.dlg.hide();
		
		//Let the base GWT implementation of centering take control.
		super.center();
		
	}
}
