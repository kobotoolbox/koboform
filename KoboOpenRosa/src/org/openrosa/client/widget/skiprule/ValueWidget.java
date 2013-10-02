package org.openrosa.client.widget.skiprule;

import java.util.Iterator;
import java.util.List;

import org.openrosa.client.controller.FormDesignerController;
import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.IFormElement;
import org.openrosa.client.model.OptionDef;
import org.openrosa.client.model.QuestionDef;
import org.openrosa.client.util.FormDesignerUtil;
import org.purc.purcforms.client.PurcConstants;
import org.purc.purcforms.client.controller.ItemSelectionListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.ModelConstants;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.widget.SelectItemCommand;
import org.zenika.widget.client.datePicker.DatePicker;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * 
 * @author daniel
 *
 */
public class ValueWidget extends Composite implements ItemSelectionListener, CloseHandler{

	public static final String EMPTY_VALUE = "_____";
	private static final String BETWEEN_WIDGET_SEPARATOR = "   "+ LocaleText.get("and") + "   ";
	private static final String BETWEEN_VALUE_SEPARATOR = " " + LocaleText.get("and") + " ";
	private static final String LIST_SEPARATOR = " , ";

	private QuestionDef questionDef;
	private int operator = ModelConstants.OPERATOR_NULL;
	private int function = ModelConstants.FUNCTION_VALUE;

	private HorizontalPanel horizontalPanel;
	private TextBox txtValue1 = new TextBox();
	private TextBox txtValue2 = new TextBox();
	private Label lblAnd = new Label(BETWEEN_WIDGET_SEPARATOR);
	private Hyperlink valueHyperlink;
	private PopupPanel popup;

	private KeyPressHandler keyboardListener1;
	private KeyPressHandler keyboardListener2;
	private HandlerRegistration handlerReg1;
	private HandlerRegistration handlerReg2;

	private QuestionDef prevQuestionDef;
	private CheckBox chkQuestionValue = new CheckBox(LocaleText.get("questionValue"));
	private FormDef formDef;
	private SuggestBox sgstField = new SuggestBox();
	private QuestionDef valueQtnDef;

	private QuestionDef parentQuestionDef;
	
	
	public ValueWidget(){
		setupWidgets();
	}

	public void setQuestionDef(QuestionDef questionDef){
		prevQuestionDef = this.questionDef;
		this.questionDef = questionDef;
	}

	public void setOperator(int operator){
		if(this.operator != operator){ 
			if(this.operator == ModelConstants.OPERATOR_IS_NULL || this.operator == ModelConstants.OPERATOR_IS_NOT_NULL)
				valueHyperlink.setText(EMPTY_VALUE);

			/*if((this.operator == PurcConstants.OPERATOR_IN_LIST || this.operator == PurcConstants.OPERATOR_NOT_IN_LIST) &&
			  !(operator == PurcConstants.OPERATOR_IN_LIST || operator == PurcConstants.OPERATOR_NOT_IN_LIST))
		    	valueHyperlink.setText(EMPTY_VALUE);*/
		}

		this.operator = operator;

		if(operator == ModelConstants.OPERATOR_IS_NULL || operator == ModelConstants.OPERATOR_IS_NOT_NULL)
			valueHyperlink.setText("");
		else if(operator == ModelConstants.OPERATOR_BETWEEN || operator == ModelConstants.OPERATOR_NOT_BETWEEN)
			valueHyperlink.setText(EMPTY_VALUE + BETWEEN_VALUE_SEPARATOR + EMPTY_VALUE);
	}

	public void setFunction(int function){
		if(this.function != function)
			valueHyperlink.setText(EMPTY_VALUE);
		this.function = function;
	}

