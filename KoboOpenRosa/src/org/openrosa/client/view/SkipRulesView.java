package org.openrosa.client.view;

import java.util.List;
import java.util.Vector;

import org.openrosa.client.controller.FormDesignerController;
import org.openrosa.client.controller.IConditionController;
import org.openrosa.client.model.Condition;
import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.IFormElement;
import org.openrosa.client.model.QuestionDef;
import org.openrosa.client.model.SkipRule;
import org.openrosa.client.widget.skiprule.ConditionWidget;
import org.openrosa.client.widget.skiprule.GroupHyperlink;
import org.openrosa.client.xforms.RelevantBuilder;
import org.purc.purcforms.client.controller.QuestionSelectionListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.ModelConstants;
import org.purc.purcforms.client.xforms.XformBuilderUtil;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget enables creation of skip rules.
 * 
 * @author daniel
 *
 */
public class SkipRulesView extends Composite implements IConditionController, QuestionSelectionListener{

	/** The widget horizontal spacing in horizontal panels. */
	private static final int HORIZONTAL_SPACING = 5;

	/** The widget vertical spacing in vertical panels. */
	private static final int VERTICAL_SPACING = 0;

	/** The main or root widget. */
	private VerticalPanel verticalPanel = new VerticalPanel();
	
	/** The panel that holds the conditions*/
	private VerticalPanel conditionPanel = new VerticalPanel();

	/** Widget for adding new conditions. */
	private Hyperlink addConditionLink = new Hyperlink(LocaleText.get("clickToAddNewCondition"),"");

	/** Widget for grouping conditions. Has all,any, none, and not all. */
	private GroupHyperlink groupHyperlink = new GroupHyperlink(GroupHyperlink.CONDITIONS_OPERATOR_TEXT_ALL,"");

	/** The form definition object that this skip rule belongs to. */
	private FormDef formDef;

	/** The question definition object which is the target of the skip rule. 
	 *  As for now, the form designer supports only one skip rule target. But the
	 *  skip rule object supports an un limited number.
	 */
	private IFormElement questionDef;

	/** The skip rule definition object. */
	private SkipRule skipRule;

	/** Flag determining whether to enable this widget or not. */
	private boolean enabled;

	/** Widget for the skip rule action to make a question required. */
	private CheckBox chkMakeRequired = new CheckBox("Make Required");

	/** Widget for Label "question". */
	private Label lblAction = new Label(LocaleText.get("question")+":");
	
	/** Widget for Label for the question, in questions, name. */
	private Label lblQuestionName = new Label("");

	/** Widget for Label "and". */
	private Label lblAnd = new Label(LocaleText.get("and"));

	/** Hande code checkbox, check this when you want to hand code your awesomeness*/
	private CheckBox chkHandCode = new CheckBox(LocaleText.get("handCode"));
	
	private TextArea handCodeTextArea = new TextArea();
	
	private HTML applyToOtherHtml = new HTML(LocaleText.get("applySkipToOtherQuestions"));
	
	private Hyperlink applyToOtherQuestionsHyperLink = null;
	
	private HorizontalPanel horzPanel = null;
	
	private HorizontalPanel horizontalPanel = null;
	/**
	 * Creates a new instance of the skip logic widget.
	 */
	public SkipRulesView(){
		setupWidgets();
	}

