package org.openrosa.client.view;

import java.util.Vector;

import org.openrosa.client.controller.FormDesignerController;
import org.openrosa.client.controller.IConditionController;
import org.openrosa.client.model.Condition;
import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.QuestionDef;
import org.openrosa.client.model.ValidationRule;
import org.openrosa.client.widget.skiprule.ConditionWidget;
import org.openrosa.client.widget.skiprule.GroupHyperlink;
import org.purc.purcforms.client.PurcConstants;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget enables creation of validation rules.
 * 
 * @author daniel
 *
 */
public class ValidationRulesView extends Composite implements IConditionController{

	/** The widget horizontal spacing in horizontal panels. */
	private static final int HORIZONTAL_SPACING = 5;

	/** The widget vertical spacing in vertical panels. */
	private static final int VERTICAL_SPACING = 0;

	/** The main or root widget. */
	private VerticalPanel verticalPanel = new VerticalPanel();
	
	/** The widget with the action */
	private VerticalPanel actionPanel = null;
	
	private VerticalPanel conditionPanel = new VerticalPanel();
	

	/** Widget for adding new conditions. */
	private Hyperlink addConditionLink = new Hyperlink(LocaleText.get("clickToAddNewCondition"),"");

	/** Widget for grouping conditions. Has all,any, none, and not all. */
	private GroupHyperlink groupHyperlink = new GroupHyperlink(GroupHyperlink.CONDITIONS_OPERATOR_TEXT_ALL,"");

	/** The form definition object that this validation rule belongs to. */
	private FormDef formDef;

	/** The question definition object which is the target of the validation rule. */
	private QuestionDef questionDef;

	/** The validation rule definition object. */
	private ValidationRule validationRule;

	/** Flag determining whether to enable this widget or not. */
	private boolean enabled;

	/** Widget for the validation rule error message. */
	private TextBox txtErrorMessage = new TextBox();

	/** Widget for Label "Question: ". */
	private HTML lblAction = new HTML(LocaleText.get("question")+": " /*"Question: "*/);


	/**
	 * Creates a new instance of the validation rule widget.
	 */
	public ValidationRulesView(){
		setupWidgets();
	}


