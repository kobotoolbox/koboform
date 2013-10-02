package org.openrosa.client.view;

import java.util.List;

import org.openrosa.client.Context;
import org.openrosa.client.OpenRosaConstants;
import org.openrosa.client.controller.FormDesignerController;
import org.openrosa.client.controller.IDataTypeChangeListener;
import org.openrosa.client.controller.IFileListener;
import org.openrosa.client.controller.IFormDesignerListener;
import org.openrosa.client.model.DataDefBase;
import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.GroupDef;
import org.openrosa.client.model.IFormElement;
import org.openrosa.client.model.ItemSetDef;
import org.openrosa.client.model.OptionDef;
import org.openrosa.client.model.QuestionDef;
import org.openrosa.client.postprocess.PostProcessProperties;
import org.openrosa.client.util.FormHandler;
import org.purc.purcforms.client.controller.IFormSelectionListener;
import org.purc.purcforms.client.controller.ILocaleListChangeListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.Locale;

import com.extjs.gxt.ui.client.Style.ButtonArrowAlign;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonGroup;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

/**
 * This widget is the tool bar for the form designer.
 * 
 * @author adewinter
 * @author ghendrick
 * 
 *         Change Log
 * 
 *         Toolbar.Images now use Source annotation and moved files to resources
 *         org/openrosa/client/resources
 * 
 *         Main Menu no longer has saveFileBut to Save to File, not supportable
 *         through offline application
 * 
 *         Main Menu no longer has submitBut to submit to server button as
 *         offline application cannot use this
 */