	private void setupWidgets(){
		horizontalPanel = new HorizontalPanel();;

		valueHyperlink = new Hyperlink(EMPTY_VALUE,"");
		horizontalPanel.add(valueHyperlink);

		valueHyperlink.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				FormDesignerController.makeDirty();
				startEdit();
			}
		});

		//Cuases wiered behaviour when editing with between operator
		/*txtValue1.addFocusListener(new FocusListenerAdapter(){
			public void onLostFocus(Widget sender){
				stopEdit();
			}
		});*/

		setupTextListeners();

		this.chkQuestionValue.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				setupFieldSelection();
			}
		});

		initWidget(horizontalPanel);
	}

	private void setupTextListeners(){

		txtValue1.addKeyPressHandler(new KeyPressHandler(){
			public void onKeyPress(KeyPressEvent event) {
				if((txtValue1.getText().indexOf("'") != -1) || (txtValue1.getText().indexOf("\"") != -1))
				{
					Window.alert(LocaleText.get("NoSingleQuotes"));
					txtValue1.setText(txtValue1.getText().replace("'", ""));
					txtValue1.setText(txtValue1.getText().replace("\"", ""));
					return;
					
				}
				if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
					stopEdit(true);
			}
		});
		
		txtValue1.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				if((txtValue1.getText().indexOf("'") != -1) || (txtValue1.getText().indexOf("\"") != -1))
				{
					Window.alert(LocaleText.get("NoSingleQuotes"));
					txtValue1.setText(txtValue1.getText().replace("'", ""));
					txtValue1.setText(txtValue1.getText().replace("\"", ""));
					return;
					
				}
				if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
					stopEdit(true);
			}
		});
		
		txtValue1.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				if((txtValue1.getText().indexOf("'") != -1) || (txtValue1.getText().indexOf("\"") != -1))
				{
					Window.alert(LocaleText.get("NoSingleQuotes"));
					txtValue1.setText(txtValue1.getText().replace("'", ""));
					txtValue1.setText(txtValue1.getText().replace("\"", ""));
					return;
					
				}
			}
		});

		txtValue2.addKeyPressHandler(new KeyPressHandler(){
			public void onKeyPress(KeyPressEvent event) {
				
				if((txtValue2.getText().indexOf("'") != -1) || (txtValue2.getText().indexOf("\"") != -1))
				{
					Window.alert(LocaleText.get("NoSingleQuotes"));
					txtValue2.setText(txtValue2.getText().replace("'", ""));
					txtValue2.setText(txtValue2.getText().replace("\"", ""));
					return;
					
				}
				
				if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
					stopEdit(true);
			}
		});
		
		
		
		

		txtValue2.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				if((txtValue2.getText().indexOf("'") != -1) || (txtValue2.getText().indexOf("\"") != -1))
				{
					Window.alert(LocaleText.get("NoSingleQuotes"));
					txtValue2.setText(txtValue2.getText().replace("'", ""));
					txtValue2.setText(txtValue2.getText().replace("\"", ""));
					return;
					
				}
				if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
					stopEdit(true);
			}
		});
		
		txtValue2.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				if((txtValue2.getText().indexOf("'") != -1) || (txtValue2.getText().indexOf("\"") != -1))
				{
					Window.alert(LocaleText.get("NoSingleQuotes"));
					txtValue2.setText(txtValue2.getText().replace("'", ""));
					txtValue2.setText(txtValue2.getText().replace("\"", ""));
					return;
					
				}
			}
		});
		
		

		if(!(operator == ModelConstants.OPERATOR_BETWEEN || operator == ModelConstants.OPERATOR_NOT_BETWEEN)){
			txtValue1.addBlurHandler(new BlurHandler(){
				public void onBlur(BlurEvent event){
					//stopEdit(true);
				}
			});
			txtValue2.addBlurHandler(new BlurHandler(){
				public void onBlur(BlurEvent event){
					stopEdit(true);
				}
			});

			//if(txtValue1 instanceof DatePicker){
			txtValue1.addChangeHandler(new ChangeHandler(){
				public void onChange(ChangeEvent event){
					stopEdit(true); //TODO One has to explicitly press ENTER because of the bug we currently have on ticking the question value checkbox
				}
			});
			txtValue2.addChangeHandler(new ChangeHandler(){
				public void onChange(ChangeEvent event){
					stopEdit(true); //TODO One has to explicitly press ENTER because of the bug we currently have on ticking the question value checkbox
				}
			});
			//}
		}
	}

	private void setupFieldSelection()
	{
		if(questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE || 
				questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE ||
				questionDef.getDataType() == QuestionDef.QTN_TYPE_BOOLEAN)
		{
			if(chkQuestionValue.getValue() == true){
				if(horizontalPanel.getWidgetIndex(valueHyperlink) > -1){
					horizontalPanel.remove(valueHyperlink);
					popup.hide();
					setupPopup();
					horizontalPanel.add(sgstField);
					horizontalPanel.add(chkQuestionValue);
					sgstField.setFocus(true);
				}
			}
			else{
				if(horizontalPanel.getWidgetIndex(sgstField) > -1){
					horizontalPanel.remove(sgstField);
					horizontalPanel.remove(chkQuestionValue);
						
					horizontalPanel.add(valueHyperlink);
					valueHyperlink.setText(ValueWidget.EMPTY_VALUE);
					horizontalPanel.add(chkQuestionValue);
					startEdit();					
				}
			}
		}
		else
		{
			if(chkQuestionValue.getValue() == true){
				if(horizontalPanel.getWidgetIndex(txtValue1) > -1){
					horizontalPanel.remove(txtValue1);
					horizontalPanel.remove(chkQuestionValue);
					setupPopup();
					horizontalPanel.add(sgstField);
					horizontalPanel.add(chkQuestionValue);
					sgstField.setFocus(true);
					sgstField.setFocus(true);
					txtValue1.selectAll();
				}
			}
			else{
				if(horizontalPanel.getWidgetIndex(sgstField) > -1){
					horizontalPanel.remove(sgstField);
					horizontalPanel.remove(chkQuestionValue);
					if(txtValue1.getParent() != null && txtValue1.getParent() instanceof SuggestBox){
						//txtValue1.removeKeyboardListener(keyboardListener1);
						//txtValue2.removeKeyboardListener(keyboardListener2);
						if(handlerReg1 != null){
							handlerReg1.removeHandler();
							handlerReg2.removeHandler();
						}
	
						txtValue1 = new TextBox();
						txtValue2 = new TextBox();
						setupTextListeners();
					}
	
					horizontalPanel.add(txtValue1);
					horizontalPanel.add(chkQuestionValue);
					txtValue1.setFocus(true);
					txtValue1.setFocus(true);
					txtValue1.selectAll();
				}
			}
		}//end question isn't a select or boolean question
	}

	private void startEdit(){
		if(questionDef.getDataType() == QuestionDef.QTN_TYPE_BOOLEAN && !chkQuestionValue.getValue()){
			MenuBar menuBar = new MenuBar(true);
			menuBar.addItem(QuestionDef.TRUE_DISPLAY_VALUE,true, new SelectItemCommand(QuestionDef.TRUE_DISPLAY_VALUE,this));
			menuBar.addItem(QuestionDef.FALSE_DISPLAY_VALUE,true, new SelectItemCommand(QuestionDef.FALSE_DISPLAY_VALUE,this));

			popup = new PopupPanel(true,false);
			popup.setWidget(menuBar);
			popup.setPopupPosition(valueHyperlink.getAbsoluteLeft(), valueHyperlink.getAbsoluteTop() - 60);
			popup.show();
			horizontalPanel.add(chkQuestionValue);
		}
		//if the question is a select1 or select, but doesn't use item sets
		else if( (questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE || questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE) && !questionDef.usesItemSet() && !chkQuestionValue.getValue()  ){

			MenuBar menuBar = new MenuBar(true);

			int size = 0, maxSize = 0; String text;
			if(questionDef.getOptions() instanceof List)
			{
				List options = (List)(questionDef.getOptions());
				
	
				if(options == null)
					return;
	
				for(int i=0; i<options.size(); i++)
				{
					if(options.get(i) instanceof OptionDef)
					{
						OptionDef optionDef = (OptionDef)options.get(i);
						text = optionDef.getText();
						size = text.length();
						if(maxSize < size)
							maxSize = size;
						menuBar.addItem(text,true, new SelectItemCommand(optionDef,this));
					}
				}
			
				maxSize*=12;
	
				/*ScrollPanel scrollPanel = new ScrollPanel();
				scrollPanel.setWidget(menuBar);
				scrollPanel.setHeight("200"+PurcConstants.UNITS);
				scrollPanel.setWidth((maxSize*11)+PurcConstants.UNITS);*/
	
				int height = options.size()*29;
				if(height > 400)
					height = 400;
				
				if(maxSize < 50)
					maxSize = 50;
				if(height < 50)
					height = 50;
				
				ScrollPanel scrollPanel = new ScrollPanel();
				scrollPanel.setWidget(menuBar);
				scrollPanel.setHeight(height+PurcConstants.UNITS); //"200"+PurcConstants.UNITS
				scrollPanel.setWidth((maxSize)+PurcConstants.UNITS);
	
				popup = new PopupPanel(true,false);
				popup.setWidget(scrollPanel);
				popup.setPopupPosition(valueHyperlink.getAbsoluteLeft(), valueHyperlink.getAbsoluteTop() - height - 10); //- height makes it fly upwards
				popup.show();
				horizontalPanel.add(chkQuestionValue);
			}
		}
		else if( (questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE || questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE) &&
				(operator == ModelConstants.OPERATOR_IN_LIST || operator == ModelConstants.OPERATOR_IN_LIST_INCLUSIVE || operator == ModelConstants.OPERATOR_NOT_IN_LIST) 
				 && !questionDef.usesItemSet() && !chkQuestionValue.getValue()){

			String values = valueHyperlink.getText();
			String[] vals = null;
			if(!values.equals(EMPTY_VALUE))
				vals = values.split(LIST_SEPARATOR);

			int size = 0, maxSize = 0; String text;
			VerticalPanel panel = new VerticalPanel();
			if(questionDef.getOptions() instanceof List)
			{
				List options = (List)(questionDef.getOptions());
	
				if(options == null)
					return;
	
				for(int i=0; i<options.size(); i++)
				{
					if(options.get(i) instanceof OptionDef)
					{
						OptionDef optionDef = (OptionDef)options.get(i);
		
						text = optionDef.getText();
						size = text.length();
						if(maxSize < size)
							maxSize = size;
		
						CheckBox checkbox = new CheckBox(text);
						if(InArray(vals,text))
							checkbox.setValue(true);
						panel.add(checkbox);
					}
				}
				
				maxSize*=12;
	
				int height = options.size()*29;
				if(height > 400)
					height = 400;
	
				if(maxSize < 50)
					maxSize = 50;
				if(height < 50)
					height = 50;
				
				ScrollPanel scrollPanel = new ScrollPanel();
				scrollPanel.setWidget(panel);
				scrollPanel.setHeight(height+PurcConstants.UNITS); //"200"+PurcConstants.UNITS
				scrollPanel.setWidth((maxSize)+PurcConstants.UNITS);
	
				popup = new PopupPanel(true,false);
				popup.addCloseHandler(this);
				popup.setWidget(scrollPanel);
				popup.setPopupPosition(valueHyperlink.getAbsoluteLeft(), valueHyperlink.getAbsoluteTop() - height - 10);
				popup.show();
				horizontalPanel.add(chkQuestionValue);
			}
		}
		else{

			if(handlerReg1 != null){
				handlerReg1.removeHandler();
				handlerReg2.removeHandler();
			}

			txtValue1 = new TextBox();
			txtValue2 = new TextBox();

			setupTextListeners();

			horizontalPanel.remove(valueHyperlink);

			

			

			if(chkQuestionValue.getValue() == true)
			{
				setupPopup();
				horizontalPanel.add(sgstField);
				if(!valueHyperlink.getText().equals(EMPTY_VALUE))
					sgstField.setText(valueHyperlink.getText());
				sgstField.setFocus(true);				
			}
			else
			{
				addNumericKeyboardListener();
				horizontalPanel.add(txtValue1);
				if(!valueHyperlink.getText().equals(EMPTY_VALUE))
					txtValue1.setText(valueHyperlink.getText());
				txtValue1.setFocus(true);
			}
			txtValue1.selectAll();
			horizontalPanel.add(chkQuestionValue);
			
			if(operator ==  ModelConstants.OPERATOR_BETWEEN ||
					operator ==  ModelConstants.OPERATOR_NOT_BETWEEN){
				horizontalPanel.add(lblAnd);
				horizontalPanel.add(txtValue2);

				String val = txtValue1.getText();
				if(val.contains(BETWEEN_VALUE_SEPARATOR)){
					int pos = val.indexOf(BETWEEN_VALUE_SEPARATOR);
					String s = val.substring(0, pos);
					if(s.equals(EMPTY_VALUE))
						s = "";
					txtValue1.setText(s);
					if(pos+BETWEEN_VALUE_SEPARATOR.length() != val.length()){
						pos = pos + BETWEEN_VALUE_SEPARATOR.length();
						s = val.substring(pos,val.length());
						if(s.equals(EMPTY_VALUE)){
							s = "";
							if(txtValue1.getText().trim().length() > 0)
								txtValue2.setFocus(true);
						}
						txtValue2.setText(s);
					}
					else
						txtValue2.setText("");
				}
			}
		}
	}

	private void addNumericKeyboardListener(){
		if(questionDef.getDataType() == QuestionDef.QTN_TYPE_NUMERIC || questionDef.getDataType() == QuestionDef.QTN_TYPE_DECIMAL){
			keyboardListener1 = FormUtil.getAllowNumericOnlyKeyboardListener(txtValue1, questionDef.getDataType() == QuestionDef.QTN_TYPE_NUMERIC ? false : true);
			keyboardListener2 = FormUtil.getAllowNumericOnlyKeyboardListener(txtValue2, questionDef.getDataType() == QuestionDef.QTN_TYPE_NUMERIC ? false : true);

			handlerReg1 = txtValue1.addKeyPressHandler(keyboardListener1);
			handlerReg2 = txtValue2.addKeyPressHandler(keyboardListener2);
		}
		else if(function == ModelConstants.FUNCTION_LENGTH){
			keyboardListener1 = FormUtil.getAllowNumericOnlyKeyboardListener(txtValue1, false);
			keyboardListener2 = FormUtil.getAllowNumericOnlyKeyboardListener(txtValue2, false );

			txtValue1.addKeyPressHandler(keyboardListener1);
			txtValue2.addKeyPressHandler(keyboardListener2);
		}

	}

	private boolean InArray(String[] array, String item){
		if(array == null)
			return false;

		for(int i=0; i<array.length; i++){
			if(array[i].equals(item))
				return true;
		}
		return false;
	}

	public void stopEdit(boolean updateValue){
		String val1 = txtValue1.getText();
		
		if((val1.indexOf("'") != -1) || (val1.indexOf("\"") != -1))
		{
			Window.alert(LocaleText.get("NoSingleQuotes"));
			val1 = val1.replace("'", "");
			val1 = val1.replace("\"", "");	
		}

		if(val1.trim().length() == 0){
			val1 = EMPTY_VALUE;
			if(txtValue1 instanceof DatePicker)
				return;
		}

		String val2 = txtValue2.getText();
		
		if((val2.indexOf("'") != -1) || (val2.indexOf("\"") != -1))
		{
			Window.alert(LocaleText.get("NoSingleQuotes"));
			val2 = val2.replace("'", "");
			val2 = val2.replace("\"", "");	
		}
		
		if(val2.trim().length() == 0){
			val2 = EMPTY_VALUE;
			if(txtValue2 instanceof DatePicker)
				return;
		}

		String val = val1 + 
		((operator == ModelConstants.OPERATOR_BETWEEN || 
				operator == ModelConstants.OPERATOR_NOT_BETWEEN) ? (BETWEEN_VALUE_SEPARATOR + val2 ): "");

		if(updateValue)
			valueHyperlink.setText(val);

		horizontalPanel.remove(txtValue1);
		horizontalPanel.remove(txtValue2);
		horizontalPanel.remove(lblAnd);
		horizontalPanel.remove(chkQuestionValue);
		horizontalPanel.remove(sgstField);
		horizontalPanel.add(valueHyperlink);
	}

	public void onItemSelected(Object sender, Object item) {
		if(sender instanceof SelectItemCommand){
			popup.hide();
			if(questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE ||
					questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE)
				valueHyperlink.setText(((OptionDef)item).getText());
			else if(questionDef.getDataType() == QuestionDef.QTN_TYPE_BOOLEAN)
				valueHyperlink.setText((String)item);
			horizontalPanel.remove(chkQuestionValue);
		}
	}

	public void onStartItemSelection(Object sender){

	}

	public void onClose(CloseEvent event){
		String value = "";
		//VerticalPanel panel = (VerticalPanel)popup.getWidget();
		HasWidgets widgetHolder = (HasWidgets)popup.getWidget();
		widgetHolder = findCheckBoxes(widgetHolder);
		Iterator<Widget> widgetIterator = widgetHolder.iterator();
		for(;widgetIterator.hasNext();)
		{
			Widget w = widgetIterator.next();
			if(w instanceof CheckBox)
			{
				CheckBox box = (CheckBox)w;
				if(box.getValue() == true)
				{
					if(value.length() > 0)
					{
						value += LIST_SEPARATOR;
					}
					value += box.getText();
				}
			}
		}		
		if(value.length() == 0)
			value = EMPTY_VALUE;
		valueHyperlink.setText(value);
	}
	
	/**
	 * finds the checkbox burried in widgets
	 * @param w the widget in question
	 * @return the first checkbox we can find
	 */
	private HasWidgets findCheckBoxes(Object w)
	{
		if(w instanceof CheckBox)
		{
			return (HasWidgets)(((CheckBox)w).getParent());
		}
		else if (w instanceof HasWidgets)
		{				
			HasWidgets widgetHolder = (HasWidgets)w;
			//we want a breadth first search.
			Iterator<Widget> widgetIterator = widgetHolder.iterator();
			for(;widgetIterator.hasNext();)
			{
				Widget widget = widgetIterator.next();
				if(widget instanceof CheckBox)
				{
					return (HasWidgets)w;
				}
			}
			
			//now go through and recurse
			widgetIterator = widgetHolder.iterator();
			for(;widgetIterator.hasNext();)
			{
				Widget widget = widgetIterator.next();
				HasWidgets answer = findCheckBoxes(widget);
				if(answer != null)
				{
					return answer;
				}			
			}
		}//end if instance of hasWidgets

		return null;
	}

	public String getValue(){
		valueQtnDef = null;

		String val = valueHyperlink.getText();
		if(val.equals(EMPTY_VALUE))
			return null;
		else if(val == null || val.trim().length() == 0)
			return val; //could be IS NULL or IS NOT NULL

		//we're using the value of a question, not the literal text typed in the field
		if(chkQuestionValue.getValue() == true)
		{
			//make sure there's a valid binding in there
			if(val.indexOf("<") == -1 || val.indexOf(">") == -1)
			{
				return null;
			}
			//get just the binding			
			String binding = val.substring(val.indexOf("<")+1, val.indexOf(">"));
			valueQtnDef = (QuestionDef)formDef.getQuestionWithBinding(binding);
			
			
		}
		else if(questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE && !questionDef.usesItemSet()){
			OptionDef optionDef = questionDef.getOptionWithText(val);
			if(optionDef != null)
				val = optionDef.getDefaultValue(); //we want to bind to the value, not the binding
			else
				val = null;
		}
		else if(questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE && !questionDef.usesItemSet()){
			String[] options = val.split(LIST_SEPARATOR);
			if(options == null || options.length == 0)
				val = null;
			else{
				val = "";
				for(int i=0; i<options.length; i++){
					OptionDef optionDef = questionDef.getOptionWithText(options[i]);
					if(optionDef != null){
						if(val.length() > 0)
							val += LIST_SEPARATOR;
						val += optionDef.getDefaultValue();
					}
				}
			}
		}
		else if(questionDef.getDataType() == QuestionDef.QTN_TYPE_BOOLEAN){
			if(val.equals(QuestionDef.TRUE_DISPLAY_VALUE))
				val = QuestionDef.TRUE_VALUE;
			else if(val.equals(QuestionDef.FALSE_DISPLAY_VALUE))
				val = QuestionDef.FALSE_VALUE;
		}

		
		return val;
	}
	
	
	
	
	
	
	
	
	

	public void setValue(String value){
		String sValue = value;

		if(sValue != null && sValue != EMPTY_VALUE){
			if(sValue.startsWith(formDef.getVariableName() + "/")){
				sValue = sValue.substring(sValue.indexOf('/')+1);
				IFormElement qtn = formDef.getElement(sValue);
				if(qtn != null)
					sValue = qtn.getText();
				else{ //possibly varname changed.
					if(valueQtnDef != null){
						qtn = formDef.getElement(valueQtnDef.getBinding());
						if(qtn != null)
							sValue = qtn.getText();
						else
							sValue = EMPTY_VALUE;
					}
					else
						sValue = EMPTY_VALUE;
				}
				chkQuestionValue.setValue(true);
			}

			if(questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE){
				OptionDef optionDef = ((OptionDef)questionDef.getOptionWithValue(value));
				if(optionDef != null)
					sValue = optionDef.getText();
			}
			else if(questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE){
				String[] options = sValue.split(LIST_SEPARATOR);
				if(options == null || options.length == 0)
					sValue = null;
				else{
					sValue = "";
					for(int i=0; i<options.length; i++){
						OptionDef optionDef = questionDef.getOptionWithValue(options[i]);
						if(optionDef != null){
							if(sValue.length() > 0)
								sValue += LIST_SEPARATOR;
							sValue += optionDef.getText();
						}
					}
				}
			}
			else if(questionDef.getDataType() == QuestionDef.QTN_TYPE_BOOLEAN){
				if(sValue.equals(QuestionDef.TRUE_VALUE))
					sValue = QuestionDef.TRUE_DISPLAY_VALUE;
				else if(sValue.equals(QuestionDef.FALSE_VALUE))
					sValue = QuestionDef.FALSE_DISPLAY_VALUE;
			}
		}
		else
			sValue = EMPTY_VALUE;

		valueHyperlink.setText(sValue);
	}

	public void setFormDef(FormDef formDef){
		this.formDef = formDef;
		//setupPopup();
	}

	private void setupPopup(){
		//txtValue1.removeKeyboardListener(keyboardListener1);
		if(handlerReg1 != null)
			handlerReg1.removeHandler();

		txtValue1 = new TextBox();

		txtValue1.addKeyPressHandler(new KeyPressHandler(){
			public void onKeyPress(KeyPressEvent event) {
				if(event.getCharCode() == KeyCodes.KEY_ENTER)
					stopEdit(true);
			}
		});

		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();

		//for(int i=0; i<formDef.getPageCount(); i++)
		//	FormDesignerUtil.loadQuestions(formDef.getPageAt(i).getQuestions(),questionDef,oracle,false,questionDef.getDataType() != QuestionDef.QTN_TYPE_REPEAT);

		//what does the sameTypesOnly variable do?
		//FormDesignerUtil.loadQuestions(formDef.getChildren(),questionDef,oracle,questionDef.getDataType() != QuestionDef.QTN_TYPE_REPEAT,parentQuestionDef);
		FormDesignerUtil.loadQuestions(formDef.getChildren(),questionDef,oracle,false,parentQuestionDef);

		sgstField = new SuggestBox(oracle,txtValue1);
		//selectFirstQuestion();

		sgstField.addSelectionHandler(new SelectionHandler(){
			public void onSelection(SelectionEvent event){
				stopEdit(true);
			}
		});

		/*sgstField.addFocusListener(new FocusListenerAdapter(){
			public void onLostFocus(Widget sender){
				stopSelection();
			}
		});*/
	}

	public QuestionDef getValueQtnDef(){
		return valueQtnDef;
	}

	public void setValueQtnDef(QuestionDef valueQtnDef){
		this.valueQtnDef = valueQtnDef;
		if(valueQtnDef != null)
		{
			chkQuestionValue.setValue(true);
		}
	}
	
	public void setParentQuestionDef(QuestionDef questionDef){
		this.parentQuestionDef = questionDef;
	}
}