	/**
	 * Sets up the widgets.
	 */
	private void setupWidgets(){
		
		//set the items for hand coding
		verticalPanel.add(chkHandCode);
		handCodeTextArea.setVisible(false);
		handCodeTextArea.setWidth("317px");
		handCodeTextArea.setHeight("100px");
		verticalPanel.add(handCodeTextArea);
		
		chkHandCode.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeToHandCode(true);
			}
		});
		

		
		handCodeTextArea.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event)
			{
				changeHandCode();
			}});
		
		
		horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(HORIZONTAL_SPACING);


		applyToOtherQuestionsHyperLink = new Hyperlink(LocaleText.get("clickForOtherQuestions"),"");
		applyToOtherQuestionsHyperLink.addClickHandler(new ClickHandler(){		
			public void onClick(ClickEvent event){
				showOtherQuestions();
			}
		});

		lblQuestionName.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		horzPanel = new HorizontalPanel();
		horzPanel.setSpacing(HORIZONTAL_SPACING);
		horzPanel.add(lblAction);
		horzPanel.add(lblQuestionName);
		//horzPanel.add(lblAnd);
		//horzPanel.add(applyToOtherQuestionsHyperLink);

		verticalPanel.add(horzPanel);		

		horizontalPanel.add(new HTML(LocaleText.get("notSkippedOver")));
		horizontalPanel.add(groupHyperlink);
		horizontalPanel.add(new Label(LocaleText.get("ofTheFollowingApply")));
		verticalPanel.add(horizontalPanel);
		verticalPanel.add(conditionPanel);
		verticalPanel.add(addConditionLink);

		addConditionLink.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				addCondition();
				FormDesignerController.makeDirty();
			}
		});

		
		verticalPanel.add(applyToOtherHtml);
		verticalPanel.add(applyToOtherQuestionsHyperLink);
		

		verticalPanel.setSpacing(VERTICAL_SPACING);
		initWidget(verticalPanel);
		
	}

	

	/**
	 * Adds a new condition.
	 */
	public void addCondition(){
		if(formDef != null && enabled){
			//verticalPanel.remove(addConditionLink);
			ConditionWidget conditionWidget = new ConditionWidget(formDef,this,true,questionDef);
			conditionPanel.add(conditionWidget);
			conditionWidget.setFocusOnFieldWidget();
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
		if(skipRule != null){
			Condition condition = conditionWidget.getCondition();
			if(condition != null){
				if(skipRule.getConditionCount() == 1)
					skipRule.removeCondition(condition);
			}
		}
		//verticalPanel.remove(conditionWidget);
		conditionPanel.remove(conditionWidget);
	}

	/**
	 * Sets or updates the values of the skip rule object from the user's widget selections.
	 */
	public void updateSkipRule(){
		if(questionDef == null){
			skipRule = null;
			return;
		}

		if(skipRule == null)
			skipRule = new SkipRule();
		
		skipRule.setActionTarget(questionDef);

		int conditionCount = 0;
		//int count = verticalPanel.getWidgetCount();
		int count = conditionPanel.getWidgetCount();
		
		for(int i=0; i<count; i++){
			Widget widget = conditionPanel.getWidget(i);
			if(widget instanceof ConditionWidget){
				Condition condition = ((ConditionWidget)widget).getCondition();
				if(condition != null && !skipRule.containsCondition(condition))
					skipRule.addCondition(condition);
				else if(condition != null && skipRule.containsCondition(condition))
					skipRule.updateCondition(condition);
				conditionCount++;
			}
		}

		if(skipRule.getConditions() == null || conditionCount == 0)
			skipRule = null;
		else{
			skipRule.setConditionsOperator(groupHyperlink.getConditionsOperator());
			skipRule.setAction(getAction());
		}

		if(skipRule != null && !formDef.containsSkipRule(skipRule))
			formDef.addSkipRule(skipRule);
	}

	/**
	 * Gets the skip rule action based on the user's widget selections.
	 * 
	 * @return the skip rule action.
	 */
	private int getAction(){
		int action = 0;
		action |= ModelConstants.ACTION_ENABLE;

		if(chkMakeRequired.getValue() == true)
			action |= ModelConstants.ACTION_MAKE_MANDATORY;
		else
			action |= ModelConstants.ACTION_MAKE_OPTIONAL;

		return action;
	}

	/**
	 * Updates the widgets basing on a given skip rule action.
	 * 
	 * @param action the skip rule action.
	 */
	private void setAction(int action){
		chkMakeRequired.setValue((action & ModelConstants.ACTION_MAKE_MANDATORY) != 0);
	}

	/**
	 * Sets the question definition object which is the target of the skip rule.
	 * For now we support only one target for the skip rule.
	 * 
	 * @param questionDef the question definition object.
	 */
	public void setQuestionDef(IFormElement questionDef){
		clearConditions();

		formDef = questionDef.getFormDef();

		if(questionDef != null)
			lblQuestionName.setText(questionDef.getText());

		this.questionDef = questionDef;

		skipRule = formDef.getSkipRule(questionDef);
		if(skipRule != null){
			groupHyperlink.setCondionsOperator(skipRule.getConditionsOperator());
			setAction(skipRule.getAction());
			Vector<Condition> conditions = skipRule.getConditions();
			Vector<Condition> lostConditions = new Vector<Condition>();
			if(conditions != null)
			{
				for(Condition condition : conditions)
				{
					ConditionWidget conditionWidget = new ConditionWidget(formDef,this,true,questionDef);
					if(conditionWidget.setCondition(condition))
						conditionPanel.add(conditionWidget);
					else
						lostConditions.add(condition);
				}
			}
			
			for(int i=0; i<lostConditions.size(); i++)
				skipRule.removeCondition((Condition)lostConditions.elementAt(i));
			if(skipRule.getConditionCount() == 0 && (skipRule.getHandCode() == null || skipRule.getHandCode().length() == 0)){
				formDef.removeSkipRule(skipRule);
				skipRule = null;
			}

		}
		
		//check if we should use the hand coded view
		if(skipRule != null && skipRule.getHandCode() != null)
		{
			chkHandCode.setValue(true);			
		}
		else
		{
			chkHandCode.setValue(false);
		}
		changeToHandCode(false);
	}

	/**
	 * Sets the form definition object to which this skip rule belongs.
	 * 
	 * @param formDef the form definition object.
	 */
	public void setFormDef(FormDef formDef){
		updateSkipRule();
		this.formDef = formDef;
		this.questionDef = null;
		clearConditions();
	}

	/**
	 * Removes all skip rule conditions.
	 */
	private void clearConditions(){
		if(questionDef != null)
			updateSkipRule();

		questionDef = null;

		conditionPanel.clear();
		
		chkMakeRequired.setValue(false);

	}

	/**
	 * Sets whether to enable this widget or not.
	 * 
	 * @param enabled set to true to enable, else false.
	 */
	public void setEnabled(boolean enabled){
		this.enabled = enabled;

		groupHyperlink.setEnabled(enabled);
		chkMakeRequired.setEnabled(enabled);

		if(!enabled)
			clearConditions();
	}


	/**
	 * Shows a list of other questions that are targets of the current skip rule.
	 */
	private void showOtherQuestions(){
		if(enabled){
			SkipQtnsDialog dialog = new SkipQtnsDialog(this);
			dialog.setData(formDef,questionDef,skipRule);
			dialog.center();
		}
	}


	/**
	 * @see org.purc.purcforms.client.controller.QuestionSelectionListener#onQuestionsSelected(List)
	 */
	public void onQuestionsSelected(List<String> questions){
		
		//loop over the selected questions
		for(String qString : questions)
		{
			//make sure we're dealing with a question
			IFormElement element = formDef.getElement(qString);
			QuestionDef question = null;
			if(!(element instanceof QuestionDef))
			{
				continue;
			}
			else
			{
				question = (QuestionDef)element;
			}
			//now check if the question we have is the question we're currently focused on,
			//if it is, ignore it
			if(question == questionDef)
			{
				continue;
			}
			//now check if there's already a skip question for this question?
			SkipRule sr = formDef.getSkipRule(question);
			if(sr == null)
			{
				sr = new SkipRule();
				sr.setActionTarget(question);
			}
			//copy the conditions of the current skip rule over to this new one
			//but first make sure we have the most up to date conditions
			updateSkipRule();
			//don't copy if it's null
			if(skipRule == null)
			{
				return;
			}
			//copy conditions
			for(Condition c : skipRule.getConditions())
			{
				//if it's already there skip it
				if(sr.containsCondition(c))
				{
					continue;
				}
				//since we're not only interested in pointer equals but effective equals check the effective equals method
				boolean dontAdd = false;
				if(sr.getConditions() != null)
				{
					for(Condition preExistingCondition : sr.getConditions())
					{
						if(preExistingCondition.effectivelyEqualTo(c))
						{
							dontAdd = true;
							continue;
						}
					}
					if(dontAdd)
					{
						continue;
					}
				}
				Condition copyCondition = new Condition(c);
				sr.addCondition(copyCondition);
			}
			//copy action, whatever that means
			sr.setAction(skipRule.getAction());
			//copy the condition operator, I think that means how it joins together multiple conditions
			sr.setConditionsOperator(skipRule.getConditionsOperator());
			
			//now add to the form if it's not already there
			if(!formDef.containsSkipRule(sr))
			{
				formDef.addSkipRule(sr);
			}
			
		}//end for loop over selected questions

		
	}//end onQuestionsSelected
	
	/**
	 * Called to swap out the hand code stuff with everything else and 
	 * vice versa
	 */
	private void changeToHandCode(boolean userInitiated)
	{
		
		if(chkHandCode.getValue())
		{
			//it's hand coded
			String relevant = "";
			if(userInitiated)
			{
				//get the machine made code thus far
				updateSkipRule();				
				if(skipRule == null)
				{
					skipRule = new SkipRule();
					skipRule.setActionTarget(questionDef);
					formDef.addSkipRule(skipRule);
				}
				//get all the conditions
				if(skipRule.getConditions() != null)
				{
					Vector<Condition> conditions  = skipRule.getConditions();
					//loop over the conditions
					for(Condition condition : conditions)
					{
						if(relevant.length() > 0)
							relevant += XformBuilderUtil.getConditionsOperatorText(skipRule.getConditionsOperator());
						relevant += RelevantBuilder.fromSkipCondition2Xform(condition,formDef,skipRule.getAction());
					}
				}
			}
			else
			{
				if(skipRule != null)
				{
					relevant = skipRule.getHandCode();
				}
			}
			skipRule.setHandCode(relevant);
			//hide things as needed
			handCodeTextArea.setVisible(true);
			handCodeTextArea.setText(relevant);
			horzPanel.setVisible(false);					
			horizontalPanel.setVisible(false);
			conditionPanel.setVisible(false);
			addConditionLink.setVisible(false);			
			applyToOtherHtml.setVisible(false);
			applyToOtherQuestionsHyperLink.setVisible(false);
		}
		else
		{
			if(userInitiated)
			{
				if(!Window.confirm(LocaleText.get("warningSwitchConditionModes")))
				{
					//invert the current selection
					chkHandCode.setValue(!chkHandCode.getValue());
					return;
				}
				formDef.removeSkipRule(skipRule);
				conditionPanel.clear();				
				chkMakeRequired.setValue(false);
				
				
				//set the hand code to null
				skipRule = null;
			}
			
			//it's written by machines						
			handCodeTextArea.setVisible(false);
			horzPanel.setVisible(true);					
			horizontalPanel.setVisible(true);
			conditionPanel.setVisible(true);
			addConditionLink.setVisible(true);			
			applyToOtherHtml.setVisible(true);
			applyToOtherQuestionsHyperLink.setVisible(true);
						
		}
	}
	
	/**
	 * Called whenever the user changes the hand coded code
	 */
	public void changeHandCode()
	{
		String handCodedCode = handCodeTextArea.getText();
		skipRule.setHandCode(handCodedCode);
	}
	
}//end class