@SuppressWarnings("unused")
public class Toolbar extends Composite implements ILocaleListChangeListener,
		IDataTypeChangeListener{

	/**
	 * Tool bar images.
	 */
	public interface Images extends ClientBundle {
		
		
		@Source("org/openrosa/client/resources/deleteFromBrowser.png")
		ImageResource deleteFromBrowser();
		
		@Source("org/openrosa/client/resources/tasksgroup.gif")
		ImageResource validate();
		
		@Source("org/openrosa/client/resources/merge.png")
		ImageResource merge();
		
		@Source("org/openrosa/client/resources/newform.gif")
		ImageResource newform();

		@Source("org/openrosa/client/resources/open.png")
		ImageResource open();

		@Source("org/openrosa/client/resources/save.png")		
		ImageResource save();
		
		@Source("org/openrosa/client/resources/179-notepad.png")
		ImageResource viewXML();
		
		@Source("org/openrosa/client/resources/40-inbox.png")
		ImageResource saveToBrowser();
		
		@Source("org/openrosa/client/resources/40-outbox.png")
		ImageResource loadFromBrowser();

		@Source("org/openrosa/client/resources/moveup.gif")
		ImageResource moveup();

		@Source("org/openrosa/client/resources/movedown.gif")
		ImageResource movedown();

		@Source("org/openrosa/client/resources/add.png")
		ImageResource add();

		@Source("org/openrosa/client/resources/addChild.png")
		ImageResource addchild();

		@Source("org/openrosa/client/resources/delete.gif")
		ImageResource delete();

		@Source("org/openrosa/client/resources/justifyleft.gif")
		ImageResource justifyleft();

		@Source("org/openrosa/client/resources/justifyright.gif")
		ImageResource justifyright();

		@Source("org/openrosa/client/resources/cut.png")
		ImageResource cut();
		
		@Source("org/openrosa/client/resources/postprocess.png")		
		ImageResource postprocess();

		@Source("org/openrosa/client/resources/copy.png")
		ImageResource copy();

		@Source("org/openrosa/client/resources/paste.png")
		ImageResource paste();

		@Source("org/openrosa/client/resources/alignTop.gif")
		ImageResource alignTop();

		@Source("org/openrosa/client/resources/alignBottom.gif")
		ImageResource alignBottom();

		@Source("org/openrosa/client/resources/samewidth.gif")
		ImageResource samewidth();

		@Source("org/openrosa/client/resources/sameheight.gif")
		ImageResource sameheight();

		@Source("org/openrosa/client/resources/samesize.gif")
		ImageResource samesize();

		@Source("org/openrosa/client/resources/undo.gif")
		ImageResource undo();

		@Source("org/openrosa/client/resources/redo.gif")
		ImageResource redo();

		@Source("org/openrosa/client/resources/refresh.gif")
		ImageResource refresh();

		@Source("org/openrosa/client/resources/emptyIcon.png")
		ImageResource emptyIcon();

		@Source("org/openrosa/client/resources/addChild.png")
		ImageResource addChild();

		@Source("org/openrosa/client/resources/addDate.png")
		ImageResource addDate();

		@Source("org/openrosa/client/resources/addNumeric.png")
		ImageResource addNumeric();

		@Source("org/openrosa/client/resources/addDecimal.png")
		ImageResource addDecimal();

		@Source("org/openrosa/client/resources/addMultSelect.png")
		ImageResource addMultSelect();

		@Source("org/openrosa/client/resources/addSingSelect.png")
		ImageResource addSingSelect();

		@Source("org/openrosa/client/resources/addText.png")
		ImageResource addText();

		@Source("org/openrosa/client/resources/load.png")
		ImageResource load();

		@Source("org/openrosa/client/resources/menu.png")
		ImageResource menu();

		@Source("org/openrosa/client/resources/localization.png")
		ImageResource localization();

		@Source("org/openrosa/client/resources/showxml.png")
		ImageResource showxml();

		@Source("org/openrosa/client/resources/newformmenu.png")
		ImageResource newformmenu();

		@Source("org/openrosa/client/resources/addTime.png")
		ImageResource addTime();

		@Source("org/openrosa/client/resources/addDateTime.png")
		ImageResource addDateTime();

		@Source("org/openrosa/client/resources/addRepeat.png")
		ImageResource addRepeat();

		@Source("org/openrosa/client/resources/addLabel.png")
		ImageResource addLabel();

		@Source("org/openrosa/client/resources/addBarcode.png")
		ImageResource addBarcode();

		@Source("org/openrosa/client/resources/addGroup.png")
		ImageResource addGroup();

		@Source("org/openrosa/client/resources/addGPS.png")
		ImageResource addGPS();

		@Source("org/openrosa/client/resources/addAudio.png")
		ImageResource addAudio();

		@Source("org/openrosa/client/resources/addVideo.png")
		ImageResource addVideo();

		@Source("org/openrosa/client/resources/addPicture.png")
		ImageResource addPicture();

		@Source("org/openrosa/client/resources/blankbutton.png")
		ImageResource blankbutton();
		
		@Source("org/openrosa/client/resources/help.png")
		ImageResource helpPicture();
		
		@Source("org/openrosa/client/resources/data_element.png")
		ImageResource dataElementPicture();
		
		@Source("org/openrosa/client/resources/data_instance.png")
		ImageResource dataInstancePicture();
		
		@Source("org/openrosa/client/resources/addFromText.png")
		ImageResource addFromTextPicture();
		
		@Source("org/openrosa/client/resources/AddBlock.png")
		ImageResource addBlockPicture();
		
		@Source("org/openrosa/client/resources/AddBlockLocal.png")
		ImageResource addBlockLocalPicture();
		
		@Source("org/openrosa/client/resources/AddBlockRemote.png")
		ImageResource addBlockRemotePicture();
	}
	
	private static Toolbar instance; 

	/** Main widget for this tool bar. */
	private ToolBar toolBar;

	/** The tool bar buttons. */
	private Menu menu;
	private SplitButton menuBut;
	private Button saveToBrowserBut;
	private Button saveAsTemplateBut;
	private Button loadFromBrowserBut;
	private Button loadTemplateBut;
	private Button deleteromBrowserBut;
	private Button saveBut;
	private Button spssBut;
	private Button saveFileBut;
	private Button openBut;
	private Button openFileBut;
	// private Button submitBut;
	private Button locBut;
	private Button addSelect;
	private Button txtBut;
	private Button intBut;
	private Button decBut;
	private Button dateBut;
	private Button multBut;
	private Button singBut, timeBut, datetimeBut, picBut, vidBut, audBut,
			gpsBut, bcdBut, grpBut, triggerBut, rptBut;
	private Button newBut;
	private SplitButton splitItem, dataSplitItem, blockAddSplitItem;
	private Button bdelete, bcut, bcopy, bpaste;
	private Button addDataInstanceBut, addDataElementBut, importDataCsvBut;
	private Button editLocale;
	
	private Button addFromTextBut, addFromLocalBut, addFromRemoteBut, copySelectedToLibraryBut;
	
	private Button bPostProcess; //button for post processing
	
	private Button bNewForm; //button for making a new form
	
	private Button bHelp; //shows the help popup or something
	
	
	private Button bValidate; //button that launches the validator for the XML

	private IFileListener fileListener;

	private DesignTabWidget dtabWidget;

	/** Widget to display the list of languages or locales. */
	private ComboBox<BaseModel> cb;

	private CheckBox chkDefault;

	/** The images for the tool bar icons. */
	public final Images images;

	/** Listener to the tool bar button click events. */
	private FormDesignerController controller;

	// This should be localized in the same way everything else is, eventually.
	String[] buttonLabels = { LocaleText.get("addQstToolbarBut"), //0
			LocaleText.get("textQstToolbarBut"), //1
			LocaleText.get("intQstToolbarBut"), //2
			LocaleText.get("decQstToolbarBut"), //3
			LocaleText.get("dateQstToolbarBut"), //4
			LocaleText.get("multiQstToolbarBut"), //5
			LocaleText.get("singleQstToolbarBut"), //6
			LocaleText.get("menuToolbarBut"), //7
			LocaleText.get("viewFormTextToolbarBut"), //8
			LocaleText.get("saveToFileToolbarBut"), //9
			LocaleText.get("loadFromTextToolbarBut"), //10
			LocaleText.get("localizationToolbarBut"),
			LocaleText.get("openFromFileToolbarBut"),
			LocaleText.get("newXformToolbarBut"),
			LocaleText.get("timeQstToolbarBut"),
			LocaleText.get("dateTimeQstToolbarBut"),
			LocaleText.get("picQstToolbarBut"),
			LocaleText.get("videoQstToolbarBut"),
			LocaleText.get("audioQstToolbarBut"),
			LocaleText.get("gpsQstToolbarBut"),
			LocaleText.get("groupQstToolbarBut"),
			LocaleText.get("barQstToolbarBut"),
			LocaleText.get("triggerQstToolbarBut"),
			LocaleText.get("repeatQstToolbarBut")};

	ListBox cbLocales = new ListBox(false);

	ButtonGroup localeGroup;

	/**
	 * Creates a new instance of the tool bar.
	 * 
	 * @param images
	 *            the images for tool bar icons.
	 * @param controller
	 *            listener to the tool bar button click events.
	 */
	
	public static Toolbar getToolbar(Images images, FormDesignerController controller,
			IFileListener fileListener, DesignTabWidget dtab) {
		
		instance = new Toolbar( images, controller, fileListener, dtab);
		return instance;
	}
	
	public static Toolbar getInstance()
	{
		return instance;
	}
	
	private Toolbar(Images images, FormDesignerController controller,
			IFileListener fileListener, DesignTabWidget dtab) {
		this.images = images;
		this.dtabWidget = dtab;
		this.controller = controller;
		setupToolbar(fileListener);
		setupClickListeners();

		Context.getEventBus().addDataTypeChangeListener(this);
		// initWidget(toolBar);
	}

	/**
	 * Sets up the tool bar.
	 */
	private void setupToolbar(IFileListener fileListener) {
		
		//setup login stuff
		Label l = new Label();
		l.setText("User info");
		l.setVisible(true);
		this.initWidget(l);
		
		
		
		toolBar = new ToolBar();
		
		this.fileListener = fileListener;
		fileListener.setToolbar(this);
		
		// ////////////////////FIRST GROUP/////////////////////////////////
		ButtonGroup group = new ButtonGroup(1);
		group.setHeading("Main Menu");

		menuBut = new SplitButton(buttonLabels[7]);
		menuBut.setIcon(AbstractImagePrototype.create(images.menu()));
		menuBut.setScale(ButtonScale.LARGE);
		menuBut.setIconAlign(IconAlign.TOP);
		menuBut.setArrowAlign(ButtonArrowAlign.RIGHT);

		menu = new Menu();
		menu.addStyleName("myMenu");

		newBut = new Button(buttonLabels[13]);
		newBut.setIcon(AbstractImagePrototype.create(images.newformmenu()));
		newBut.setScale(ButtonScale.LARGE);
		newBut.setIconAlign(IconAlign.LEFT);
		newBut.addStyleName("myMenuButton");

		saveBut = new Button(buttonLabels[8]);
		saveBut.setIcon(AbstractImagePrototype.create(images.viewXML()));
		saveBut.setScale(ButtonScale.LARGE);
		saveBut.setIconAlign(IconAlign.LEFT);
		saveBut.addStyleName("myMenuButton");
		
		spssBut = new Button(LocaleText.get("createSpssFile"));
		spssBut.setIcon(AbstractImagePrototype.create(images.viewXML()));
		spssBut.setScale(ButtonScale.LARGE);
		spssBut.setIconAlign(IconAlign.LEFT);
		spssBut.addStyleName("myMenuButton");

		saveFileBut = new Button(buttonLabels[9]);
		saveFileBut.setIcon(AbstractImagePrototype.create(images.save()));
		saveFileBut.setScale(ButtonScale.LARGE);
		saveFileBut.setIconAlign(IconAlign.LEFT);
		saveFileBut.addStyleName("myMenuButton");
		
		saveToBrowserBut = new Button(LocaleText.get("saveToBroswerBut"));
		saveToBrowserBut.setIcon(AbstractImagePrototype.create(images.saveToBrowser()));
		saveToBrowserBut.setScale(ButtonScale.LARGE);
		saveToBrowserBut.setIconAlign(IconAlign.LEFT);
		saveToBrowserBut.addStyleName("myMenuButton");

		
		saveAsTemplateBut  = new Button(LocaleText.get("saveAsTemplateBut"));
		saveAsTemplateBut.setIcon(AbstractImagePrototype.create(images.saveToBrowser()));
		saveAsTemplateBut.setScale(ButtonScale.LARGE);
		saveAsTemplateBut.setIconAlign(IconAlign.LEFT);
		saveAsTemplateBut.addStyleName("myMenuButton");
		
		loadFromBrowserBut = new Button(LocaleText.get("loadFromBroswerBut"));
		loadFromBrowserBut.setIcon(AbstractImagePrototype.create(images.loadFromBrowser()));
		loadFromBrowserBut.setScale(ButtonScale.LARGE);
		loadFromBrowserBut.setIconAlign(IconAlign.LEFT);
		loadFromBrowserBut.addStyleName("myMenuButton");
		
		loadTemplateBut = new Button(LocaleText.get("loadTemplateBut"));
		loadTemplateBut.setIcon(AbstractImagePrototype.create(images.loadFromBrowser()));
		loadTemplateBut.setScale(ButtonScale.LARGE);
		loadTemplateBut.setIconAlign(IconAlign.LEFT);
		loadTemplateBut.addStyleName("myMenuButton");
		
		deleteromBrowserBut = new Button(LocaleText.get("deleteFromBroswerBut"));
		deleteromBrowserBut.setIcon(AbstractImagePrototype.create(images.deleteFromBrowser()));
		deleteromBrowserBut.setScale(ButtonScale.LARGE);
		deleteromBrowserBut.setIconAlign(IconAlign.LEFT);
		deleteromBrowserBut.addStyleName("myMenuButton");
				
		openBut = new Button(buttonLabels[10]);
		openBut.setIcon(AbstractImagePrototype.create(images.showxml()));
		openBut.setScale(ButtonScale.LARGE);
		openBut.setIconAlign(IconAlign.LEFT);
		openBut.addStyleName("myMenuButton");

		openFileBut = new Button(buttonLabels[12]);
		openFileBut.setIcon(AbstractImagePrototype.create(images.load()));
		openFileBut.setScale(ButtonScale.LARGE);
		openFileBut.setIconAlign(IconAlign.LEFT);
		openFileBut.addStyleName("myMenuButton");

		
		bNewForm = new Button(LocaleText.get("newForm"));
		bNewForm.setIcon(AbstractImagePrototype.create(images.newformmenu()));
		bNewForm.setScale(ButtonScale.LARGE);
		bNewForm.setIconAlign(IconAlign.LEFT);
		bNewForm.addStyleName("myMenuButton");
		
		bValidate = new Button(LocaleText.get("validate"));
		bValidate.setIcon(AbstractImagePrototype.create(images.validate()));
		bValidate.setScale(ButtonScale.LARGE);
		bValidate.setIconAlign(IconAlign.LEFT);
		bValidate.addStyleName("myMenuButton");

		/*
		 * submitBut = new Button("Submit to server");
		 * submitBut.setIcon(AbstractImagePrototype
		 * .create(images.localization()));
		 * submitBut.setScale(ButtonScale.LARGE);
		 * submitBut.setIconAlign(IconAlign.LEFT);
		 * submitBut.addStyleName("myMenuButton");
		 */
		locBut = new Button(buttonLabels[11]);
		locBut.setIcon(AbstractImagePrototype.create(images.localization()));
		locBut.setScale(ButtonScale.LARGE);
		locBut.setIconAlign(IconAlign.LEFT);
		locBut.addStyleName("myMenuButton");
		locBut.disable(); // feature not ready yet.

		// menu.add(newBut);
		menu.add(bNewForm);
		menu.add(openBut);
		menu.add(openFileBut);		
		if(OpenRosaConstants.ENABLE_LIBRARY)
		{
			menu.add(loadTemplateBut);
		}
		menu.add(loadFromBrowserBut);
		if(OpenRosaConstants.ENABLE_LIBRARY)
		{
			menu.add(saveAsTemplateBut);
		}
		menu.add(saveBut);
		menu.add(saveFileBut);
		menu.add(saveToBrowserBut);
		menu.add(deleteromBrowserBut);
		//menu.add(bValidate);
		// menu.add(submitBut);
		// menu.add(locBut);
		menu.add(spssBut);

		menuBut.setMenu(menu);
		group.addButton(menuBut);
		toolBar.add(group);
		// /////////////////////////////////////////////////////////////////

		// ////////////////////SECOND GROUP/////////////////////////////////
		group = new ButtonGroup(2);
		group.setHeading("Add Questions");
		splitItem = new SplitButton(buttonLabels[0]);
		splitItem.setIcon(AbstractImagePrototype.create(images.add()));
		splitItem.setScale(ButtonScale.LARGE);
		splitItem.setIconAlign(IconAlign.TOP);
		splitItem.setArrowAlign(ButtonArrowAlign.RIGHT);
		
		
		
		
		menu = new Menu();
		menu.addStyleName("myMenu");
		txtBut = new Button(buttonLabels[1]);
		txtBut.setIcon(AbstractImagePrototype.create(images.addText()));
		txtBut.setScale(ButtonScale.LARGE);
		txtBut.setIconAlign(IconAlign.LEFT);
		txtBut.addStyleName("myMenuButton");

		intBut = new Button(buttonLabels[2]);
		intBut.setIcon(AbstractImagePrototype.create(images.addNumeric()));
		intBut.setScale(ButtonScale.LARGE);
		intBut.setIconAlign(IconAlign.LEFT);
		intBut.addStyleName("myMenuButton");
		decBut = new Button(buttonLabels[3]);
		decBut.setIcon(AbstractImagePrototype.create(images.addDecimal()));
		decBut.setScale(ButtonScale.LARGE);
		decBut.setIconAlign(IconAlign.LEFT);
		decBut.addStyleName("myMenuButton");
		dateBut = new Button(buttonLabels[4]);
		dateBut.setIcon(AbstractImagePrototype.create(images.addDate()));
		dateBut.setScale(ButtonScale.LARGE);
		dateBut.setIconAlign(IconAlign.LEFT);
		dateBut.addStyleName("myMenuButton");
		multBut = new Button(buttonLabels[5]);
		multBut.setIcon(AbstractImagePrototype.create(images.addMultSelect()));
		multBut.setScale(ButtonScale.LARGE);
		multBut.setIconAlign(IconAlign.LEFT);
		multBut.addStyleName("myMenuButton");
		singBut = new Button(buttonLabels[6]);
		singBut.setIcon(AbstractImagePrototype.create(images.addSingSelect()));
		singBut.setScale(ButtonScale.LARGE);
		singBut.setIconAlign(IconAlign.LEFT);
		singBut.addStyleName("myMenuButton");
		timeBut = new Button(buttonLabels[14]);
		timeBut.setIcon(AbstractImagePrototype.create(images.addTime()));
		timeBut.setScale(ButtonScale.LARGE);
		timeBut.setIconAlign(IconAlign.LEFT);
		timeBut.addStyleName("myMenuButton");
		datetimeBut = new Button(buttonLabels[15]);
		datetimeBut
				.setIcon(AbstractImagePrototype.create(images.addDateTime()));
		datetimeBut.setScale(ButtonScale.LARGE);
		datetimeBut.setIconAlign(IconAlign.LEFT);
		datetimeBut.addStyleName("myMenuButton");
		picBut = new Button(buttonLabels[16]);
		picBut.setIcon(AbstractImagePrototype.create(images.addPicture()));
		picBut.setScale(ButtonScale.LARGE);
		picBut.setIconAlign(IconAlign.LEFT);
		picBut.addStyleName("myMenuButton");
		vidBut = new Button(buttonLabels[17]);
		vidBut.setIcon(AbstractImagePrototype.create(images.addVideo()));
		vidBut.setScale(ButtonScale.LARGE);
		vidBut.setIconAlign(IconAlign.LEFT);
		vidBut.addStyleName("myMenuButton");
		audBut = new Button(buttonLabels[18]);
		audBut.setIcon(AbstractImagePrototype.create(images.addAudio()));
		audBut.setScale(ButtonScale.LARGE);
		audBut.setIconAlign(IconAlign.LEFT);
		audBut.addStyleName("myMenuButton");
		gpsBut = new Button(buttonLabels[19]);
		gpsBut.setIcon(AbstractImagePrototype.create(images.addGPS()));
		gpsBut.setScale(ButtonScale.LARGE);
		gpsBut.setIconAlign(IconAlign.LEFT);
		gpsBut.addStyleName("myMenuButton");
		grpBut = new Button(buttonLabels[20]);
		grpBut.setIcon(AbstractImagePrototype.create(images.addGroup()));
		grpBut.setScale(ButtonScale.LARGE);
		grpBut.setIconAlign(IconAlign.LEFT);
		grpBut.addStyleName("myMenuButton");
		bcdBut = new Button(buttonLabels[21]);
		bcdBut.setIcon(AbstractImagePrototype.create(images.addBarcode()));
		bcdBut.setScale(ButtonScale.LARGE);
		bcdBut.setIconAlign(IconAlign.LEFT);
		bcdBut.addStyleName("myMenuButton");
		triggerBut = new Button(buttonLabels[22]);
		triggerBut.setIcon(AbstractImagePrototype.create(images.addLabel()));
		triggerBut.setScale(ButtonScale.LARGE);
		triggerBut.setIconAlign(IconAlign.LEFT);
		triggerBut.addStyleName("myMenuButton");
		rptBut = new Button(buttonLabels[23]);
		rptBut.setIcon(AbstractImagePrototype.create(images.addRepeat()));
		rptBut.setScale(ButtonScale.LARGE);
		rptBut.setIconAlign(IconAlign.LEFT);
		rptBut.addStyleName("myMenuButton");

		


		menu.add(txtBut);
		menu.add(intBut);
		menu.add(decBut);
		menu.add(dateBut);
		menu.add(multBut);
		menu.add(singBut);
		menu.add(timeBut);
		menu.add(datetimeBut);
		menu.add(picBut);
		menu.add(vidBut);
		menu.add(audBut);
		menu.add(gpsBut);
		menu.add(bcdBut);
		menu.add(triggerBut);
		menu.add(rptBut);
		menu.add(grpBut);

		splitItem.setMenu(menu);
		splitItem.setEnabled(false);
		group.addButton(splitItem);

		addSelect = new Button("Add Select Option");
		addSelect.setIcon(AbstractImagePrototype.create(images.addchild()));
		addSelect.setIconAlign(IconAlign.TOP);
		addSelect.setScale(ButtonScale.LARGE);
		addSelect.disable();

		group.addButton(addSelect);

		bdelete = new Button("Delete", AbstractImagePrototype.create(images
				.delete()));
		bdelete.setScale(ButtonScale.LARGE);
		bdelete.setIconAlign(IconAlign.TOP);
		bdelete.disable();
		group.addButton(bdelete);
		///////////////////////////////////////////////////////////////////
		//setup the menu items for working with blocks.
		////////////////////////////////////////////////////////////////
		blockAddSplitItem = new SplitButton(LocaleText.get("blocks"));
		blockAddSplitItem.setIcon(AbstractImagePrototype.create(images.addBlockPicture()));
		blockAddSplitItem.setScale(ButtonScale.LARGE);
		blockAddSplitItem.setIconAlign(IconAlign.TOP);
		blockAddSplitItem.setArrowAlign(ButtonArrowAlign.RIGHT);
		blockAddSplitItem.disable();
		Menu blockMenu = new Menu();
		blockMenu.addStyleName("myMenu");
		
		addFromTextBut = new Button(LocaleText.get("addQuestionsFromText"));
		addFromTextBut.setIcon(AbstractImagePrototype.create(images.addFromTextPicture()));
		addFromTextBut.setScale(ButtonScale.LARGE);
		addFromTextBut.setIconAlign(IconAlign.LEFT);
		addFromTextBut.addStyleName("myMenuButton");
		addFromTextBut.setWidth(175);
		
		addFromLocalBut = new Button(LocaleText.get("addQuestionsFromLocal"));
		addFromLocalBut.setIcon(AbstractImagePrototype.create(images.addBlockLocalPicture()));
		addFromLocalBut.setScale(ButtonScale.LARGE);
		addFromLocalBut.setIconAlign(IconAlign.LEFT);
		addFromLocalBut.addStyleName("myMenuButton");
		addFromLocalBut.setWidth(175);
		
		copySelectedToLibraryBut = new Button(LocaleText.get("copyToLibrary"), AbstractImagePrototype.create(images.copy()));
		copySelectedToLibraryBut.setScale(ButtonScale.LARGE);
		copySelectedToLibraryBut.setIconAlign(IconAlign.LEFT);
		copySelectedToLibraryBut.setWidth(175);

		
		addFromRemoteBut = new Button(LocaleText.get("addQuestionsFromRemote"));
		addFromRemoteBut.setIcon(AbstractImagePrototype.create(images.addBlockRemotePicture()));
		addFromRemoteBut.setScale(ButtonScale.LARGE);
		addFromRemoteBut.setIconAlign(IconAlign.LEFT);
		addFromRemoteBut.addStyleName("myMenuButton");
		addFromRemoteBut.disable();
		addFromRemoteBut.setWidth(175);

		blockMenu.add(addFromTextBut);
		//blockMenu.add(addFromLocalBut);		
		blockMenu.add(copySelectedToLibraryBut);
		//blockMenu.add(addFromRemoteBut);
		
		blockAddSplitItem.setMenu(blockMenu);
		if(OpenRosaConstants.ENABLE_LIBRARY)
		{
			group.addButton(blockAddSplitItem);
		}
		group.setHeight(85);
		toolBar.add(group);
		// /////////////////////////////////////////////////////////////////
		
		// ////////////////////SECOND and a HAFL GROUP/////////////////////////////////
		group = new ButtonGroup(1);
		group.setHeading(LocaleText.get("addData"));
		
		dataSplitItem = new SplitButton(LocaleText.get("addDataInstance"));
		dataSplitItem.setIcon(AbstractImagePrototype.create(images.dataInstancePicture()));
		dataSplitItem.setScale(ButtonScale.LARGE);
		dataSplitItem.setIconAlign(IconAlign.TOP);
		dataSplitItem.setArrowAlign(ButtonArrowAlign.RIGHT);
		dataSplitItem.disable();
		
		menu = new Menu();
		menu.addStyleName("myMenu");
		
		
		
		addDataInstanceBut = new Button(LocaleText.get("addDataInstance"));
		addDataInstanceBut.setIcon(AbstractImagePrototype.create(images.dataInstancePicture()));
		addDataInstanceBut.setScale(ButtonScale.LARGE);
		addDataInstanceBut.setIconAlign(IconAlign.LEFT);
		addDataInstanceBut.addStyleName("myMenuButton");
		
		addDataElementBut = new Button(LocaleText.get("addDataElement"));
		addDataElementBut.setIcon(AbstractImagePrototype.create(images.dataElementPicture()));
		addDataElementBut.setScale(ButtonScale.LARGE);
		addDataElementBut.setIconAlign(IconAlign.LEFT);
		addDataElementBut.addStyleName("myMenuButton");
		addDataElementBut.disable();
		
		
		importDataCsvBut = new Button(LocaleText.get("importDataCsv"));
		importDataCsvBut.setIcon(AbstractImagePrototype.create(images.addRepeat()));
		importDataCsvBut.setScale(ButtonScale.LARGE);
		importDataCsvBut.setIconAlign(IconAlign.LEFT);
		importDataCsvBut.addStyleName("myMenuButton");
		
		menu.add(addDataInstanceBut);
		menu.add(addDataElementBut);
		menu.add(importDataCsvBut);
		dataSplitItem.setMenu(menu);
		group.addButton(dataSplitItem);
		toolBar.add(group);

		// ////////////////////THIRD GROUP/////////////////////////////////
		group = new ButtonGroup(3);
		group.setHeading("Clipboard");
		bcut = new Button("Cut", AbstractImagePrototype.create(images.cut()));
		bcut.setScale(ButtonScale.LARGE);
		bcut.setIconAlign(IconAlign.TOP);
		
		bcopy = new Button("Copy", AbstractImagePrototype.create(images.copy()));
		bcopy.setScale(ButtonScale.LARGE);
		bcopy.setIconAlign(IconAlign.TOP);
		
		bpaste = new Button("Paste", AbstractImagePrototype.create(images.paste()));
		bpaste.setScale(ButtonScale.LARGE);
		bpaste.setIconAlign(IconAlign.TOP);
		bpaste.disable(); // feature not ready yet
				
		
		
		group.addButton(bcut);
		group.addButton(bcopy);		
		group.addButton(bpaste);
		group.setHeight(95);
		toolBar.add(group);

		// /////////////////////////////////////////////////////////////////

		// ////////////////////FOURTH GROUP/////////////////////////////////
		group = new ButtonGroup(3);
		group.setHeading("Localization");
		group.setHeight(97);
		group.setBodyStyle("myGroupStyle");
		AbstractImagePrototype spacer = AbstractImagePrototype.create(images
				.emptyIcon());

		editLocale = new Button();
		editLocale.setText("Edit Locales");
		editLocale.setBorders(true);
		editLocale.setScale(ButtonScale.SMALL);
		// editLocale.disable(); //feature not ready yet
		group.addButton(editLocale);

		group.setButtonAlign(HorizontalAlignment.CENTER);
		Text lang = new Text();
		lang.setText("Language : ");
		group.add(lang);

		cb = new ComboBox<BaseModel>();
		cb.setDisplayField("name");

		populateLocales();
		// cb.setValue(cb.getStore().getAt(0));
		/*
		cb.addSelectionChangedListener(new SelectionChangedListener<BaseModel>() {
					public void selectionChanged(
							SelectionChangedEvent<BaseModel> se) {

						if (se.getSelection().size() > 0) {
							//controller.changeLocale(new Locale((String) se.getSelectedItem().get("key"), (String) se.getSelectedItem().get("name")));
							//Info.display("Alert", "Language Selected: " + se.getSelectedItem().get("name"));
						}
					}
				});
		*/

		cbLocales.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				
				int index = getCurrentLocaleIndex();
				ListBox listBox = (ListBox) event.getSource();
				Locale locale = new Locale(listBox.getValue(listBox.getSelectedIndex()), listBox.getItemText(listBox.getSelectedIndex()));
				if(controller.changeLocale(locale))
				{
					Context.setLocale(locale);
					populateLocales();
				}				
			}
		});

		cbLocales.setWidth("100px");
		group.add(/* cb */cbLocales);
		localeGroup = group;
		// group.addStyleName("localizationGroup");

		chkDefault = new CheckBox("Default");
		// group.add(chkDefault);

		toolBar.add(group);
		
		
		// ////////////////////FITH GROUP/////////////////////////////////
		group = new ButtonGroup(1);
		group.setHeading("KoboSync");
		bPostProcess = new Button("KoboSync", AbstractImagePrototype.create(images.postprocess()));
		bPostProcess.setScale(ButtonScale.LARGE);
		bPostProcess.setIconAlign(IconAlign.TOP);		
		group.addButton(bPostProcess);;
		group.setHeight(95);
		toolBar.add(group);
		
		//////////////////////SIXTH GROUP///////////////////////////////////
		group = new ButtonGroup(1);
		group.setHeading(LocaleText.get("help"));
		bHelp = new Button(LocaleText.get("help"), AbstractImagePrototype.create(images.helpPicture()));
		bHelp.setScale(ButtonScale.LARGE);
		bHelp.setIconAlign(IconAlign.TOP);		
		group.addButton(bHelp);
		group.setHeight(95);
			
		toolBar.add(group);
		

		toolBar.setEnableOverflow(true);

		Context.addLocaleListChangeListener(this);
	}

	public ToolBar getToolBar() {
		return toolBar;
	}
	
	public void setNewQuestionButtonEnable(boolean isEnabled)
	{
		splitItem.setEnabled(isEnabled);
		dataSplitItem.setEnabled(isEnabled);
		blockAddSplitItem.setEnabled(isEnabled);
		bdelete.setEnabled(isEnabled);
	}
	

	/**
	 * Setup button click event handlers.
	 */
	private void setupClickListeners() {

		addSelect.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				controller.addNewChildItem();
			}
		});

		bdelete.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				controller.deleteSelectedItem();

			}
		});

		dtabWidget.addFormSelectionListener(new IFormSelectionListener() {
			@Override
			public void onFormItemSelected(Object formItem) {
				checkEnableAddSelect((IFormElement) formItem);

			}
		});

		// addSelect.addListener(Events., listener)

		bcut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				bpaste.enable();
				controller.cutItem();				
			}
		});
		bcopy.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				bpaste.enable();
				controller.copyItem();				
			}
		});
		bpaste.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				controller.pasteItem();
			}
		});
		txtBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				splitItem.setText(txtBut.getText());
				splitItem.setIcon(txtBut.getIcon());
				splitItem.hideMenu();
				controller.addNewQuestion(QuestionDef.QTN_TYPE_TEXT);
				
			}

		});
		intBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				splitItem.setText(intBut.getText());
				splitItem.setIcon(intBut.getIcon());
				splitItem.hideMenu();
				controller.addNewQuestion(QuestionDef.QTN_TYPE_NUMERIC);
			}
		});
		decBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				splitItem.setText(decBut.getText());
				splitItem.setIcon(decBut.getIcon());
				splitItem.hideMenu();
				controller.addNewQuestion(QuestionDef.QTN_TYPE_DECIMAL);
			}
		});
		dateBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				splitItem.setText(dateBut.getText());
				splitItem.setIcon(dateBut.getIcon());
				splitItem.hideMenu();
				controller.addNewQuestion(QuestionDef.QTN_TYPE_DATE);
			}
		});
		singBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				splitItem.setText(singBut.getText());
				splitItem.setIcon(singBut.getIcon());
				splitItem.hideMenu();
				controller.addNewQuestion(QuestionDef.QTN_TYPE_LIST_EXCLUSIVE);
			}
		});
		multBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				splitItem.setText(multBut.getText());
				splitItem.setIcon(multBut.getIcon());
				splitItem.hideMenu();
				controller.addNewQuestion(QuestionDef.QTN_TYPE_LIST_MULTIPLE);
			}
		});
		// timeBut,datetimeBut,picBut,vidBut,audBut,gpsBut,bcdBut,grpBut,lblBut,rptBut
		timeBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				splitItem.setText(timeBut.getText());
				splitItem.setIcon(timeBut.getIcon());
				splitItem.hideMenu();
				controller.addNewQuestion(QuestionDef.QTN_TYPE_TIME);
			}
		});
		datetimeBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				splitItem.setText(datetimeBut.getText());
				splitItem.setIcon(datetimeBut.getIcon());
				splitItem.hideMenu();
				controller.addNewQuestion(QuestionDef.QTN_TYPE_DATE_TIME);				
			}
		});
		picBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				splitItem.setText(picBut.getText());
				splitItem.setIcon(picBut.getIcon());
				splitItem.hideMenu();
				controller.addNewQuestion(QuestionDef.QTN_TYPE_IMAGE);				
			}
		});
		vidBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				splitItem.setText(vidBut.getText());
				splitItem.setIcon(vidBut.getIcon());
				splitItem.hideMenu();
				controller.addNewQuestion(QuestionDef.QTN_TYPE_VIDEO);				
			}
		});
		audBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				splitItem.setText(audBut.getText());
				splitItem.setIcon(audBut.getIcon());
				splitItem.hideMenu();
				controller.addNewQuestion(QuestionDef.QTN_TYPE_AUDIO);				
			}
		});
		gpsBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				splitItem.setText(gpsBut.getText());
				splitItem.setIcon(gpsBut.getIcon());
				splitItem.hideMenu();
				controller.addNewQuestion(QuestionDef.QTN_TYPE_GPS);				
			}
		});
		bcdBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				splitItem.setText(bcdBut.getText());
				splitItem.setIcon(bcdBut.getIcon());
				splitItem.hideMenu();
				controller.addNewQuestion(QuestionDef.QTN_TYPE_BARCODE);				
			}
		});
		grpBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				splitItem.setText(grpBut.getText());
				splitItem.setIcon(grpBut.getIcon());
				splitItem.hideMenu();
				controller.addNewQuestion(QuestionDef.QTN_TYPE_GROUP);				
			}
		});
		triggerBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				splitItem.setText(triggerBut.getText());
				splitItem.setIcon(triggerBut.getIcon());
				splitItem.hideMenu();
				controller.addNewQuestion(QuestionDef.QTN_TYPE_TRIGGER);				
			}
		});
		
		addFromTextBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				blockAddSplitItem.hideMenu();
				controller.addBlockFromText();				
			}
		});
		
		
		addFromLocalBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				//controller.addBlockFromHTML5Local();
				blockAddSplitItem.hideMenu();
				controller.addBlockFromLibrary();				
			}
		});
				
		copySelectedToLibraryBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				blockAddSplitItem.hideMenu();
				controller.copySelectedToLibraryBut();				
			}
		});
		
		blockAddSplitItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				blockAddSplitItem.showMenu();
			}
		});
		
		rptBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				splitItem.setText(rptBut.getText());
				splitItem.setIcon(rptBut.getIcon());
				splitItem.hideMenu();
				controller.addNewQuestion(QuestionDef.QTN_TYPE_REPEAT);				
			}
		});
		
		importDataCsvBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				dataSplitItem.setText(importDataCsvBut.getText());
				dataSplitItem.setIcon(importDataCsvBut.getIcon());
				dataSplitItem.hideMenu();
				controller.importDataFromCSV();				
			}
		});
		
		addDataInstanceBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				dataSplitItem.setText(addDataInstanceBut.getText());
				dataSplitItem.setIcon(addDataInstanceBut.getIcon());
				dataSplitItem.hideMenu();
				controller.addDataInstance();				
			}
		});
		
		addDataElementBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				dataSplitItem.setText(addDataElementBut.getText());
				dataSplitItem.setIcon(addDataElementBut.getIcon());
				dataSplitItem.hideMenu();
				controller.addDataElement();								
			}
		});
		
		dataSplitItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				String t = dataSplitItem.getText();
				if(t.equals(LocaleText.get("addDataElement")))
				{
					controller.addDataElement();
				}
				else if(t.equals(LocaleText.get("addDataInstance")))
				{
					controller.addDataInstance();
				}
				else if(t.equals(LocaleText.get("importDataCsv")))
				{
					controller.importDataFromCSV();
				}
			}
		});
		
		blockAddSplitItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				String t = blockAddSplitItem.getText();

				if(t.equals(addFromTextBut.getText()))
				{
					controller.addBlockFromText();
				}
			}
		});

		splitItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				String t = splitItem.getText();
				if (t.equals(buttonLabels[0])) {
					controller.addNewQuestion(QuestionDef.QTN_TYPE_LIST_EXCLUSIVE);
				} else if (t.equals(buttonLabels[1])) {
					controller.addNewQuestion(QuestionDef.QTN_TYPE_TEXT);
				} else if (t.equals(buttonLabels[2])) {
					controller.addNewQuestion(QuestionDef.QTN_TYPE_NUMERIC);
				} else if (t.equals(buttonLabels[3])) {
					controller.addNewQuestion(QuestionDef.QTN_TYPE_DECIMAL);
				} else if (t.equals(buttonLabels[4])) {
					controller.addNewQuestion(QuestionDef.QTN_TYPE_DATE);
				} else if (t.equals(buttonLabels[5])) {
					controller
							.addNewQuestion(QuestionDef.QTN_TYPE_LIST_MULTIPLE);
				} else if (t.equals(buttonLabels[6])) {
					controller
							.addNewQuestion(QuestionDef.QTN_TYPE_LIST_EXCLUSIVE);
				} else if (t.equals(buttonLabels[14])) {
					controller.addNewQuestion(QuestionDef.QTN_TYPE_TIME);
				} else if (t.equals(buttonLabels[15])) {
					controller.addNewQuestion(QuestionDef.QTN_TYPE_DATE_TIME);
				} else if (t.equals(buttonLabels[16])) {
					controller.addNewQuestion(QuestionDef.QTN_TYPE_IMAGE);
				} else if (t.equals(buttonLabels[17])) {
					controller.addNewQuestion(QuestionDef.QTN_TYPE_VIDEO);
				} else if (t.equals(buttonLabels[18])) {
					controller.addNewQuestion(QuestionDef.QTN_TYPE_AUDIO);
				} else if (t.equals(buttonLabels[19])) {
					controller.addNewQuestion(QuestionDef.QTN_TYPE_GPS);
				} else if (t.equals(buttonLabels[20])) {
					controller.addNewQuestion(QuestionDef.QTN_TYPE_GROUP);
				} else if (t.equals(buttonLabels[21])) {
					controller.addNewQuestion(QuestionDef.QTN_TYPE_BARCODE);
				} else if (t.equals(buttonLabels[22])) {
					controller.addNewQuestion(QuestionDef.QTN_TYPE_TRIGGER);
				} else if (t.equals(buttonLabels[23])) {
					controller.addNewQuestion(QuestionDef.QTN_TYPE_REPEAT);
				} else {
					controller.addNewQuestion(QuestionDef.QTN_TYPE_TEXT);
				}
			}
		});

		saveBut.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				menuBut.hideMenu();
				controller.hideApplets();
				fileListener.onSave(true);
			}
		});
		
		spssBut.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				menuBut.hideMenu();
				controller.hideApplets();
				controller.createSPSS();
			}
		});

		openFileBut.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// fileListener.onSave(true);
				menuBut.hideMenu();
				controller.hideApplets();
				fileListener.onOpenFile();
			}
		});

		
