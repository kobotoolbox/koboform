package org.openrosa.client.view;

import java.util.HashMap;

import org.openrosa.client.Context;
import org.openrosa.client.controller.FormDesignerController;
import org.openrosa.client.controller.IFormChangeListener;
import org.openrosa.client.model.DataDef;
import org.openrosa.client.model.DataInstanceDef;
import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.GroupDef;
import org.openrosa.client.model.IFormElement;
import org.openrosa.client.model.ItemSetDef;
import org.openrosa.client.model.ItextModel;
import org.openrosa.client.model.OptionDef;
import org.openrosa.client.model.QuestionDef;
import org.openrosa.client.postprocess.PostProcessProperties;
import org.openrosa.client.util.FormDesignerUtil;
import org.openrosa.client.util.FormHandler;
import org.purc.purcforms.client.controller.IFormActionListener;
import org.purc.purcforms.client.controller.IFormSelectionListener;
import org.purc.purcforms.client.controller.ItemSelectionListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.PageDef;
import org.purc.purcforms.client.util.FormUtil;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * View responsible for displaying and hence allow editing of form, page,
 * question, or question option properties.
 * 
 * @author daniel
 * @author GHendrick Removed all Desc Template references as well as calculation
 *         references Removed support for Dynamic Lists by removing the
 *         DynamicListsView
 */
