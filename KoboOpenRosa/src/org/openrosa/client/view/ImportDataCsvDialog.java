package org.openrosa.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openrosa.client.controller.FormDesignerController;
import org.openrosa.client.controller.IFormUIListener;
import org.openrosa.client.model.DataDef;
import org.openrosa.client.model.DataDefBase;
import org.openrosa.client.model.DataInstanceDef;
import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.IFormElement;
import org.openrosa.client.model.ItemSetDef;
import org.openrosa.client.model.PredicateDef;
import org.openrosa.client.model.QuestionDef;
import org.openrosa.client.util.CSVParser;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * This is ripped off from org.purc.purcforms.client.view.LoginDialog
 * This dialog will be used for importing cascading questions data
 * 
 * @author John Etherton
 *
 */
public class ImportDataCsvDialog extends DialogBox {

	private FlexTable table = new FlexTable();
	
	/** lets us know how things are going**/
	private Label statusLabel = null;

	/** Gives the user a heads up on what's about to happen**/
	private Label helpLabel = null;
	
	/** Text box for the CSV */
	private TextArea csvTextArea = null;
	
	/** Text box for the name of the dataset*/
	private TextBox dataSetName = null;
	
	/** The form that we're adding this stuff to **/
	private FormDef formDef = null;
	
	/** To let the UIs know what's happening*/
	FormDesignerController formDesignerController;
	
