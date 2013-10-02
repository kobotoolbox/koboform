package org.openrosa.client.widget.skiprule;

import java.util.List;

import org.openrosa.client.controller.FormDesignerController;
import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.GroupDef;
import org.openrosa.client.model.IFormElement;
import org.openrosa.client.model.QuestionDef;
import org.openrosa.client.util.FormDesignerUtil;
import org.purc.purcforms.client.controller.ItemSelectionListener;
import org.purc.purcforms.client.locale.LocaleText;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusListenerAdapter;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


/**
 * This widget is used to let one select a field or question for a skip or validation
 * rule condition. For validation rules, this can be used for the condition value. eg
 * Weight less than Height. For skip rules, this can be used for both the condition
 * question and value.
 * 
 * @author daniel
 *
 */
public class FieldWidget extends Composite{

	/** The text to display when no value is specified for a condition. */
	private static final String EMPTY_VALUE = "_____";

	/** The form to which the question, represented by this widget, belongs. */
	private FormDef formDef;

	/** The main widget. */
	private HorizontalPanel horizontalPanel;

	/** The widget to do auto suggest for form questions as the user types. */
	private SuggestBox sgstField = new SuggestBox();

	/** The text field where to type the question name. */
	private TextBox txtField = new TextBox();

	/** The widget to display the selected question text when not in selection mode. */
	private Hyperlink fieldHyperlink;

	/** The listener for item selection events. */
	private ItemSelectionListener itemSelectionListener;

	/** A flag determining if the current field selection is for a single select dynamic.
	 * type of question.
	 */
	private boolean forDynamicOptions = false;

	/** The single select dynamic question. */
	private QuestionDef dynamicQuestionDef;
	
	//TODO I think we need only one of questionDef or dynamicQuestionDef to serve the same purpose
	/** The question that this field widget is handling. eg the skip logic question. */
	private QuestionDef questionDef;


	public FieldWidget(ItemSelectionListener itemSelectionListener){
		this.itemSelectionListener = itemSelectionListener;
		setupWidgets();
	}

	/**
	 * Sets the form to which the referenced question belongs.
	 * 
	 * @param formDef the form definition object.
	 */
	public void setFormDef(FormDef formDef){
		this.formDef = formDef;
		setupPopup();
	}

	@SuppressWarnings("deprecation")
	private void setupWidgets(){
		fieldHyperlink = new Hyperlink("",""); //Field 1

		horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(fieldHyperlink);

		fieldHyperlink.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				FormDesignerController.makeDirty();
				itemSelectionListener.onStartItemSelection(this);
				horizontalPanel.remove(fieldHyperlink);
				horizontalPanel.add(sgstField);
				sgstField.setText(fieldHyperlink.getText());
				sgstField.setFocus(true);
				txtField.selectAll();
			}
		});

		
		sgstField.addSelectionHandler(new SelectionHandler(){
			public void onSelection(SelectionEvent event){
				stopSelection();
			}
		});

		initWidget(horizontalPanel);
	}

	public void stopSelection(){
		if(horizontalPanel.getWidgetIndex(fieldHyperlink) != -1)
			return;

		String val = sgstField.getText();
		if(val.trim().length() == 0)
			val = EMPTY_VALUE;
		fieldHyperlink.setText(val);
		horizontalPanel.remove(sgstField);
		horizontalPanel.add(fieldHyperlink);
		
		//the old way of doing things using 
		//IFormElement qtn = formDef.getQuestionWithText(txtField.getText());
		
		//figure out the binding
		if(val != EMPTY_VALUE)
		{
			int lastIndexOfParen = val.lastIndexOf(">");
			if(lastIndexOfParen == -1)
			{
				Window.alert("\""+val + "\" " + LocaleText.get("isNotValidQuestion"));
				return;
			}
			String binding = val.substring(0, val.lastIndexOf(">"));
			binding = binding.substring(binding.lastIndexOf("<")+1);
			IFormElement qtn = formDef.getQuestionWithBinding(binding);
			if(qtn != null)
			{				
				itemSelectionListener.onItemSelected(this,qtn);
			}
			else
			{
				Window.alert("\""+val + "\" " + LocaleText.get("isNotValidQuestion"));
			}
		}
	}

	private void setupPopup(){

		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();

		
		FormDesignerUtil.loadQuestions(formDef.getChildren(),(forDynamicOptions ? dynamicQuestionDef : questionDef),oracle,forDynamicOptions);


		txtField = new TextBox(); //TODO New and hence could be buggy
		sgstField = new SuggestBox(oracle,txtField);
		//make sure something is set
		selectFirstQuestion();


		sgstField.addSelectionHandler(new SelectionHandler(){
			public void onSelection(SelectionEvent event){
				stopSelection();
			}
		});

		
	}

	public void selectQuestion(IFormElement questionDef){
		fieldHyperlink.setText(questionDef.getText() + "\t<" + questionDef.getBinding() + ">");
		itemSelectionListener.onItemSelected(this, questionDef);
	}

	private void selectFirstQuestion(){
		if(questionDef != null)
		{
			selectQuestion(questionDef);
		}
		else
		{
			selectFirstQuestion(formDef.getChildren());
		}
	}

	private boolean selectFirstQuestion(List<IFormElement> questions){
		for(int i=0; i<questions.size(); i++){
			IFormElement questionDef = questions.get(i);
			//if(questionDef.getDataType() == QuestionDef.QTN_TYPE_REPEAT)
			//	selectFirstQuestion(questionDef.getRepeatQtnsDef().getQuestions());
			if(questionDef instanceof GroupDef)
				selectFirstQuestion(((GroupDef)questionDef).getChildren());
			else{
				if(forDynamicOptions){
					if(questionDef == dynamicQuestionDef)
						continue;

					if(!(questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE))
						continue;
				}

				
				selectQuestion(questionDef);
				return true;
			}
		}
		return false;
	}


	/**
	 * Sets the question for this widget.
	 * 
	 * @param questionDef the question definition object.
	 */
	public void setQuestion(IFormElement questionDef){
		if(questionDef instanceof QuestionDef)
			this.questionDef = (QuestionDef)questionDef;
		
		if(questionDef != null)
			fieldHyperlink.setText(questionDef.getText()+"\t("+questionDef.getBinding()+")");
		else{
			horizontalPanel.remove(fieldHyperlink);
			horizontalPanel.remove(sgstField);

			//Removing and adding of fieldHyperlink is to prevent a wiered bug from
			//happening where focus is taken off, brought back and the hyperlink
			//displays no more text.
			horizontalPanel.add(fieldHyperlink);
			fieldHyperlink.setText("");
		}
	}
	
	/**
	 * Gets the currently selected question
	 * @return the question that this widget currently points to
	 */
	public QuestionDef getQuestion()
	{
		return questionDef;
	}

	public void setForDynamicOptions(boolean forDynamicOptions){
		this.forDynamicOptions = forDynamicOptions;
	}

	public void setDynamicQuestionDef(QuestionDef dynamicQuestionDef){
		this.dynamicQuestionDef = dynamicQuestionDef;
	}
	
	/**
	 * Sets the focus on the suggestion box
	 */
	public void setFocus()
	{
		FormDesignerController.makeDirty();
		itemSelectionListener.onStartItemSelection(this);
		horizontalPanel.remove(fieldHyperlink);
		horizontalPanel.add(sgstField);
		sgstField.setText(LocaleText.get("typeQuestion"));
		sgstField.setFocus(true);
		txtField.selectAll();
	}
}