//		the following is commented in order to bypass offline save file issues 
		saveFileBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) { //
				menuBut.hideMenu();
				fileListener.onSaveFile();
			}
		});
		
		saveToBrowserBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) { //
				menuBut.hideMenu();
				controller.hideApplets();
				controller.onSaveToBrowser();
			}
		});
		
		saveAsTemplateBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) { //
				menuBut.hideMenu();
				controller.hideApplets();
				controller.onSaveAsTemplate();
			}
		});
		
		loadFromBrowserBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) { //
				menuBut.hideMenu();
				controller.hideApplets();
				controller.onLoadFromBrowser();
			}
		});
		
		loadTemplateBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) { //
				menuBut.hideMenu();
				controller.hideApplets();
				controller.loadTemplate();
			}
		});
		
		deleteromBrowserBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) { //
				menuBut.hideMenu();
				controller.hideApplets();
				controller.onDeleteFromBrowser();
			}
		});
		
		
		bNewForm.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				menuBut.hideMenu();
				fileListener.onNew();
				setNewQuestionButtonEnable(true);				
			}
		});
		 
		menuBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				menuBut.showMenu();
			}
		});

		editLocale.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				fileListener.showItext();
			}
		});

		//Used to open a form from text
		openBut.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				controller.hideApplets();
				menuBut.hideMenu();
				fileListener.onOpen();
			}
		});


		
		bPostProcess.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				PostProcessProperties prop = new PostProcessProperties(null, null, null, null);
				menuBut.hideMenu();
				controller.postProcess(prop);
			}
		});
		
		bHelp.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				menuBut.hideMenu();
				controller.showHelpPage();
			}
		});
		
		
		bValidate.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				try
				{
					menuBut.hideMenu();
					FormDef form = controller.getSelectedForm();
					//if there's no form, bounce
					if(form == null) return;
					String xmlText = FormHandler.writeToXML(form);
					controller.validate(xmlText);
				}
				catch (Exception exp)
				{
					System.out.println(exp.getMessage());
					System.out.println(exp.getCause());
					for(StackTraceElement ste : exp.getStackTrace())
					{
						System.out.println(ste.toString());
					}
				}
				
			}
		});
		
	}//end of method

	/**
	 * Populates the locale drop down with a list of locales supported by the
	 * form designer.
	 */
	public void populateLocales() {
		cbLocales.clear();

		List<Locale> locales = Context.getLocales();
		if (locales == null)
			return;

		for (Locale locale : locales)
			cbLocales.addItem(locale.getName(), locale.getKey());

		cbLocales.setSelectedIndex(getCurrentLocaleIndex());
	}

	private int getCurrentLocaleIndex() {
		Locale currentLocale = Context.getLocale();

		List<Locale> locales = Context.getLocales();
		assert (locales != null);

		for (int index = 0; index < locales.size(); index++) {
			Locale locale = locales.get(index);
			if (locale.getKey().equals(currentLocale.getKey()))
				return index;
		}

		return 0;
	}

	public void onLocaleListChanged() {
		populateLocales();
	}

	public void checkEnableAddSelect(IFormElement formItem) {
		String text = "Add Select Option";

		if (formItem instanceof QuestionDef
				&& !((QuestionDef)formItem).usesItemSet()
				&& ((formItem.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE)
						|| (formItem.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE) || (formItem
						.getDataType() == QuestionDef.QTN_TYPE_REPEAT))) {
			if (formItem.getDataType() == QuestionDef.QTN_TYPE_REPEAT)
				text = "Add Repeat Child";

			addSelect.enable();
		} else if(formItem instanceof QuestionDef
				&& !((QuestionDef)formItem).usesItemSet()){
			addSelect.disable();
		} else if(formItem instanceof ItemSetDef){
			addSelect.disable();
		} else if (formItem instanceof OptionDef) {
			addSelect.enable();
		} else if ((formItem != null && formItem.getDataType() == QuestionDef.QTN_TYPE_REPEAT)
				|| (formItem != null && formItem.getParent() != null && formItem
						.getParent().getDataType() == QuestionDef.QTN_TYPE_REPEAT)) {
			text = "Add Repeat Child";
			addSelect.enable();
		} else if (formItem instanceof GroupDef) {
			addSelect.enable();
			text = "Add Group Child";
		} else {
			addSelect.disable();
		}
		
		addSelect.setText(text);
		
		//stuff for data instances
		//can't add a question when you're looking at data stuff
		if(formItem instanceof DataDefBase)
		{
			splitItem.disable();		
			addDataElementBut.enable();
		}
		else
		{
			splitItem.enable();
			addDataElementBut.disable();
			dataSplitItem.setText(addDataInstanceBut.getText());
			dataSplitItem.setIcon(addDataInstanceBut.getIcon());
		}
		//can only add a data element when on a data instance
		
	}
	
	/** 
	 * Can't add a question when you're on data stuff
	 * @param formItem
	 */
	public void checkAddQuestion(IFormElement formItem)
	{
		if(formItem instanceof DataDefBase)
		{
			splitItem.disable();
			dataSplitItem.enable();
		}
		else
		{
			splitItem.enable();
			dataSplitItem.disable();
		}
	}
	
	/**
	 * This is a huge hack, this function shouldn't be here, but a lot of classes
	 * that need to be able to see each other, can't so this way the FileListener
	 * can call the controller. Why both these classes aren't static is beyond me. It's
	 * not like we need more than one of them at any given time.
	 * 
	 * Anyway, call this guy from wherever you are to validate some XML
	 * 
	 * @param xmlStr String of XML to get validated
	 */
	public void validate(String xmlStr)
	{
		controller.validate(xmlStr);
	}

	@Override
	public void onDataTypeChanged(IFormElement element, int prevDataType) {
		checkEnableAddSelect(element);
	}
}