	/**
	 * Sets up the widgets.
	 */
	private void setupWidgets(){
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(HORIZONTAL_SPACING);

		actionPanel = new VerticalPanel();
		actionPanel.setWidth("100%");
		FormUtil.maximizeWidget(txtErrorMessage);
		actionPanel.add(new HTML(LocaleText.get("errorMessage")));
		actionPanel.add(txtErrorMessage);
		actionPanel.setSpacing(10);

		verticalPanel.add(lblAction);
		

		horizontalPanel.add(new HTML(LocaleText.get("isValidWhen")));
		horizontalPanel.add(groupHyperlink);
		horizontalPanel.add(new Label(LocaleText.get("ofTheFollowingApply")));
		verticalPanel.add(horizontalPanel);

		verticalPanel.add(conditionPanel);
		verticalPanel.add(addConditionLink);
		
		
		
		verticalPanel.add(actionPanel);

		addConditionLink.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				addCondition();
			}
		});


		verticalPanel.setSpacing(VERTICAL_SPACING);
		initWidget(verticalPanel);
		
		//create change listener for the text box
		txtErrorMessage.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				FormDesignerController.makeDirty();
			}
		});
	}


	/**
	 * Adds a new condition.
	 */
	public void addCondition(){
		if(formDef != null && enabled){
			FormDesignerController.makeDirty();
			ConditionWidget conditionWidget = new ConditionWidget(formDef,this,false,questionDef);
			conditionWidget.setQuestionDef(questionDef);
			
			conditionPanel.add(conditionWidget);
			


			txtErrorMessage.setFocus(true);
		}
	}


	/**
	 * Supposed to add a bracket or nested set of related conditions which are 
	 * currently not supported.
	 */
	public void addBracket(){

	}


	/**
	 * Deletes a condition.
	 * 
	 * @param conditionWidget the widget having the condition to delete.
	 */
	public void deleteCondition(ConditionWidget conditionWidget){
		if(validationRule != null)
			validationRule.removeCondition(conditionWidget.getCondition());
		conditionPanel.remove(conditionWidget);
	}


	/**
	 * Sets or updates the values of the validation rule object from the user's widget selections.
	 */
	public void updateValidationRule(){
		if(questionDef == null){
			validationRule = null;
			return;
		}

		if(validationRule == null)
			validationRule = new ValidationRule(questionDef,formDef);

		validationRule.setErrorMessage(txtErrorMessage.getText());
		
		
		
		int count = conditionPanel.getWidgetCount();
		for(int i=0; i<count; i++){
			Widget widget = conditionPanel.getWidget(i);
			if(widget instanceof ConditionWidget){
				Condition condition = ((ConditionWidget)widget).getCondition();

				if(condition != null && !validationRule.containsCondition(condition) && condition.getValue() != null)
					validationRule.addCondition(condition);
				else if(condition != null && validationRule.containsCondition(condition)){
					if(condition.getValue() != null)
						validationRule.updateCondition(condition);
					else
						validationRule.removeCondition(condition);
				}
			}
		}
		

		/*
		int count = verticalPanel.getWidgetCount();
		for(int i=0; i<count; i++){
			Widget widget = verticalPanel.getWidget(i);
			if(widget instanceof ConditionWidget){
				Condition condition = ((ConditionWidget)widget).getCondition();

				if(condition != null && !validationRule.containsCondition(condition) && condition.getValue() != null)
					validationRule.addCondition(condition);
				else if(condition != null && validationRule.containsCondition(condition)){
					if(condition.getValue() != null)
						validationRule.updateCondition(condition);
					else
						validationRule.removeCondition(condition);
				}
			}
		}
		*/

		if(validationRule.getConditions() == null || validationRule.getConditionCount() == 0){
			formDef.removeValidationRule(validationRule);
			validationRule = null;
		}
		else
			validationRule.setConditionsOperator(groupHyperlink.getConditionsOperator());

		if(validationRule != null && !formDef.containsValidationRule(validationRule))
			formDef.addValidationRule(validationRule);
	}


	/**
	 * Sets the question definition object which is the target of the validation rule.
	 * 
	 * @param questionDef the question definition object.
	 */
	public void setQuestionDef(QuestionDef questionDef){
		if(questionDef == null)
		{
			return;
		}
		clearConditions();

		if(questionDef != null)
			formDef = questionDef.getFormDef();
		else
			formDef = null;

		/*if(questionDef.getParent() instanceof PageDef)
			formDef = ((PageDef)questionDef.getParent()).getParent();
		else
			formDef = ((PageDef)((QuestionDef)questionDef.getParent()).getParent()).getParent();*/
		
		SafeHtmlBuilder shb = new SafeHtmlBuilder();
		shb.appendEscapedLines(LocaleText.get("question"));
		shb.appendHtmlConstant(":  <span style=\"font-weight:bold;\">");
		shb.appendEscapedLines(questionDef.getText());
		shb.appendHtmlConstant("</span>");
		
		if(questionDef != null)
			lblAction.setHTML(shb.toSafeHtml());
		else
			lblAction.setText(LocaleText.get("question")+": ");

		this.questionDef = questionDef;

		if(formDef != null){
			validationRule = formDef.getValidationRule(questionDef);
			if(validationRule != null){
				groupHyperlink.setCondionsOperator(validationRule.getConditionsOperator());
				txtErrorMessage.setText(validationRule.getErrorMessage());
				Vector<Condition> conditions = validationRule.getConditions();
				Vector<Condition> lostConditions = new Vector<Condition>();
				for(Condition condition : conditions)					
				{
					ConditionWidget conditionWidget = new ConditionWidget(formDef,this,false,questionDef);
					if(conditionWidget.setCondition(condition))
					{
						conditionPanel.add(conditionWidget);
					}
					else
					{
						lostConditions.add(condition);
					}
				}
				for(int i=0; i<lostConditions.size(); i++)
					validationRule.removeCondition((Condition)lostConditions.elementAt(i));
				if(validationRule.getConditionCount() == 0){
					formDef.removeValidationRule(validationRule);
					validationRule = null;
				}
			}
		}
	}



	/**
	 * Sets the form definition object to which this validation rule belongs.
	 * 
	 * @param formDef the form definition object.
	 */
	public void setFormDef(FormDef formDef){
		updateValidationRule();
		this.formDef = formDef;
		this.questionDef = null;
		clearConditions();
	}


	/**
	 * Removes all validation rule conditions.
	 */
	private void clearConditions(){
		if(questionDef != null)
			updateValidationRule();

		questionDef = null;
		lblAction.setText(LocaleText.get("question")+": ");
		
		conditionPanel.clear();

		

		txtErrorMessage.setText(null);
	}


	/**
	 * Sets whether to enable this widget or not.
	 * 
	 * @param enabled set to true to enable, else false.
	 */
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
		this.groupHyperlink.setEnabled(enabled);

		txtErrorMessage.setEnabled(enabled);

		if(!enabled)
			clearConditions();
	}


	/**
	 * @see com.google.gwt.user.client.WindowResizeListener#onWindowResized(int, int)
	 */
	public void onWindowResized(int width, int height){
		if(width - 700 > 0)
			txtErrorMessage.setWidth(width - 700 + PurcConstants.UNITS);
	}
}