	/**
	 * Creates a new instance of the login dialog box.
	 */
	public ImportDataCsvDialog(FormDef formDef, FormDesignerController formDesignerController){
		this.formDesignerController = formDesignerController;
		this.formDef = formDef;
		setup();
	}

	
	/**
	 * Sets up the login widget.
	 */
	private void setup(){
		
		//setup the help label
		helpLabel = new Label();
		HTML helpHtml = new HTML();
		helpLabel.setText(LocaleText.get("cascadeHelp"));
		helpHtml.setHTML(LocaleText.get("cascadeHelp"));
		helpLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	
		table.setWidget(0,0, helpHtml);
		table.getFlexCellFormatter().setColSpan(0,0,2);
		
		//create the text box for copy and pasting CSV data
		csvTextArea = new TextArea();
		csvTextArea.setVisibleLines(20);
		csvTextArea.setCharacterWidth(60);
		table.setWidget(1, 0, csvTextArea);
		table.getFlexCellFormatter().setColSpan(1, 0, 2);
		FormUtil.maximizeWidget(csvTextArea);
		
		
		dataSetName = new TextBox();
		table.setWidget(3, 0, new Label(LocaleText.get("dataSetName")));
		table.setWidget(3, 1, dataSetName);
		
		//create the button for creating the questions
		Button btnCreateQuestions = new Button(LocaleText.get("createCascadingQuestions"), new ClickHandler(){
			public void onClick(ClickEvent event){
				createCascadingQuestions();
			}
		});		
		table.setWidget(4, 0, btnCreateQuestions);

		//setup the cancel button
		Button btnCancel = new Button(LocaleText.get("cancel"), new ClickHandler(){
			public void onClick(ClickEvent event){
				cancel();
			}
		});
		table.setWidget(4, 1, btnCancel);
		
		
		//set up the title of the dialog
		setText(LocaleText.get("cascadeDialogTitle"));
		statusLabel = new Label();
		statusLabel.setText(LocaleText.get("awaitingInput"));
		table.setWidget(5, 0, statusLabel);
		
		
		
		//setup the rest of the dialog
		VerticalPanel panel = new VerticalPanel();
		FormUtil.maximizeWidget(panel);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.add(table);
		
		setWidget(panel);
		
		setWidth("500px");

		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				if(csvTextArea != null)
				{
					csvTextArea.setFocus(true);
					
				}
			}
		});
	}
	
	/**
	 * Called when one selects the save button.
	 */
	private void createCascadingQuestions(){
		
		//So we know what was selected when the user started
		List<IFormElement> currentlySelected = formDesignerController.getSelectedItems();
		
		if(dataSetName.getText().length() < 1)
		{
			Window.alert(LocaleText.get("dataSetNameRequired"));
			return;
		}
		
		try
		{
			String line = csvTextArea.getText();			
			HashMap<Integer,HashMap<Integer, String>> csvData = CSVParser.parse(line);
			//get the headers in the first row
			HashMap<Integer, String> headerRow = csvData.get(0);

			//create data instance 
			String dataSetId = PropertiesView.removeShadyCharacters(dataSetName.getText());
			
			//is that a unique name
			if(formDef.getDataInstance(dataSetId) != null)
			{
				Window.alert(LocaleText.get("dataIDNotUnqiue"));
				return;
			}
			
			int[] elementsAtLevels = new int[headerRow.size()];
			
			DataInstanceDef did = new DataInstanceDef(dataSetId, dataSetId, formDef);
			
			//now loop through the data and create data elements
			for(int i = 1; i < csvData.size(); i++)
			{
				HashMap<Integer,String> row = csvData.get(i);
				DataDefBase currentDataParent = did;
				for(int k = 0; k < row.size(); k++)
				{
					String name = PropertiesView.removeShadyCharacters(headerRow.get(k));
					String label = row.get(k);
					
					//check and see if there already an entry with this label
					boolean found = false;
					List<IFormElement> kids = currentDataParent.getChildren();
					for(IFormElement child : kids)
					{
						if(! (child instanceof DataDef))
						{
							return; //something is wrong
						}
						DataDef kid = (DataDef)child;
						if(kid.getAttributeValue("label").equals(label))
						{
							currentDataParent = kid;
							found = true;
							break;
						}
					}
					//if we didn't find it, then make it
					if(!found)
					{
						elementsAtLevels[k] = elementsAtLevels[k] + 1; 
						DataDef kid = new DataDef(name, currentDataParent);
						kid.addAttribute("label", label);
						kid.addAttribute("value", "" + elementsAtLevels[k]);
						currentDataParent.addChild(kid);
						currentDataParent = kid;
					}

				}//end loop over columns
			}//end loop over rows.
			
			//no we want to add the data instance def
			formDesignerController.addDataInstance(did);
			
			List<QuestionDef> questions = new ArrayList<QuestionDef>();
			
			
			
			//now we want to add the questions for this data
			for(int k = 0; k < headerRow.size(); k++)
			{
				//reset the originally selected items
				if(currentlySelected.size() > 0)
				{
					formDesignerController.setItemAsSelected(currentlySelected.get(0));
				}
				
				//create the question
				QuestionDef questionDef = (QuestionDef)formDesignerController.addNewQuestion(QuestionDef.QTN_TYPE_LIST_EXCLUSIVE);
				//questionDef.setBinding(PropertiesView.removeShadyCharacters(headerRow.get(k)));
				questionDef.setText(headerRow.get(k));								
				questionDef.setItextId(questionDef.getBinding());

				//store this for use later
				questions.add(questionDef);
				//create the itemsSet
				ItemSetDef itemSet = new ItemSetDef("value", "label",formDef, questionDef);
				
				//now create the nodetSet for the itemSet, start by adding the instance
				itemSet.addNodeSet(did);
				
				//now go back through all the nodes we used to get here
				for(int j = 0; j <= k; j++ )
				{
					itemSet.addNodeSet(PropertiesView.removeShadyCharacters(headerRow.get(j)));
					//if there's more stuff to come, then we need a predicate
					if(j < k)
					{
						PredicateDef predicate = new PredicateDef("value", questions.get(j), null, itemSet);
						String temp = predicate.toString();
						itemSet.addNodeSet(predicate);
					}
				}
				
				questionDef.getChildren().clear();
				questionDef.addChild(itemSet);
				
				formDesignerController.onFormItemChanged(questionDef);
				formDesignerController.setItemAsSelected(questionDef);
				
			}
			
		}//end try
		catch(Exception e) // in case there's an error with storage
		{
			statusLabel.setText(e.getMessage());
			DOM.setStyleAttribute(statusLabel.getElement(), "color", "red");
		}
		
		hide();
	}
	
	
	
	
	
	
	/**
	 * Called when the user selects the CANCEL button.
	 */
	private void cancel(){
		hide();
	}
	
		
	/**
	 * Displays the dialog box at the center of the browser window.
	 */
	public void center(){
		
		//If there is any progress dialog box, close it.
		FormUtil.dlg.hide();
		
		//Let the base GWT implementation of centering take control.
		super.center();
		
		//Some how focus will not get to the user name unless when called within
		//a deffered command.		
		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				if(csvTextArea != null)
				{
					csvTextArea.setFocus(true);
				}
			}
		});
	}
}