public class PropertiesView extends Composite implements
		IFormSelectionListener, ItemSelectionListener {
	
		
	/** List box index for no selected data type. */
	private static final byte DT_INDEX_NONE = -1;

	/** List box index for text data type. */
	private static final byte DT_INDEX_TEXT = 0;

	/** List box index for number data type. */
	private static final byte DT_INDEX_NUMBER = 1;

	/** List box index for decimal data type. */
	private static final byte DT_INDEX_DECIMAL = 2;

	/** List box index for date data type. */
	private static final byte DT_INDEX_DATE = 3;

	/** List box index for time data type. */
	private static final byte DT_INDEX_TIME = 4;

	/** List box index for dateTime data type. */
	private static final byte DT_INDEX_DATE_TIME = 5;

	/** List box index for boolean data type. */
	private static final byte DT_INDEX_BOOLEAN = 6;

	/** List box index for single select data type. */
	private static final byte DT_INDEX_SINGLE_SELECT = 7;

	/** List box index for multiple select data type. */
	private static final byte DT_INDEX_MULTIPLE_SELECT = 8;

	/** List box index for repeat data type. */
	private static final byte DT_INDEX_REPEAT = 9;

	/** List box index for image data type. */
	private static final byte DT_INDEX_IMAGE = 10;

	/** List box index for video data type. */
	private static final byte DT_INDEX_VIDEO = 11;

	/** List box index for audio data type. */
	private static final byte DT_INDEX_AUDIO = 12;

	/** List box index for gps data type. */
	private static final byte DT_INDEX_GPS = 13;

	/** List box index for barcode data type. */
	private static final byte DT_INDEX_BARCODE = 14;

	/** List box index for group data type. */
	private static final byte DT_INDEX_TRIGGER = 15;

	/** List box index for group data type. */
	private static final byte DT_INDEX_GROUP = 16;

	/** Table used for organizing widgets in a table format. */
	private FlexTable table = new FlexTable();
	
	/** Tabled used for organizing widgets for the advanced features*/
	private FlexTable advancedTable = new FlexTable();

	/** Widget for displaying the list of data types. */
	private ListBox cbDataType = new ListBox(false);

	/** Widget for setting the visibility property. */
	private CheckBox chkVisible = new CheckBox();

	/** Widget for setting the enabled property. */
	private CheckBox chkEnabled = new CheckBox();

	/** Widget for setting the required property. */
	private CheckBox chkRequired = new CheckBox();
	
	/** Widget for setting if a question uses a itemset*/
	private CheckBox chkUseItemSet = new CheckBox();

	/** Widget for setting the text property. */
	private TextArea txtText = new TextArea();

	/** Widget for setting the help text property. */
	private TextBox txtHelpText = new TextBox();

	/** Widget for setting the binding property. */
	private TextBox txtBinding = new TextBox();

	/** Widget for setting the Question ID property. */
	private TextBox qtnID = new TextBox();

	/** Widget for setting the default value property. */
	private TextBox txtDefaultValue = new TextBox();
	
	/** Widget for setting Where the XML comes from. */
	private TextBox txtXMLFromFolder = new TextBox();
	
	/** Widget for setting the preload. */
	private TextBox txtPreload = new TextBox();
	
	/** Widget for setting the preload param. */
	private TextBox txtPreloadParam = new TextBox();
	
	/** Widget for displaying the Post Processor applet */
	private HTML postProcessorApplet = new HTML();
	
	/** Widget for validator applet*/
	private HTML validatorApplet = new HTML();
	
	/** Lets users edit data attributes, obviously **/
	private DataAttributesView dataAttributesView = new DataAttributesView();
	
	/** Lets users edit item sets**/
	private ItemSetPropertiesView itemSetView = new ItemSetPropertiesView();
	
	
	
	// private TextBox txtCalculation = new TextBox();
	// private DescTemplateWidget btnCalculation;

	/**
	 * The selected object which could be FormDef, PageDef, QuestionDef or
	 * OptionDef
	 */
	private Object propertiesObj;

	/** Listener to form change events. */
	private IFormChangeListener formChangeListener;

	/** Widget for defining skip rules. */
	private SkipRulesView skipRulesView = new SkipRulesView();

	/** Widget for defining validation rules. */
	private ValidationRulesView validationRulesView = new ValidationRulesView();

	/** Listener to form action events. */
	private IFormActionListener formActionListener;
	
	Hyperlink btnAdvancedProperties = new Hyperlink(LocaleText.get("showAdvancedProperties"), "");
	Label lblText = new Label(LocaleText.get("qtnText"));
	Label lblQtnID = new Label(LocaleText.get("qtnId"));
	Label lblHelpText = new Label(LocaleText.get("qtnHelpText"));
	Label lblDataType = new Label(LocaleText.get("qtnType"));
	Label lblBinding = new Label(LocaleText.get("qtnBinding"));
	Label lblVisible = new Label(LocaleText.get("qtnVisible"));
	Label lblEnabled = new Label(LocaleText.get("qtnEnabled"));
	Label lblRequired = new Label(LocaleText.get("qtnRequired"));
	Label lblDefault = new Label(LocaleText.get("qtnDefaultValue"));
	Label lblXMLFromFolder = new Label(LocaleText.get("qtnXMLFromFolder"));
	Label lblUseItemSet = new Label(LocaleText.get("useItemSet"));
	
	Label lblPreload = new Label(LocaleText.get("qtnPreload"));
	Label lblPreloadParam = new Label(LocaleText.get("qtnPreloadParam"));
	
	Label lblSpacer = new Label(" ");
	
	ToggleButton qtnIDHint;
	ToggleButton txtTextHint;
	ToggleButton txtHelpTextHint;
	ToggleButton cbDataTypeHint;
	ToggleButton txtBindingHint;
	ToggleButton chkVisibleHint;
	ToggleButton chkEnabledHint;
	ToggleButton chkRequiredHint;
	ToggleButton txtDefaultValueHint;
	ToggleButton txtXMLFromFolderHint;
	ToggleButton txtPreloadHint;
	ToggleButton txtPreloadParamHint;
	ToggleButton useItemSetHint;

	// Tab panel for holding skip, validation logic and dynamic lists.
	DecoratedTabPanel tabs = new DecoratedTabPanel();

	/**
	 * Creates a new instance of the properties view widget.
	 */
	public PropertiesView() {

		
		lblSpacer.getElement().getStyle().setHeight(15, Unit.PX);
		lblSpacer.getElement().getStyle().setMargin(15, Unit.PX);

		String htmlStr = "<APPLET codebase=\"postprocessorapplet/\" "
			+"archive=\"kobo_postproc_applet.jar, KoboSync_0.93.jar\" "
			+"CODE=\"org.oyrm.kobo.postproc.ui.KoboPostProcApplet.class\" "
			+"WIDTH=\"600\" HEIGHT=\"300\">"
			+"<param name=\"XML_AGGREGATE_COMMAND\" value=\""+LocaleText.get("XML_AGGREGATE_COMMAND")+"\">"
			+"<param name=\"CSV_CONVERT_COMMAND\" value=\""+LocaleText.get("CSV_CONVERT_COMMAND")+"\">"
			+"<param name=\"STATUS_INIT\" value=\""+LocaleText.get("STATUS_INIT")+"\">"
			+"<param name=\"COUNTER_SYNC_TEXT\" value=\""+LocaleText.get("COUNTER_SYNC_TEXT")+"\">"
			+"<param name=\"COUNTER_TRANS_TEXT\" value=\""+LocaleText.get("COUNTER_TRANS_TEXT")+"\">"
			+"<param name=\"BROWSE_TEXT\" value=\""+LocaleText.get("BROWSE_TEXT")+"\">"
			+"<param name=\"CHANGE_CSV_DIR_TEXT\" value=\""+LocaleText.get("CHANGE_CSV_DIR_TEXT")+"\">"
			+"<param name=\"CHANGE_SRC_DIR_TEXT\" value=\""+LocaleText.get("CHANGE_SRC_DIR_TEXT")+"\">"
			+"<param name=\"CHANGE_XML_DIR_TEXT\" value=\""+LocaleText.get("CHANGE_XML_DIR_TEXT")+"\">"
			+"<param name=\"CSV_CONVERT_PROC_COMPLETE_TEXT\" value=\""+LocaleText.get("CSV_CONVERT_PROC_COMPLETE_TEXT")+"\">"
			+"<param name=\"TASK_COMPLETED_TEXT\" value=\""+LocaleText.get("TASK_COMPLETED_TEXT")+"\">"
			+"<param name=\"COMPLETED_PERCENT_TEXT\" value=\""+LocaleText.get("COMPLETED_PERCENT_TEXT")+"\">"
			+"<param name=\"XML_AGGREGATE_COMPLETE_TEXT\" value=\""+LocaleText.get("XML_AGGREGATE_COMPLETE_TEXT")+"\">"
			+"<param name=\"RETRY_TEXT\" value=\""+LocaleText.get("RETRY_TEXT")+"\">"
			+"<param name=\"SET_TEXT\" value=\""+LocaleText.get("SET_TEXT")+"\">"
			+"<param name=\"STRING_NODIR_MESSAGE\" value=\""+LocaleText.get("STRING_NODIR_MESSAGE")+"\">"
			+"<param name=\"STRING_NODIR_CSV\" value=\""+LocaleText.get("STRING_NODIR_CSV")+"\">"
			+"<param name=\"STRING_NODIR_TITLE\" value=\""+LocaleText.get("STRING_NODIR_TITLE")+"\">"
			+"<param name=\"STARTING_TEXT\" value=\""+LocaleText.get("STARTING_TEXT")+"\">"
			+"<param name=\"WRITING_XML_TO_STORAGE\" value=\""+LocaleText.get("WRITING_XML_TO_STORAGE")+"\">"
			+"<param name=\"CONVERT_TO_CSV_TASK_TEXT\" value=\""+LocaleText.get("CONVERT_TO_CSV_TASK_TEXT")+"\">"
			+"<param name=\"AGGREGATE_XML_TASK_TEXT\" value=\""+LocaleText.get("AGGREGATE_XML_TASK_TEXT")+"\">"
			+"<param name=\"DIR_PREF_SET_TEXT\" value=\""+LocaleText.get("DIR_PREF_SET_TEXT")+"\">"
			+"<param name=\"STATUS_TEXT\" value=\""+LocaleText.get("STATUS_TEXT")+"\">"
			+"<param name=\"CONVERT_TO_CSV_TEXT\" value=\""+LocaleText.get("CONVERT_TO_CSV_TEXT")+"\">"
			+"<param name=\"AGGREGATE_XML_TEXT\" value=\""+LocaleText.get("AGGREGATE_XML_TEXT")+"\">"
			+"<param name=\"COUNTER_TEXT\" value=\""+LocaleText.get("COUNTER_TEXT")+"\">"
			+"<param name=\"SURVEY_INSTANCES_TEXT\" value=\""+LocaleText.get("SURVEY_INSTANCES_TEXT")+"\">"
			+"<param name=\"SAVE_TO_CSV_TEXT\" value=\""+LocaleText.get("SAVE_TO_CSV_TEXT")+"\">"
			+"<param name=\"AGGREGATE_TO_TEXT\" value=\""+LocaleText.get("AGGREGATE_TO_TEXT")+"\">"		
			+"</APPLET>";
		
		postProcessorApplet.setHTML(htmlStr);
		
		String validatorAppletString = "<APPLET codebase=\"validatorapplet/\" "
			+"archive=\"FormBuilderValidatorApplet.jar, ODK_Validate_v1.6.jar, plugin.jar\" "
			+"CODE=\"org.oyrm.kobo.formBuilder.validatorApplet.ValidatorApplet.class\" "
			+"WIDTH=\"600\" HEIGHT=\"400\">"
			+"</APPLET>";
		validatorApplet.setHTML(validatorAppletString);
		
		int row0 = 0;
		table.setWidget(row0++, 0, lblText);		
		table.setWidget(row0++, 0, lblHelpText);
		table.setWidget(row0++, 0, lblDataType);
		table.setWidget(row0++, 0, lblUseItemSet);
		table.setWidget(row0++, 0, lblSpacer);
		table.setWidget(row0++, 0, lblSpacer);
		table.setWidget(row0++, 0, lblSpacer);
		table.setWidget(row0++, 0, lblSpacer);
		table.setWidget(row0++, 0, lblSpacer);		
		table.setWidget(row0++, 0, lblSpacer); //just a place holder
		
		int row0a = 0;		
		advancedTable.setWidget(row0a++, 0, lblBinding);
		advancedTable.setWidget(row0a++, 0, lblQtnID);		
		advancedTable.setWidget(row0a++, 0, lblVisible);
		advancedTable.setWidget(row0a++, 0, lblEnabled);
		advancedTable.setWidget(row0a++, 0, lblRequired);
		advancedTable.setWidget(row0a++, 0, lblDefault);
		advancedTable.setWidget(row0a++, 0, lblPreload);
		advancedTable.setWidget(row0a++, 0, lblPreloadParam);
		advancedTable.setWidget(row0a++, 0, lblXMLFromFolder); 
		advancedTable.setWidget(row0a++, 0, new Label("")); // just as a place holder

		int row1 = 0;
		table.setWidget(row1++, 1, txtText);		
		table.setWidget(row1++, 1, txtHelpText);
		table.setWidget(row1++, 1, cbDataType);
		table.setWidget(row1++, 1, chkUseItemSet);
		table.setWidget(row1++, 1, dataAttributesView);
		table.setWidget(row1++, 1, itemSetView);
		table.setWidget(row1++, 1, postProcessorApplet);
		table.setWidget(row1++, 1, validatorApplet);
		table.setWidget(row1++, 1, lblSpacer);
		table.setWidget(row1++, 1, btnAdvancedProperties);
		
		int row1a = 0;
		advancedTable.setWidget(row1a++, 1, txtBinding);
		advancedTable.setWidget(row1a++, 1, qtnID);
		advancedTable.setWidget(row1a++, 1, chkVisible);
		advancedTable.setWidget(row1a++, 1, chkEnabled);
		advancedTable.setWidget(row1a++, 1, chkRequired);
		advancedTable.setWidget(row1a++, 1, txtDefaultValue);
		advancedTable.setWidget(row1a++, 1, txtPreload);
		advancedTable.setWidget(row1a++, 1, txtPreloadParam);


		SelectionListener<ButtonEvent> hintListener = new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (ce.getButton() instanceof ToggleButton) {
					if (!((ToggleButton) ce.getButton()).isPressed()) {
						ce.getButton().getToolTip().getToolTipConfig()
								.setHideDelay(100);
						ce.getButton().getToolTip().hide();
					} else {
						ce.getButton().getToolTip().getToolTipConfig()
								.setHideDelay(100000);
						ce.getButton().getToolTip().show();
					}
				}
			}
		};

		qtnIDHint = new ToggleButton("?", hintListener);
		txtTextHint = new ToggleButton("?", hintListener);
		txtHelpTextHint = new ToggleButton("?", hintListener);
		cbDataTypeHint = new ToggleButton("?", hintListener);
		txtBindingHint = new ToggleButton("?", hintListener);
		chkVisibleHint = new ToggleButton("?", hintListener);
		chkEnabledHint = new ToggleButton("?", hintListener);
		chkRequiredHint = new ToggleButton("?", hintListener);
		txtDefaultValueHint = new ToggleButton("?", hintListener);
		txtXMLFromFolderHint = new ToggleButton("?", hintListener);
		txtPreloadHint = new ToggleButton("?", hintListener);
		txtPreloadParamHint = new ToggleButton("?", hintListener);
		useItemSetHint = new ToggleButton("?", hintListener);
		
		qtnIDHint.setToolTip(LocaleText.get("questionIdDesc"));
		qtnIDHint.getToolTip().getToolTipConfig().setShowDelay(100);
		qtnIDHint.getToolTip().getToolTipConfig().setDismissDelay(0);
		txtTextHint.setToolTip(LocaleText.get("questionTextDesc"));
		txtTextHint.getToolTip().getToolTipConfig().setShowDelay(100);
		txtTextHint.getToolTip().getToolTipConfig().setDismissDelay(0);
		txtHelpTextHint.setToolTip(LocaleText.get("questionDescDesc"));
		txtHelpTextHint.getToolTip().getToolTipConfig().setShowDelay(100);
		txtHelpTextHint.getToolTip().getToolTipConfig().setDismissDelay(0);
		cbDataTypeHint.setToolTip(LocaleText.get("questionTypeDesc"));
		cbDataTypeHint.getToolTip().getToolTipConfig().setShowDelay(100);
		cbDataTypeHint.getToolTip().getToolTipConfig().setDismissDelay(0);
		txtBindingHint.setToolTip(LocaleText.get("questionBindingDesc"));
		txtBindingHint.getToolTip().getToolTipConfig().setShowDelay(100);
		txtBindingHint.getToolTip().getToolTipConfig().setDismissDelay(0);
		chkVisibleHint.setToolTip(LocaleText.get("visibleDesc"));
		chkVisibleHint.getToolTip().getToolTipConfig().setShowDelay(100);
		chkVisibleHint.getToolTip().getToolTipConfig().setDismissDelay(0);
		chkEnabledHint.setToolTip(LocaleText.get("enabledDesc"));
		chkEnabledHint.getToolTip().getToolTipConfig().setShowDelay(100);
		chkEnabledHint.getToolTip().getToolTipConfig().setDismissDelay(0);
		chkRequiredHint.setToolTip(LocaleText.get("requiredDesc"));
		chkRequiredHint.getToolTip().getToolTipConfig().setShowDelay(100);
		chkRequiredHint.getToolTip().getToolTipConfig().setDismissDelay(0);
		txtDefaultValueHint.setToolTip(LocaleText.get("defaultValDesc"));
		txtDefaultValueHint.getToolTip().getToolTipConfig().setShowDelay(100);
		txtDefaultValueHint.getToolTip().getToolTipConfig().setDismissDelay(0);
		txtXMLFromFolderHint.setToolTip(LocaleText.get("xmlFromFolderDesc"));
		txtXMLFromFolderHint.getToolTip().getToolTipConfig().setShowDelay(100);
		txtXMLFromFolderHint.getToolTip().getToolTipConfig().setDismissDelay(0);		
		txtPreloadHint.setToolTip(LocaleText.get("preloadDesc"));
		txtPreloadHint.getToolTip().getToolTipConfig().setShowDelay(100);
		txtPreloadHint.getToolTip().getToolTipConfig().setDismissDelay(0);		
		txtPreloadParamHint.setToolTip(LocaleText.get("preloadParamDesc"));
		txtPreloadParamHint.getToolTip().getToolTipConfig().setShowDelay(100);
		txtPreloadParamHint.getToolTip().getToolTipConfig().setDismissDelay(0);
		useItemSetHint.setToolTip(LocaleText.get("useItemSetHint"));
		useItemSetHint.getToolTip().getToolTipConfig().setShowDelay(100);
		useItemSetHint.getToolTip().getToolTipConfig().setDismissDelay(0);
		
		 
		int row2 = 0;
		table.setWidget(row2++, 2, txtTextHint);				
		table.setWidget(row2++, 2, txtHelpTextHint);
		table.setWidget(row2++, 2, cbDataTypeHint);
		table.setWidget(row2++, 2, useItemSetHint);
		table.setWidget(row2++, 2, lblSpacer);
		table.setWidget(row2++, 2, lblSpacer);
		table.setWidget(row2++, 2, lblSpacer);
		table.setWidget(row2++, 2, lblSpacer);
		table.setWidget(row2++, 2, lblSpacer);
		table.setWidget(row2++, 2, lblSpacer);

		int row2a = 0;
		advancedTable.setWidget(row2a++, 2, txtBindingHint);
		advancedTable.setWidget(row2a++, 2, qtnIDHint);
		advancedTable.setWidget(row2a++, 2, chkVisibleHint);
		advancedTable.setWidget(row2a++, 2, chkEnabledHint);
		advancedTable.setWidget(row2a++, 2, chkRequiredHint);
		advancedTable.setWidget(row2a++, 2, txtDefaultValueHint);
		advancedTable.setWidget(row2a++, 2, txtPreloadHint);
		advancedTable.setWidget(row2a++, 2, txtPreloadParamHint);
		//table.setWidget(9, 2, txtXMLFromFolderHint);

		HorizontalPanel panel = new HorizontalPanel();
		FormUtil.maximizeWidget(panel);

		advancedTable.setWidget(row1a++, 1, panel);

		panel = new HorizontalPanel();
		FormUtil.maximizeWidget(panel);

		advancedTable.setWidget(row1a++, 1, panel);

		table.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		table.getElement().getStyle().setBorderColor("black");
		table.getElement().getStyle().setBorderWidth(0, Unit.PX);
		table.getElement().getStyle().setPadding(0, Unit.PX);
		
		advancedTable.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		advancedTable.getElement().getStyle().setBorderColor("grey");
		advancedTable.getElement().getStyle().setBorderWidth(1, Unit.PX);
		advancedTable.getElement().getStyle().setPadding(2, Unit.PX);
		advancedTable.getElement().getStyle().setMargin(5, Unit.PX);

		cbDataType.addItem(LocaleText.get("qtnTypeText"));
		cbDataType.addItem(LocaleText.get("qtnTypeNumber"));
		cbDataType.addItem(LocaleText.get("qtnTypeDecimal"));
		cbDataType.addItem(LocaleText.get("qtnTypeDate"));
		cbDataType.addItem(LocaleText.get("qtnTypeTime"));
		cbDataType.addItem(LocaleText.get("qtnTypeDateTime"));
		cbDataType.addItem(LocaleText.get("qtnTypeBoolean"));
		cbDataType.addItem(LocaleText.get("qtnTypeSingleSelect"));
		cbDataType.addItem(LocaleText.get("qtnTypeMultSelect"));
		cbDataType.addItem(LocaleText.get("qtnTypeRepeat"));
		cbDataType.addItem(LocaleText.get("qtnTypePicture"));
		cbDataType.addItem(LocaleText.get("qtnTypeVideo"));
		cbDataType.addItem(LocaleText.get("qtnTypeAudio"));
		cbDataType.addItem(LocaleText.get("qtnTypeGPS"));
		cbDataType.addItem(LocaleText.get("qtnTypeBarcode"));
		cbDataType.addItem(LocaleText.get("qtnTypeTrigger"));
		cbDataType.addItem(LocaleText.get("qtnTypeGroup"));
		cbDataType.setSelectedIndex(-1);

		FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
		
		cellFormatter.setHorizontalAlignment(row1++, 1,
				HasHorizontalAlignment.ALIGN_CENTER);
		
		FlexCellFormatter cellFormatterAdvanced = advancedTable.getFlexCellFormatter();		
		cellFormatterAdvanced.setHorizontalAlignment(row1++, 1,
				HasHorizontalAlignment.ALIGN_CENTER);

		qtnID.setWidth("100%");
		txtText.setWidth("100%");
		txtText.setHeight("70px");
		txtHelpText.setWidth("100%");
		txtBinding.setWidth("100%");
		txtDefaultValue.setWidth("100%");
		txtXMLFromFolder.setWidth("100%");
		cbDataType.setWidth("100%");

		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setSpacing(0);
		verticalPanel.add(table);
		verticalPanel.add(advancedTable);

		tabs.add(skipRulesView, LocaleText.get("skipLogic"));
		tabs.add(validationRulesView, LocaleText.get("validationLogic"));
		tabs.selectTab(0);
		
		verticalPanel.add(tabs);
		verticalPanel.setWidth("100%");
		initWidget(verticalPanel);

		setupEventListeners();
		enableQuestionOnlyProperties(false);
		tabs.setVisible(false);
		table.setVisible(false);
		advancedTable.setVisible(false);
		DOM.sinkEvents(getElement(), Event.ONKEYDOWN
				| DOM.getEventsSunk(getElement()));
	}
	
	

	/**
	 * Sets up event listeners.
	 */
	private void setupEventListeners() {
		
		btnAdvancedProperties.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(advancedTable.isVisible())
				{
					hideAdvancedProperties();
				}
				else
				{
					showAdvancedProperties();
				}
			}
		});
		
		chkVisible.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				FormDesignerController.makeDirty();
				((QuestionDef) propertiesObj)
						.setVisible(chkVisible.getValue() == true);
				propertiesObj = formChangeListener
						.onFormItemChanged(propertiesObj);
			}
		});

		chkEnabled.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				FormDesignerController.makeDirty();
				((QuestionDef) propertiesObj)
						.setEnabled(chkEnabled.getValue() == true);
				propertiesObj = formChangeListener
						.onFormItemChanged(propertiesObj);
			}
		});

		chkRequired.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				FormDesignerController.makeDirty();
				((QuestionDef) propertiesObj).setRequired(chkRequired
						.getValue() == true);
				propertiesObj = formChangeListener
						.onFormItemChanged(propertiesObj);
			}
		});
		
		chkUseItemSet.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				FormDesignerController.makeDirty();
				setUseItemSet();
			}
		});

		txtDefaultValue.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updateDefaultValue();
			}
		});
		txtDefaultValue.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				updateDefaultValue();
			}
		});

		txtDefaultValue.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER
						|| event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
					txtPreload.setFocus(true);					
				}
			}
		});

		txtHelpText.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updateHelpText();
			}
		});
		txtHelpText.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				updateHelpText();
			}
		});

		txtHelpText.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				int keyCode = event.getNativeKeyCode();
				if (keyCode == KeyCodes.KEY_ENTER
						|| keyCode == KeyCodes.KEY_DOWN)
					cbDataType.setFocus(true);
				else if (keyCode == KeyCodes.KEY_UP) {
					txtText.setFocus(true);
					txtText.selectAll();
				}
			}
		});

		
		txtBinding.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				String s = txtBinding.getText();

				s = PropertiesView.removeShadyCharacters(s);

				
				txtBinding.setText(s);
				qtnID.setText(s);
				updateBinding();				
				updateID();				
			}
		});
		/*
		txtBinding.addBlurHandler(new BlurHandler() {
			public void onBlur(BlurEvent event) {
				doubleCheckBinding();
				qtnID.setText(txtBinding.getText());
				updateID();
			}
		});
		*/
		
		
		txtBinding.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				String s = txtBinding.getText();

				s = PropertiesView.removeShadyCharacters(s);

				txtBinding.setText(s);
				qtnID.setText(s);				
				updateBinding();
				updateID();
				
				
			}
		});

		txtBinding.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_UP) {
					if (cbDataType.isEnabled())
						cbDataType.setFocus(true);
					else {
						txtText.setFocus(true);
						txtText.selectAll();
					}
				}
			}
		});

		
		txtBinding.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (propertiesObj instanceof PageDef) {
					if (!Character.isDigit(event.getCharCode())) {
						((TextBox) event.getSource()).cancelKey();
						return;
					}
				} 
				
				else if (propertiesObj instanceof FormDef
						|| propertiesObj instanceof QuestionDef) {
					
					char ch = event.getCharCode();
					if(ch == '#' || ch =='%'
					 || ch == '('
					 || ch == ')'
					 || ch == '!'
					 || ch == '&'
				     || ch == '@'
					 || ch == '\''
					 || ch == '\"'
					 || ch == '$'
					 || ch == '#'
					 || ch == '<'
					 || ch == '>'
				     || ch == '['
					 || ch == ']'
					 || ch == '{'
					 || ch == '}'
					 || ch == ':'
 					 || ch == ';'
 					 || ch == '/'
					 || ch == '\\'
					 || ch == '|'
					 || ch == '+'
					 || ch == '='
					 || ch == ' ' 
					 || ch == '^'
					 || ch == ','
					 || ch == '?'
					 || ch == '`'
					 || ch == '~'							 
					 || ch == '*')
					{
						((TextBox) event.getSource()).cancelKey();
						return;
					}
					
					if (((TextBox) event.getSource()).getCursorPos() == 0) {
						if (Character.isDigit(event.getCharCode())) {
							((TextBox) event.getSource()).cancelKey();
							return;
						}
					} 
					
				} // else OptionDef varname can be anything
				
			}
		});
		
		
		txtText.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updateText();
			}
		});
		txtText.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				updateText();
			}
		});

		txtText.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER
						|| event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
					qtnID.setFocus(true);					
				}
			}
		});
		
		
		txtPreload.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updatePreload();
			}
		});
		txtPreload.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				updatePreload();
			}
		});

		txtPreload.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER
						|| event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
					txtPreloadParam.setFocus(true);					
				}
			}
		});
		
		
		txtPreloadParam.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updatePreloadParam();
			}
		});
		txtPreloadParam.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				updatePreloadParam();
			}
		});
		
		txtPreloadParam.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER
						|| event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
					//txtPreloadParam.setFocus(true);					
				}
			}
		});

		qtnID.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				updateID();
				propertiesObj = formChangeListener.onFormItemChanged(propertiesObj);
			}
		});

		qtnID.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER
						|| event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
					txtHelpText.setFocus(true);
					
				}
			}
		});

		cbDataType.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				//updateDataType();
			}
		});
		cbDataType.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updateDataType();
			}
		});
		
		cbDataType.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				int keyCode = event.getNativeEvent().getKeyCode();
				if (keyCode == KeyCodes.KEY_ENTER
						|| keyCode == KeyCodes.KEY_DOWN) {
					txtBinding.setFocus(true);
					txtBinding.selectAll();
				} else if (keyCode == KeyCodes.KEY_UP) {
					txtHelpText.setFocus(true);
					txtHelpText.selectAll();
				}
			}
		});
	}
	
	
	/**
	 * Use this to clean up bindings and such
	 * @param s
	 * @return
	 */
	public static String removeShadyCharacters(String s)
	{
		s = s.replace("%", "");
		s = s.replace("(", "");
		s = s.replace(")", "");
		s = s.replace("!", "");
		s = s.replace("&", "");
		// s = s.replace(".", ""); //Looks like this is an allowed
		// character in xml node names.
		s = s.replace("'", "");
		s = s.replace("\"", "");
		s = s.replace("$", "");
		s = s.replace("#", "");
		s = s.replace("<", "");
		s = s.replace(">", "");
		s = s.replace("*", "");
		s = s.replace(" ", "_");
		s = s.replace("~", "");
		s = s.replace("`", "");
		s = s.replace("@", "");
		s = s.replace("^", "");
		s = s.replace(" ", "");
		s = s.replace("=", "");
		s = s.replace("[", "");
		s = s.replace("]", "");
		s = s.replace("{", "");
		s = s.replace("}", "");
		s = s.replace("\\", "");
		s = s.replace("/", "");
		s = s.replace("|", "");
		s = s.replace(":", "");
		s = s.replace(";", "");
		s = s.replace(",", "");
		s = s.replace("?", "");
		return s;
	}

	private String getSelObjectOriginalText() {
		if (propertiesObj instanceof FormDef)
			return ((FormDef) propertiesObj).getName();
		else if (propertiesObj instanceof QuestionDef)
			return ((QuestionDef) propertiesObj).getText();
		else if (propertiesObj instanceof OptionDef)
			return ((OptionDef) propertiesObj).getText();
		else if (propertiesObj instanceof GroupDef)
			return ((GroupDef) propertiesObj).getText();
		return null;
	}

	private void updateSelObjBinding(String orgText) {

		if (orgText == null)
			return;

		String orgTextDefBinding = FormDesignerUtil
				.getXmlTagName(getTextWithoutDecTemplate(orgText));

		if (propertiesObj != null && Context.allowBindEdit()
				&& !Context.isStructureReadOnly()) {
			String text = getTextWithoutDecTemplate(txtText.getText().trim());
			String name = FormDesignerUtil.getXmlTagName(text);
			if (propertiesObj instanceof FormDef
					&& ((FormDef) propertiesObj).getVariableName().equals(
							orgTextDefBinding) /* startsWith("newform") */) {
				((FormDef) propertiesObj).setVariableName(name);
				txtBinding.setText(name);

				if (((FormDef) propertiesObj).getItextId().equals(
						orgTextDefBinding)) {
					((FormDef) propertiesObj).setItextId(name);
					qtnID.setText(name);
				}
			} else if (propertiesObj instanceof GroupDef
					&& ((GroupDef) propertiesObj).getName().equals(
							orgTextDefBinding) /* startsWith("newform") */) {
				((GroupDef) propertiesObj).setName(name);
				txtBinding.setText(name);

				if (((GroupDef) propertiesObj).getItextId().equals(
						orgTextDefBinding)) {
					((GroupDef) propertiesObj).setItextId(name);
					qtnID.setText(name);
				}
			} else if (propertiesObj instanceof QuestionDef
					&& ((QuestionDef) propertiesObj).getBinding().equals(
							orgTextDefBinding) /* startsWith("question") */) {
				((QuestionDef) propertiesObj).setBinding(name);
				txtBinding.setText(name);

				if (((QuestionDef) propertiesObj).getItextId().equals(
						orgTextDefBinding)) {
					((QuestionDef) propertiesObj).setItextId(name);
					qtnID.setText(name);
				}
			} else if (propertiesObj instanceof OptionDef
					&& ((OptionDef) propertiesObj).getBinding().equals(
							orgTextDefBinding) /* .startsWith("option") */) {
				((OptionDef) propertiesObj).setBinding(name);
				txtBinding.setText(name);

				if (((OptionDef) propertiesObj).getItextId().equals(
						orgTextDefBinding)) {
					((OptionDef) propertiesObj).setItextId(name);
					qtnID.setText(name);
				}
			}
		}
	}

	/**
	 * Gets text without the description template, for a given text.
	 * 
	 * @param text
	 *            the text to parse.
	 * @return the text without the description template.
	 */
	private String getTextWithoutDecTemplate(String text) {
		if (text.contains("${")) {
			if (text.indexOf("}$") < text.length() - 2)
				text = text.substring(0, text.indexOf("${"))
						+ text.substring(text.indexOf("}$") + 2);
			else
				text = text.substring(0, text.indexOf("${"));
		}
		return text;
	}

	/**
	 * Checks if a given character is allowed to begin an xml node name.
	 * 
	 * @param keyCode
	 *            the character code.
	 * @return true if is allowed, else false.
	 */
	private boolean isAllowedXmlNodeNameStartChar(char keyCode) {
		return ((keyCode >= 'a' && keyCode <= 'z')
				|| (keyCode >= 'A' && keyCode <= 'Z') || isControlChar(keyCode));
	}

	/**
	 * Checks if a character is allowed in an xml node name.
	 * 
	 * @param keyCode
	 *            the character code.
	 * @return true if allowed, else false.
	 */
	private boolean isAllowedXmlNodeNameChar(char keyCode) {
		return isAllowedXmlNodeNameStartChar(keyCode)
				|| Character.isDigit(keyCode) || keyCode == '-'
				|| keyCode == '_' || keyCode == '.';
	}

	/**
	 * Check if a character is a control character. Examples of control
	 * characters are ALT, CTRL, ESCAPE, DELETE, SHIFT, HOME, PAGE_UP,
	 * BACKSPACE, ENTER, TAB, LEFT, and more.
	 * 
	 * @param keyCode
	 *            the character code.
	 * @return true if yes, else false.
	 */
	private boolean isControlChar(char keyCode) {
		int code = (int)keyCode;
		return (code == KeyCodes.KEY_ALT || code == KeyCodes.KEY_BACKSPACE
				|| code == KeyCodes.KEY_CTRL || code == KeyCodes.KEY_DELETE
				|| code == KeyCodes.KEY_DOWN || code == KeyCodes.KEY_END
				|| code == KeyCodes.KEY_ENTER || code == KeyCodes.KEY_ESCAPE
				|| code == KeyCodes.KEY_HOME || code == KeyCodes.KEY_LEFT
				|| code == KeyCodes.KEY_PAGEDOWN || code == KeyCodes.KEY_PAGEUP
				|| code == KeyCodes.KEY_RIGHT || code == KeyCodes.KEY_SHIFT
				|| code == KeyCodes.KEY_TAB || code == KeyCodes.KEY_UP
				|| keyCode == (char)(KeyCodes.KEY_BACKSPACE));
	}

	/**
	 * Updates the selected object with the new text as typed by the user.
	 */
	private void updateText() {
		
		String text = txtText.getText();
		
		//do some testing to make sure there's nothing crazy here
		
		if(text.indexOf("…") != -1)
		{
			text = text.replace("…", "...");
			txtText.setText(text);
		}
		
		if (propertiesObj == null)
			return;
		else if (propertiesObj instanceof QuestionDef)
			((QuestionDef) propertiesObj).setText(text);
		else if (propertiesObj instanceof OptionDef)
			((OptionDef) propertiesObj).setText(text);
		else if (propertiesObj instanceof GroupDef)
			((GroupDef) propertiesObj).setName(text);
		else if (propertiesObj instanceof FormDef)
		{
			((FormDef) propertiesObj).setName(text);
			String binding = PropertiesView.removeShadyCharacters(text);
			((FormDef) propertiesObj).setBinding(binding);
			txtBinding.setText(binding);
			
		}
		else if(propertiesObj instanceof DataDef)
		{
			String s = PropertiesView.removeShadyCharacters(text);
			if(!s.equals(text))
			{
				txtText.setText(s);
			}
			((DataDef)propertiesObj).setName(s);
		}

		propertiesObj = formChangeListener.onFormItemChanged(propertiesObj);
		FormDesignerController.makeDirty();
	}
	
	
	/**
	 * Updates the selected object with the new preload text as typed by the user.
	 */
	private void updatePreload() {
		if (propertiesObj == null)
			return;
		else if (propertiesObj instanceof QuestionDef)
			((QuestionDef) propertiesObj).setPreload(txtPreload.getText());
		

		propertiesObj = formChangeListener.onFormItemChanged(propertiesObj);
		FormDesignerController.makeDirty();
	}
	
	/**
	 * Updates the selected object with the new preload param text as typed by the user.
	 */
	private void updatePreloadParam() {
		if (propertiesObj == null)
			return;
		else if (propertiesObj instanceof QuestionDef)
			((QuestionDef) propertiesObj).setPreloadParam(txtPreloadParam.getText());
		
		FormDesignerController.makeDirty();
		propertiesObj = formChangeListener.onFormItemChanged(propertiesObj);
	}

	/**
	 * Updates the selected object with the new binding as typed by the user.
	 */
	private void updateBinding() {
		if (propertiesObj == null || (txtBinding.getText().trim().length() == 0))
			return;
		else if (propertiesObj instanceof QuestionDef)
			((QuestionDef) propertiesObj).setBinding(txtBinding.getText());
		else if (propertiesObj instanceof OptionDef)
			((OptionDef) propertiesObj).setBinding(txtBinding.getText());
		else if (propertiesObj instanceof FormDef)
			((FormDef) propertiesObj).setVariableName(txtBinding.getText());
		else if (propertiesObj instanceof PageDef) {
			try {
				((PageDef) propertiesObj).setPageNo(Integer.parseInt(txtBinding
						.getText()));
			} catch (Exception ex) {
				return;
			}
		}
		FormDesignerController.makeDirty();
		propertiesObj = formChangeListener.onFormItemChanged(propertiesObj);
	}
	
	
	private void doubleCheckBinding() {
		if (propertiesObj == null || (txtBinding.getText().trim().length() == 0))
			return;
		else if (propertiesObj instanceof QuestionDef)
		{
			QuestionDef questionDef = ((QuestionDef) propertiesObj);
			FormDef formDef = questionDef.getFormDef();
			if(formDef != null)
			{
				IFormElement ife = formDef.getChild(txtBinding.getText().trim());
				if(ife != null && ife != questionDef)
				{
					Window.alert("error it's a duplicate");
				}
			}
		}

	}
	/**
	 * Updates the selected object with the new help text as typed by the user.
	 */
	private void updateHelpText() {
		if (propertiesObj == null)
			return;
		
		FormDesignerController.makeDirty();
		((IFormElement) propertiesObj).setHelpText(txtHelpText.getText());
		propertiesObj = formChangeListener.onFormItemChanged(propertiesObj);
	}

	/**
	 * Updates the selected object with the new itext id as typed by the user.
	 */
	private void updateID() {
		String qtnIdStr = qtnID.getText().trim();
		if (propertiesObj == null)
			return;
		
		//we're dropping the itext ID so act accordingly
		if( qtnIdStr.length() == 0)
		{
			String oldItextId = ((IFormElement)propertiesObj).getItextId();
			((IFormElement)propertiesObj).getFormDef().getITextMap().remove(oldItextId);
			((IFormElement)propertiesObj).setItextId(null);
			return;
		}
		//update the itextMap
		String oldItextId = ((IFormElement)propertiesObj).getItextId();
		ItextModel itext = null;
		if(oldItextId != null)
		{
			FormDef form = ((IFormElement)propertiesObj).getFormDef(); 
			itext = form.getITextMap().get(oldItextId);
			form.getITextMap().remove(oldItextId);
		}
		else
		{
			itext = new ItextModel();
		}
		if(itext == null)
		{
			itext = new ItextModel(); 
		}
		itext.set("id", qtnIdStr);
		FormDef form = ((IFormElement)propertiesObj).getFormDef();
		form.getITextMap().put(qtnIdStr, itext);
		
		if (propertiesObj instanceof QuestionDef)
		{
			QuestionDef formObj = (QuestionDef)propertiesObj;
			formObj.setItextId(qtnIdStr);
			if(formObj.getText().startsWith("Question"+formObj.getOldBinding()))
			{
				txtText.setText("Question"+qtnIdStr);
				formObj.setText("Question"+qtnIdStr);
			}				
		}
		else if (propertiesObj instanceof OptionDef)
		{
			OptionDef formObj = (OptionDef)propertiesObj;
			formObj.setItextId(qtnIdStr);
			if(formObj.getText().startsWith("Question"+formObj.getOldBinding()))
			{
				txtText.setText("Question"+qtnIdStr);
				formObj.setText("Question"+qtnIdStr);
			}				
		}
		else if (propertiesObj instanceof FormDef)
		{
			FormDef formObj = (FormDef)propertiesObj;
			formObj.setItextId(qtnIdStr);
		}
		else if (propertiesObj instanceof GroupDef)
		{
			GroupDef formObj = (GroupDef)propertiesObj;
			formObj.setItextId(qtnIdStr);
		}
		else if (propertiesObj instanceof DataInstanceDef)
		{
			String s = PropertiesView.removeShadyCharacters(qtnIdStr);
			if(!s.equals(qtnIdStr))
			{
				qtnIdStr = s;
				qtnID.setText(qtnIdStr);
			}
			DataInstanceDef dataInstance = (DataInstanceDef)propertiesObj;
			String oldInstanceId = dataInstance.getInstanceId();
			dataInstance.setInstanceId(qtnIdStr);
			dataInstance.setRootNodeName(qtnIdStr);
			FormDef formDef = dataInstance.getFormDef();
			formDef.removeDataInstance(oldInstanceId);
			formDef.addDataInstance(qtnIdStr, dataInstance);
		}
		FormDesignerController.makeDirty();
		propertiesObj = formChangeListener.onFormItemChanged(propertiesObj);
	}

	/**
	 * Updates the selected object with the new default value as typed by the
	 * user.
	 */
	private void updateDefaultValue() {
		if (propertiesObj == null)
			return;
		else if (propertiesObj instanceof QuestionDef)
			((QuestionDef) propertiesObj).setDefaultValue(txtDefaultValue.getText());
		else if (propertiesObj instanceof OptionDef)
		{
			if((txtDefaultValue.getText().indexOf("'") != -1) || (txtDefaultValue.getText().indexOf("\"") != -1))
			{
				Window.alert(LocaleText.get("NoSingleQuotes"));
				txtDefaultValue.setText(txtDefaultValue.getText().replace("'", ""));
				txtDefaultValue.setText(txtDefaultValue.getText().replace("\"", ""));
				return;
				
			}
			((OptionDef) propertiesObj).setDefaultValue(txtDefaultValue.getText());
		}
		
		propertiesObj = formChangeListener.onFormItemChanged(propertiesObj);
		FormDesignerController.makeDirty();
	}

	/**
	 * Updates the selected object with the new data type as typed by the user.
	 */
	private void updateDataType() {
		if (propertiesObj == null)
			return;

		boolean deleteKids = false;
		int index = cbDataType.getSelectedIndex();
		IFormElement questionDef = (IFormElement) propertiesObj;
		if ((questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE || 
				questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE) && 
				!(index == DT_INDEX_SINGLE_SELECT || index == DT_INDEX_MULTIPLE_SELECT)) 
		{
			if (questionDef.getChildCount() > 0 && 
					!Window.confirm(LocaleText.get("changeWidgetTypePrompt"))) 
			{
				index = (questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE) ? DT_INDEX_SINGLE_SELECT
						: DT_INDEX_MULTIPLE_SELECT;
				cbDataType.setSelectedIndex(index);
				return;
			}
			deleteKids = true;
		} 
		else if ((questionDef.getDataType() == QuestionDef.QTN_TYPE_REPEAT) && !(index == DT_INDEX_REPEAT)) 
		{
			if (!Window.confirm(LocaleText.get("changeWidgetTypePrompt"))) 
			{
				index = DT_INDEX_REPEAT;
				cbDataType.setSelectedIndex(index);
				return;
			}
			deleteKids = true;
		}
		else if ((questionDef instanceof GroupDef) && !(index == DT_INDEX_GROUP)) 
		{
			if (!Window.confirm(LocaleText.get("changeWidgetTypePrompt"))) 
			{
				index = DT_INDEX_GROUP;
				cbDataType.setSelectedIndex(index);
				return;
			}
			deleteKids = true;
		}
		
		//If we're making a question into a multi select we need to change the binding and possibly the binding of the kids.
		//check to make sure we're actually changing things
		if(index == DT_INDEX_MULTIPLE_SELECT && questionDef.getDataType() != QuestionDef.QTN_TYPE_LIST_MULTIPLE)
		{
			//check that the multi suffix isn't already there
			if(questionDef.getBinding().indexOf(QuestionDef.MULTI_SUFFIX) == -1 )
			{
				
				//setup the itext stuff now before we make changes
				FormDef formDef = questionDef.getFormDef();
				FormHandler.updateLanguage(Context.getLocale(), Context.getLocale(), formDef);
				HashMap<String,ItextModel> map = questionDef.getFormDef().getITextMap();
				
				//first check the kids				
				for(IFormElement e : questionDef.getChildren())
				{

					OptionDef option = (OptionDef)e;
					if(option.getBinding().indexOf(QuestionDef.MULTI_SUFFIX) != -1)
					{
						continue;
					}
					String oldITextId = option.getItextId();
					String binding = option.getBinding();
					int pos = binding.lastIndexOf("_");
					binding = binding.substring(0, pos) + QuestionDef.MULTI_SUFFIX +  binding.substring(pos);
					option.setBinding(binding);
					option.setItextId(binding);
					ItextModel itext = map.get(oldITextId);
					map.remove(oldITextId);
					itext.set("id", binding);
					map.put(binding, itext);
				}
				
				
				
				String oldITextId = questionDef.getItextId();
				questionDef.setBinding(questionDef.getBinding()+ QuestionDef.MULTI_SUFFIX);
				questionDef.setItextId(questionDef.getBinding());
				txtBinding.setText(questionDef.getBinding());

				ItextModel itext = map.get(oldITextId);
				map.remove(oldITextId);
				itext.set("id", questionDef.getItextId());
				map.put(questionDef.getItextId(), itext);
				
				//check for a hint
				if(map.containsKey(oldITextId+"-hint"))
				{
					oldITextId = oldITextId + "-hint";
					itext = map.get(oldITextId);
					map.remove(oldITextId);
					itext.set("id", questionDef.getItextId() + "-hint");
					map.put(questionDef.getItextId() + "-hint", itext);
				}
			}
		}
		//If we're making a question that was a multi select into something else then get rid of MULTI
		else if(index != DT_INDEX_MULTIPLE_SELECT && questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE)
		{
			//check that the multi string is in there
			int strPos = questionDef.getBinding().lastIndexOf(QuestionDef.MULTI_SUFFIX);
			if(strPos != -1)
			{
				//setup the itext stuff now before we make changes
				FormDef formDef = questionDef.getFormDef();
				FormHandler.updateLanguage(Context.getLocale(), Context.getLocale(), formDef);
				HashMap<String,ItextModel> map = formDef.getITextMap();
				
				//first check the kids				
				for(IFormElement e : questionDef.getChildren())
				{

					OptionDef option = (OptionDef)e;
					if(option.getBinding().indexOf(QuestionDef.MULTI_SUFFIX) == -1)
					{
						continue;
					}
					String oldITextId = option.getItextId();
					String binding = option.getBinding();
					binding = binding.replace(QuestionDef.MULTI_SUFFIX, "");
					option.setBinding(binding);
					option.setItextId(binding);
					ItextModel itext = map.get(oldITextId);
					map.remove(oldITextId);
					itext.set("id", binding);
					map.put(binding, itext);
				}
				
				
				String oldITextId = questionDef.getItextId();
				String bindingStr = questionDef.getBinding();
				bindingStr = bindingStr.substring(0, strPos);
				questionDef.setBinding(bindingStr);
				txtBinding.setText(bindingStr);
				questionDef.setItextId(bindingStr);
				
				ItextModel itext = map.get(oldITextId);
				map.remove(oldITextId);
				itext.set("id", questionDef.getItextId());
				map.put(questionDef.getItextId(), itext);
				
				//check for a hint
				if(map.containsKey(oldITextId+"-hint"))
				{
					oldITextId = oldITextId + "-hint";
					itext = map.get(oldITextId);
					map.remove(oldITextId);
					itext.set("id", questionDef.getItextId() + "-hint");
					map.put(questionDef.getItextId() + "-hint", itext);
				}
				
			}
		}
			

		int prevDataType = questionDef.getDataType();
		FormDesignerController.makeDirty();
		// cbDataType.setSelectedIndex(index);
		if (deleteKids)
		{
			formChangeListener.onDeleteChildren(propertiesObj);
			cbDataType.setItemSelected(index, true);
		}
		propertiesObj = setQuestionDataType(index, (IFormElement) propertiesObj);

		propertiesObj = formChangeListener.onFormItemChanged(propertiesObj);
		

		Context.getEventBus().fireDataTypeChangeEvent(questionDef, prevDataType);
		
		formChangeListener.setItemAsSelected((IFormElement)propertiesObj);
	}

	/**
	 * Sets the data type of a question definition object basing on selection in
	 * the type selection list box widget.
	 * 
	 * @param questionDef
	 *            the question definition object.
	 */
	private IFormElement setQuestionDataType(int newDataType, IFormElement questionDef) {
		int dataType = QuestionDef.QTN_TYPE_TEXT;

		if( newDataType != DT_INDEX_GROUP)
		{
			
			//check if we're turning a group into a question, because then we'll have to change the underlying object
			if(questionDef instanceof GroupDef)
			{

				//find out the family relations of the question
				IFormElement parent = questionDef.getParent();
				int index = parent.getIndexOfChild(questionDef);
				
				//remove it form it's parent
				formChangeListener.deleteSelectedItem();
				
				//make a new question def def
				QuestionDef qDef = (QuestionDef)(formChangeListener.addNewQuestion(QuestionDef.QTN_TYPE_TEXT));
							
				//set the details of the new group def
				qDef.setBinding(questionDef.getBinding());
				qDef.setText(questionDef.getText());
				qDef.setItextId(questionDef.getItextId());
				qDef.setHelpText(questionDef.getHelpText());
				
				//move it where it needs to go
				formChangeListener.moveFormObjects(qDef, parent, index, null);
				//hand over the new question to the original question variable
				questionDef = qDef;
			}
			
			switch (newDataType) {
			case DT_INDEX_NUMBER:
				dataType = QuestionDef.QTN_TYPE_NUMERIC;
				break;
			case DT_INDEX_DECIMAL:
				dataType = QuestionDef.QTN_TYPE_DECIMAL;
				break;
			case DT_INDEX_DATE:
				dataType = QuestionDef.QTN_TYPE_DATE;
				break;
			case DT_INDEX_TIME:
				dataType = QuestionDef.QTN_TYPE_TIME;
				break;
			case DT_INDEX_DATE_TIME:
				dataType = QuestionDef.QTN_TYPE_DATE_TIME;
				break;
			case DT_INDEX_BOOLEAN:
				dataType = QuestionDef.QTN_TYPE_BOOLEAN;
				break;
			case DT_INDEX_SINGLE_SELECT:
				dataType = QuestionDef.QTN_TYPE_LIST_EXCLUSIVE;
				break;
			case DT_INDEX_MULTIPLE_SELECT:
				dataType = QuestionDef.QTN_TYPE_LIST_MULTIPLE;
				break;
			case DT_INDEX_REPEAT:
				dataType = QuestionDef.QTN_TYPE_REPEAT;
				break;
			case DT_INDEX_IMAGE:
				dataType = QuestionDef.QTN_TYPE_IMAGE;
				break;
			case DT_INDEX_VIDEO:
				dataType = QuestionDef.QTN_TYPE_VIDEO;
				break;
			case DT_INDEX_AUDIO:
				dataType = QuestionDef.QTN_TYPE_AUDIO;
				break;
			case DT_INDEX_GPS:
				dataType = QuestionDef.QTN_TYPE_GPS;
				break;
			case DT_INDEX_BARCODE:
				dataType = QuestionDef.QTN_TYPE_BARCODE;
				break;
			case DT_INDEX_TRIGGER:
				dataType = QuestionDef.QTN_TYPE_TRIGGER;
				break;
			case DT_INDEX_GROUP:
				dataType = QuestionDef.QTN_TYPE_GROUP;
				break;
			}
	
			questionDef.setDataType(dataType);
		}
		else //with groups it's not just about changing the data type, but the whole object
		{
			
			//find out the family relations of the question
			IFormElement parent = questionDef.getParent();
			int index = parent.getIndexOfChild(questionDef);
			
			//remove it form it's parent
			formChangeListener.deleteSelectedItem();
			
			//make a new group def
			GroupDef groupDef = (GroupDef)(formChangeListener.addNewQuestion(QuestionDef.QTN_TYPE_GROUP));
						
			//set the details of the new group def
			groupDef.setBinding(questionDef.getBinding());
			groupDef.setText(questionDef.getText());
			groupDef.setItextId(questionDef.getItextId());
			groupDef.setHelpText(questionDef.getHelpText());

			
			//move it where it needs to go
			formChangeListener.moveFormObjects(groupDef, parent, index, null);
			
			questionDef = groupDef;
		}
		
		return questionDef;
	}//end method

	/**
	 * Sets the listener for form change events.
	 * 
	 * @param formChangeListener
	 *            the listener.
	 */
	public void setFormChangeListener(IFormChangeListener formChangeListener) {
		this.formChangeListener = formChangeListener;
	}

	/**
	 * Sets values for widgets which deal with form definition properties.
	 * 
	 * @param formDef
	 *            the form definition object.
	 */
	private void setFormProperties(FormDef formDef) {		
		enableQuestionOnlyProperties(false);
		
		//turned these off since we're using the preloads. The preloads aren't really "in" the system
		//like everything else, so if you change the xml binding or something the preloads will get lost
		//so you just can't change the XML binding. We'll do this right when we don't have a conference
		//7 days away.
		txtBinding.setVisible(true);
		qtnID.setVisible(false);
		qtnIDHint.setVisible(false);
		txtText.setVisible(true);
		
		lblBinding.setVisible(true);
		lblQtnID.setVisible(false);
		lblText.setVisible(true);
		
		lblBinding.setText(LocaleText.get("formBinding"));
		lblQtnID.setText(LocaleText.get("formId"));
		lblText.setText(LocaleText.get("formText"));
		
		txtBinding.setText(formDef.getVariableName());
		qtnID.setText(formDef.getItextId());
		txtText.setText(formDef.getName());
		table.setVisible(true);
		
		
		txtText.setFocus(true);
		txtText.selectAll();
	}

	/**
	 * Sets values for widgets which deal with page definition properties.
	 * 
	 * @param pageDef
	 *            the page definition object.
	 */
	private void setPageProperties(GroupDef pageDef) {
		enableQuestionOnlyProperties(false);
		
		cbDataType.setVisible(true); 
		qtnID.setVisible(true);
		qtnIDHint.setVisible(true);
		txtText.setVisible(true);
		cbDataTypeHint.setVisible(true); 
		qtnIDHint.setVisible(true);
		txtTextHint.setVisible(true);
		qtnID.setVisible(true);
		
		

		
		lblDataType.setVisible(true);
		lblQtnID.setVisible(true);
		lblText.setVisible(true);
		
		lblDataType.setText(LocaleText.get("groupType"));
		lblQtnID.setText(LocaleText.get("groupId"));
		lblText.setText(LocaleText.get("groupText"));

		// We do not just wanna show this but rather want to enable them.
		skipRulesView.setEnabled(true);
		validationRulesView.setEnabled(true);

		qtnID.setText(pageDef.getItextId());
		txtText.setText(pageDef.getText());
		setDataType(pageDef.getDataType());


		table.setVisible(true);
		txtText.setFocus(true);
		txtText.selectAll();
	}

	/**
	 * Sets values for widgets which deal with question definition properties.
	 * 
	 * @param questionDef
	 *            the question definition object.
	 */
	private void setQuestionProperties(QuestionDef questionDef) {
		enableQuestionOnlyProperties(true);
		
		qtnID.setVisible(true);
		qtnIDHint.setVisible(true);
						
		chkEnabledHint.setVisible(true);
		txtHelpTextHint.setVisible(true);
		chkRequiredHint.setVisible(true);
		chkVisibleHint.setVisible(true);
		qtnIDHint.setVisible(true);
		
		txtPreload.setVisible(true);
		txtPreloadParam.setVisible(true);
		txtPreloadHint.setVisible(true);
		txtPreloadParamHint.setVisible(true);
		lblPreload.setVisible(true);
		lblPreloadParam.setVisible(true);
		
		lblQtnID.setVisible(true);
		lblQtnID.setText(LocaleText.get("qtnId"));

		qtnID.setText(questionDef.getItextId());
		txtText.setText(questionDef.getText());
		txtBinding.setText(questionDef.getBinding());
		txtHelpText.setText(questionDef.getHelpText());
		txtDefaultValue.setText(questionDef.getDefaultValue());
		txtPreload.setText(questionDef.getPreload());
		txtPreloadParam.setText(questionDef.getPreloadParam());
		
		
		chkVisible.setValue(questionDef.isVisible());
		chkEnabled.setValue(questionDef.isEnabled());
		chkRequired.setValue(questionDef.isRequired());

		setDataType(questionDef.getDataType());
		
		//itemset
		if(questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE || questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE)
		{
			chkUseItemSet.setVisible(true);
			lblUseItemSet.setVisible(true);
			useItemSetHint.setVisible(true);
			
			if(questionDef.usesItemSet())
			{
				chkUseItemSet.setValue(true);
			}
			else
			{
				chkUseItemSet.setValue(false);
			}
		}

		// Skip logic processing is a bit slow and hence we wanna update the
		// UI with the rest of simple quick properties as we process skip logic
		//DeferredCommand.addCommand(new Command() { //Depreciated
		Scheduler scheduler = Scheduler.get();
		scheduler.scheduleDeferred(new Command() {
			public void execute() {
				skipRulesView.setQuestionDef((IFormElement) propertiesObj);

				if (propertiesObj instanceof QuestionDef)
					validationRulesView.setQuestionDef((QuestionDef) propertiesObj);
				else
					validationRulesView.setQuestionDef(null);
			}
		});
	}

	/**
	 * Sets values for widgets which deal with question option definition
	 * properties.
	 * 
	 * @param optionDef
	 *            the option definition object.
	 */
	private void setQuestionOptionProperties(OptionDef optionDef) {
		enableQuestionOnlyProperties(false);
		
		// We don't need to see the validation and skip logic tabs
		tabs.setVisible(false);
		
		txtDefaultValue.setVisible(true);
		lblDefault.setVisible(true);
		txtDefaultValueHint.setVisible(true);
		qtnIDHint.setVisible(true);
		
				
		qtnID.setVisible(true);
		qtnID.setText(optionDef.getItextId());
		txtText.setText(optionDef.getText());
		txtDefaultValue.setText(optionDef.getDefaultValue());
		
		
		lblQtnID.setVisible(true);
		lblText.setVisible(true);
		lblDefault.setVisible(true);
		
		
		lblQtnID.setText(LocaleText.get("optQtnId"));
		lblText.setText(LocaleText.get("optText"));
		lblDefault.setText(LocaleText.get("optDefaultValue"));
		
		skipRulesView.updateSkipRule();
		
		table.setVisible(true);
		txtBinding.setVisible(false);
	}
	

	/**
	 * Sets whether to enable question property widgets.
	 * 
	 * @param enabled
	 *            true to enable them, false to disable them.
	 */
	public void enableQuestionOnlyProperties(boolean enabled) {
		tabs.setVisible(false);
		table.setVisible(false);
		
		postProcessorApplet.setVisible(false);
		validatorApplet.setVisible(false);
		lblXMLFromFolder.setVisible(false);
		dataAttributesView.setVisible(false);
		itemSetView.setVisible(false);
		
		txtBinding.setVisible(enabled);
		txtBindingHint.setVisible(enabled);
		cbDataType.setVisible(enabled);
		cbDataTypeHint.setVisible(enabled);
		txtDefaultValue.setVisible(enabled);
		txtDefaultValueHint.setVisible(enabled);
		chkEnabled.setVisible(enabled); 
		chkEnabledHint.setVisible(enabled);
		txtHelpText.setVisible(enabled);
		txtHelpTextHint.setVisible(enabled);
		chkRequired.setVisible(enabled);  
		chkRequiredHint.setVisible(enabled);
		chkVisible.setVisible(enabled);
		chkVisibleHint.setVisible(enabled);
		txtPreload.setVisible(enabled);
		txtPreloadParam.setVisible(enabled);
		txtPreloadHint.setVisible(enabled);
		txtPreloadParamHint.setVisible(enabled);
		chkUseItemSet.setVisible(false);
		useItemSetHint.setVisible(false);

		lblDataType.setVisible(enabled);
		lblDefault.setVisible(enabled);
		lblEnabled.setVisible(enabled);
		lblHelpText.setVisible(enabled);
		lblRequired.setVisible(enabled); 
		lblVisible.setVisible(enabled);
		lblBinding.setVisible(enabled);
		lblPreload.setVisible(enabled);
		lblPreloadParam.setVisible(enabled);
		lblUseItemSet.setVisible(false);

		lblDataType.setText(enabled ? LocaleText.get("qtnType") : "");
		lblDefault.setText(enabled ? LocaleText.get("qtnDefaultValue") : "");
		lblEnabled.setText(enabled ? LocaleText.get("qtnEnabled") : "");
		lblRequired.setText(enabled ? LocaleText.get("qtnRequired") : ""); 
		lblVisible.setText(enabled ? LocaleText.get("qtnVisible") : "");
		lblText.setText(enabled ? LocaleText.get("qtnText") : "");
		lblQtnID.setText(enabled ? LocaleText.get("qtnId") : "");
		lblHelpText.setText(enabled ? LocaleText.get("qtnHelpText") : "");
		lblBinding.setText(enabled ? LocaleText.get("qtnBinding") : "");

		skipRulesView.setEnabled(enabled);
		validationRulesView.setEnabled(enabled);
		clearProperties();
		if(enabled) {
			table.setVisible(enabled);
		}
	}

	/**
	 * Selects the current question's data type in the data types drop down
	 * listbox.
	 * 
	 * @param type
	 *            the current question's data type.
	 */
	private void setDataType(int type) {
		int index = DT_INDEX_NONE;

		switch (type) {
		case QuestionDef.QTN_TYPE_DATE:
			index = DT_INDEX_DATE;
			break;
		case QuestionDef.QTN_TYPE_BOOLEAN:
			index = DT_INDEX_BOOLEAN;
			break;
		case QuestionDef.QTN_TYPE_DATE_TIME:
			index = DT_INDEX_DATE_TIME;
			break;
		case QuestionDef.QTN_TYPE_DECIMAL:
			index = DT_INDEX_DECIMAL;
			break;
		case QuestionDef.QTN_TYPE_LIST_EXCLUSIVE:
			index = DT_INDEX_SINGLE_SELECT;
			break;
		case QuestionDef.QTN_TYPE_LIST_MULTIPLE:
			index = DT_INDEX_MULTIPLE_SELECT;
			break;
		case QuestionDef.QTN_TYPE_NUMERIC:
			index = DT_INDEX_NUMBER;
			break;
		case QuestionDef.QTN_TYPE_REPEAT:
			index = DT_INDEX_REPEAT;
			break;
		case QuestionDef.QTN_TYPE_TEXT:
			index = DT_INDEX_TEXT;
			break;
		case QuestionDef.QTN_TYPE_TIME:
			index = DT_INDEX_TIME;
			break;
		case QuestionDef.QTN_TYPE_IMAGE:
			index = DT_INDEX_IMAGE;
			break;
		case QuestionDef.QTN_TYPE_VIDEO:
			index = DT_INDEX_VIDEO;
			break;
		case QuestionDef.QTN_TYPE_AUDIO:
			index = DT_INDEX_AUDIO;
			break;
		case QuestionDef.QTN_TYPE_GPS:
			index = DT_INDEX_GPS;
			break;
		case QuestionDef.QTN_TYPE_BARCODE:
			index = DT_INDEX_BARCODE;
			break;
		case QuestionDef.QTN_TYPE_TRIGGER:
			index = DT_INDEX_TRIGGER;
			break;
		case QuestionDef.QTN_TYPE_GROUP:
			index = DT_INDEX_GROUP;
			break;
		}

		cbDataType.setSelectedIndex(index);
	}
	
	/**
	 * Used to hide the properties. Use this when clearing the form tree.
	 */
	public void hideProperties()
	{
		enableQuestionOnlyProperties(false);
	}

	/**
	 * Clears values from all widgets.
	 */
	public void clearProperties() {
		cbDataType.setSelectedIndex(DT_INDEX_NONE);
		chkEnabled.setValue(false);
		chkRequired.setValue(false);
		chkVisible.setValue(false);
		qtnID.setText(null);
		txtBinding.setText(null);
		txtDefaultValue.setText(null);
		txtHelpText.setText(null);
		txtText.setText(null);
	}

	/**
	 * @see org.purc.purcforms.client.controller.IFormSelectionListener#onFormItemSelected(java.lang.Object)
	 */
	public void onFormItemSelected(Object formItem) {
		propertiesObj = formItem;
		hideAdvancedProperties();
		btnAdvancedProperties.setVisible(true);

		clearProperties();

		// For now these may be options for boolean question types (Yes & No)
		if (formItem == null) {
			enableQuestionOnlyProperties(false);
			txtText.setVisible(false);
			lblText.setVisible(false);
			qtnID.setVisible(false);
			lblQtnID.setVisible(false);
			txtBinding.setVisible(false);
			lblBinding.setVisible(false);
			return;
		}
		
		txtText.setVisible(true);
		lblText.setVisible(true);

		boolean visible = Context.allowBindEdit()
				&& !Context.isStructureReadOnly();
		txtBinding.setVisible(visible);
		lblBinding.setVisible(visible);

		if (formItem instanceof FormDef)
		{
			btnAdvancedProperties.setVisible(false);
			setFormProperties((FormDef) formItem);
		}
		else if (formItem instanceof GroupDef)
			setPageProperties((GroupDef) formItem);
		else if (formItem instanceof QuestionDef)
			setQuestionProperties((QuestionDef) formItem);
		else if(formItem instanceof ItemSetDef)
		{
			setItemSetProperties((ItemSetDef)formItem);
		}
		else if (formItem instanceof OptionDef) {
			setQuestionOptionProperties((OptionDef) formItem);
		

			// Since option bindings are not xml node names, we may allow their
			// edits as they are not structure breaking.
			visible = !Context.isStructureReadOnly();
			if(!(formItem instanceof OptionDef))
			{
				txtBinding.setVisible(visible);
				lblBinding.setVisible(visible);
			}
		}
		else if (formItem instanceof PostProcessProperties)
			setPostProcess((PostProcessProperties) formItem);
		else if (formItem instanceof DataInstanceDef)
		{
			setDataInstanceProperties((DataInstanceDef)formItem);
		}
		else if (formItem instanceof DataDef)
		{
			setDataElementProperties((DataDef)formItem);
		}
		
		txtText.setFocus(true);
		txtText.selectAll();

	}
	
	/**
	 * Set the data for item sets
	 * @param itemSet
	 */
	private void setItemSetProperties(ItemSetDef itemSet)
	{
		btnAdvancedProperties.setVisible(false);
		enableQuestionOnlyProperties(false);
		table.setVisible(true);
		itemSetView.setVisible(true);
		itemSetView.setFormChangeListener(formChangeListener);
		itemSetView.setItemSet(itemSet);
		
		
		this.txtTextHint.setVisible(false);
		this.txtText.setVisible(false);
		
		qtnID.setVisible(false);
		lblQtnID.setVisible(false);
		qtnIDHint.setVisible(false);

	}
	
	/**
	 * Switch to data instance
	 * @param dataInstance
	 */
	private void setDataInstanceProperties(DataInstanceDef dataInstance)
	{
		enableQuestionOnlyProperties(false);
		this.showAdvancedProperties();
		qtnID.setVisible(true);
		qtnID.setText(dataInstance.getInstanceId());
		lblQtnID.setVisible(true);
		lblQtnID.setText(LocaleText.get("dataInstanceId"));
		qtnIDHint.setVisible(true);
		
		//turn off the ID fields
		/*
		qtnID.setVisible(false);
		lblQtnID.setVisible(false);
		qtnIDHint.setVisible(false);*/
	}
	
	/**
	 * Switch to data instance
	 * @param dataInstance
	 */
	private void setDataElementProperties(DataDef dataDef)
	{
		enableQuestionOnlyProperties(false);		
		table.setVisible(true);
		
		txtText.setText(dataDef.getName());
		txtText.setVisible(true);
		lblText.setVisible(true);
		lblText.setText(LocaleText.get("dataElementText"));
		txtTextHint.setVisible(true);
		
		//turn on the attributes view
		dataAttributesView.setVisible(true);
		dataAttributesView.setFormChangeListener(formChangeListener);
		dataAttributesView.setDataDef(dataDef);
		
		//turn off the ID fields
		qtnID.setVisible(false);
		lblQtnID.setVisible(false);
		qtnIDHint.setVisible(false);
		
		txtText.setFocus(true);
		txtText.selectAll();
	}

	/**
	 * This will take the postProcessProperties and create the UI
	 * for post processing!!!
	 * @param postProcessProperties
	 */
	private void setPostProcess(PostProcessProperties postProcessProperties) {
		enableQuestionOnlyProperties(false);
		txtText.setVisible(false);
		lblText.setVisible(false);
		qtnID.setVisible(false);
		lblQtnID.setVisible(false);
		qtnIDHint.setVisible(false);
		txtTextHint.setVisible(false);
		
		
		postProcessorApplet.setVisible(true);
		lblXMLFromFolder.setVisible(true);
		//txtXMLFromFolderHint.setVisible(true);
		
		
		table.setVisible(true);		
	}

	/**
	 * Sets focus to the first input widget.
	 */
	public void setFocus() {
		txtText.setFocus(true);
		txtText.selectAll();
	}

	/**
	 * @see com.google.gwt.user.client.WindowResizeListener#onWindowResized(int,
	 *      int)
	 */
	public void onWindowResized(int width, int height) {
		setWidth("100%");
		setHeight("100%");
		validationRulesView.onWindowResized(width, height);
	}

	/**
	 * Retrieves changes from all widgets and updates the selected object.
	 */
	public void commitChanges() {
		skipRulesView.updateSkipRule();
		validationRulesView.updateValidationRule();
	}

	/**
	 * @see org.purc.purcforms.client.controller.ItemSelectionListener#onItemSelected(Object,
	 *      Object)
	 */
	public void onItemSelected(Object sender, Object item) {
		/*
		 * if(sender == btnDescTemplate){
		 * 
		 * item = "${" + item + "}$";
		 * 
		 * if(propertiesObj instanceof QuestionDef){
		 * txtText.setText(txtText.getText() + " " + txtDescTemplate.getText() +
		 * item); updateText(); txtText.setFocus(true); } else{
		 * txtDescTemplate.setText(txtDescTemplate.getText() + item);
		 * updateDescTemplate(); //Added for IE which does not properly throw
		 * change events for the desc template textbox
		 * txtDescTemplate.setFocus(true); } } else if(sender ==
		 * btnCalculation){ assert(propertiesObj instanceof QuestionDef);
		 * txtCalculation.setText(txtCalculation.getText() + item);
		 * updateCalculation(); //Added for IE which does not properly throw
		 * change events for the desc template textbox
		 * txtCalculation.setFocus(true); }
		 */
	}

	/**
	 * @see org.purc.purcforms.client.controller.ItemSelectionListener#onStartItemSelection(Object)
	 */
	public void onStartItemSelection(Object sender) {}

	/**
	 * Sets the listener to form action events.
	 * 
	 * @param formActionListener
	 *            the listener.
	 */
	public void setFormActionListener(IFormActionListener formActionListener) {
		this.formActionListener = formActionListener;
	}
	
	
	/**
	 * Pass in some XML and this will display the results of validation to the user
	 * @param xmlStr
	 */
	public void validate(String xmlStr)
	{
		enableQuestionOnlyProperties(false);
		txtText.setVisible(false);
		lblText.setVisible(false);
		qtnID.setVisible(false);
		lblQtnID.setVisible(false);
		qtnIDHint.setVisible(false);
		txtTextHint.setVisible(false);
		
		//send the XML string to javascript so the plugin can get it
		SaveToFileDialog.formXmlToSave(xmlStr);
		
		validatorApplet.setVisible(true);
		
		table.setVisible(true);		
	}
	
	/**
	 * Hides the applets when we want our screen back
	 */
	public void hideApplets()	
	{
		validatorApplet.setVisible(false);
		postProcessorApplet.setVisible(false);
	}

	@Override
	public void onBrowserEvent(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONKEYDOWN:
			if (!isVisible())
				return;

			/* Etherton: not sure if this even works any more
			int keyCode = event.getKeyCode();
			if (event.getCtrlKey()) {
				if (keyCode == 'N' || keyCode == 'n') {
					formActionListener.addNewItem();
					DOM.eventPreventDefault(event);
				} else if (keyCode == KeyCodes.KEY_RIGHT) {
					formActionListener.moveToChild();
					DOM.eventPreventDefault(event);
				} else if (keyCode == KeyCodes.KEY_LEFT) {
					formActionListener.moveToParent();
					DOM.eventPreventDefault(event);
				} else if (keyCode == KeyCodes.KEY_UP) {
					formActionListener.moveUp();
					DOM.eventPreventDefault(event);
				} else if (keyCode == KeyCodes.KEY_DOWN) {
					formActionListener.moveDown();
					DOM.eventPreventDefault(event);
				}
			}
			*/
		}
	}
	
	/**
	 * Used to turn on advanced properties
	 */
	public void showAdvancedProperties()
	{
		advancedTable.setVisible(true);
		if(propertiesObj instanceof QuestionDef || propertiesObj instanceof GroupDef)
		{
			tabs.setVisible(true);
		}
		btnAdvancedProperties.setText(LocaleText.get("hideAdvancedProperties"));
	}
	
	/**
	 * Used to turn off the advanced properties
	 */
	public void hideAdvancedProperties()
	{
		advancedTable.setVisible(false);
		if(propertiesObj instanceof QuestionDef || propertiesObj instanceof GroupDef)
		{
			tabs.setVisible(false);
		}
		btnAdvancedProperties.setText(LocaleText.get("showAdvancedProperties"));
	}
	
	private void setUseItemSet()
	{
		ItemSetDef isd = null;
		if(!Window.confirm(LocaleText.get("switchQuestionDataSourceWarning")))
		{
			chkUseItemSet.setValue(!chkUseItemSet.getValue());
			return;
		}
		QuestionDef questionDef = (QuestionDef)(propertiesObj);
		if(chkUseItemSet.getValue())
		{ //using an item set
			//drop those choices
			//and add an itemset instead
			questionDef.getChildren().clear();
			isd = new ItemSetDef(questionDef);
			questionDef.addChild(isd);			
		}
		else //not using an itemset
		{
			questionDef.getChildren().clear();
		}
		
		propertiesObj = formChangeListener.onFormItemChanged(propertiesObj);
		if(isd != null)
		{
			formChangeListener.setItemAsSelected(isd);
		}
	}
}
