package org.openrosa.client.view;

import java.util.ArrayList;

import org.openrosa.client.controller.FormDesignerController;
import org.openrosa.client.library.KoboItemTypes;
import org.openrosa.client.library.LibraryHelper;
import org.purc.purcforms.client.locale.LocaleText;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SearchPanel extends ContentPanel{

	

	/** Are we searching for question*/
	private CheckBox questionsCheckBox = null;
	
	/** Are we searching for blocks*/
	private CheckBox blockCheckBox = null;
	
	/** Are we searching for options*/
	private CheckBox optionsCheckBox = null;
	
	/** The table that holds the search criteria*/
	private FlexTable table = new FlexTable();
	
	/** Array keeps track of the search terms we're using*/
	private ArrayList<TextBox> searchTerms = new ArrayList<TextBox>();
	
	/**Dropdown of how we'll join together multiple search terms*/
	ListBox booleanListBox = null;
	
	/** Used to tell the users the progress of their search*/
	private ProgressBar pb = null;
	
	/** So the progress bar knows how far along we are*/
	private int thingsToSearchThrough = 0;
	
	/**So the progress bar knows how far along we are*/
	private int currentlySearchingThroughThingNumber = 0;
	
	private FormDesignerController controller = null;
	
	
	/**
	 * The classic default contructor. Does a few things to tidy up the place
	 */
	public SearchPanel(FormDesignerController controller)
	{
		setHeading(LocaleText.get("searchLocalLibrary"));
		this.controller = controller;
		setupUI();
	}
	
	
	/**
	 * This is called to create the UI components
	 */
	private void setupUI()
	{
		ContentPanel cp = this;//new ContentPanel();
		
		//make sure scrolling is turned on
		cp.setScrollMode(Scroll.AUTO);
		//set the background to white
		cp.setStyleAttribute("background-color", "white");
		
		//set the layout
		TableLayout layout = new TableLayout(2);		
	    layout.setCellVerticalAlign(VerticalAlignment.MIDDLE);	    
	    layout.setWidth("100%");
	    cp.setLayout(layout);
		
		//add some instructions
		Text searchInstructions = new Text();
		searchInstructions.setText(LocaleText.get("selectComponentsFor"));
		searchInstructions.setStyleAttribute("font-weight", "bold");
		searchInstructions.setStyleAttribute("padding", "5px");
		TableData td = new TableData();
		td.setColspan(2);
		cp.add(searchInstructions, td);

		//now add the check box for questions
		questionsCheckBox = new CheckBox();
		//questionsCheckBox.setEnabled(false);
		questionsCheckBox.setTitle(LocaleText.get("individualQuestions"));
		cp.add(questionsCheckBox);
		cp.add(new Text(LocaleText.get("individualQuestions")));
		
		//now add the check box for blocks
		blockCheckBox = new CheckBox();
		//questionsCheckBox.setEnabled(false);
		blockCheckBox.setTitle(LocaleText.get("blocks"));
		cp.add(blockCheckBox);
		cp.add(new Text(LocaleText.get("blocks")));
		
		//now add the check box for blocks
		optionsCheckBox = new CheckBox();
		//questionsCheckBox.setEnabled(false);
		optionsCheckBox.setTitle(LocaleText.get("options"));
		cp.add(optionsCheckBox);
		cp.add(new Text(LocaleText.get("options")));

		
		//add "search terms" text
		Text searchTermsTxt = new Text();
		searchTermsTxt.setText(LocaleText.get("searchTerms"));
		searchTermsTxt.setStyleAttribute("font-weight", "bold");
		searchTermsTxt.setStyleAttribute("padding", "5px");
		td = new TableData();
		td.setColspan(2);
		cp.add(searchTermsTxt, td);
		
		//add the multi term search stuff
		TextBox textBox = new TextBox();
		searchTerms.add(textBox);
		Button addTermButton = new Button("+");
		table.setWidget(0, 0, textBox);
		table.setWidget(0, 1, addTermButton);
		//set the handler for the button
		addTermButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){				
				handleAddRemoveButtonClick((Button)(event.getSource()));
			}
		});
		//add the search terms
		td = new TableData();
		td.setColspan(2);
		cp.add(table,td);
		
		//add the AND/OR stuff
		//add "search terms" text
		Text andOrTxt = new Text();
		andOrTxt.setText(LocaleText.get("andor"));
		andOrTxt.setStyleAttribute("font-weight", "bold");
		andOrTxt.setStyleAttribute("padding", "5px");
		td = new TableData();
		td.setColspan(2);
		cp.add(andOrTxt, td);
		
		//add the list box
		booleanListBox = new ListBox();
		booleanListBox.addItem(LocaleText.get("booleanOr"));
		booleanListBox.addItem(LocaleText.get("booleanAnd"));
		booleanListBox.setEnabled(false);
		td = new TableData();
		td.setColspan(2);
		cp.add(booleanListBox, td);
		
		
		//and add the search button
		Button searchButton = new Button(LocaleText.get("search"));
		//set the handler for the button
		searchButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){				
				search();
			}
		});
		searchButton.setWidth("100px");
		searchButton.getElement().setAttribute("style", "padding:5px 20px;");
		td = new TableData();
		td.setColspan(2);
		td.setHorizontalAlign(HorizontalAlignment.CENTER);
		cp.add(searchButton,td);
		
		//now some progress text or something
		pb = new ProgressBar();
		pb.setVisible(false);
		td = new TableData();
		td.setColspan(2);
		td.setPadding(15);
		td.setHorizontalAlign(HorizontalAlignment.CENTER);
		cp.add(pb,td);
		
		//search results
		SearchResultsPanel searchResultsPanel = new SearchResultsPanel(controller);
		//LibraryHelper.setSearchResultsPanel(searchResultsPanel);
		
		
		
		add(cp);
		//add(searchResultsPanel);
		
		SearchResultsTable srt = new SearchResultsTable(controller);
		LibraryHelper.setSearchResultsPanel(srt);
		ScrollPanel sp = new ScrollPanel();
		sp.setHeight("100%");
		sp.setWidth("100%");
		sp.add(srt);
		td = new TableData();
		td.setColspan(2);
		td.setHorizontalAlign(HorizontalAlignment.CENTER);
		add(sp,td);
		
		
		
	}//end setupUI()
	
	//Called when the add/remove buttons are pressed
	protected void handleAddRemoveButtonClick(Button button)
	{		
		//am i adding or removing
		boolean isAdding = button.getText().equals("+");
		//if I'm adding
		if(isAdding)
		{
			//get the next row
			int newRow = table.getRowCount();
			//make the text box
			TextBox textBox = new TextBox();
			searchTerms.add(textBox);
			//make the button
			Button addTermButton = new Button("+");
			Button removeTermButton = new Button("-");
			//add them to the table
			table.setWidget(newRow, 0, textBox);
			table.setWidget(newRow, 1, addTermButton);
			table.setWidget(newRow, 2, removeTermButton);		
			//make the button that was just pushed go away
			table.remove(button);
			//enable boolean logic
			booleanListBox.setEnabled(true);
			
			//add the event handler
			addTermButton.addClickHandler(new ClickHandler(){
				public void onClick(ClickEvent event){				
					handleAddRemoveButtonClick((Button)(event.getSource()));
				}
			});
			removeTermButton.addClickHandler(new ClickHandler(){
				public void onClick(ClickEvent event){				
					handleAddRemoveButtonClick((Button)(event.getSource()));
				}
			});
		}//end of hanlding adding
		else //why we must be removing things
		{
			//get the of the button
			int rowOfButton = getRowOfWidget(button);
			if(rowOfButton == -1){return;}//something went wrong
			
			//remove the text box from the list			
			Widget textbox = table.getWidget(rowOfButton, 0);
			searchTerms.remove(textbox);
			table.removeRow(rowOfButton);
			//check if there's an add button at the end, if not, add it
			if(table.getWidget(table.getRowCount()-1, 1) == null)
			{
				Button addTermButton = new Button("+");
				table.setWidget(table.getRowCount()-1, 1, addTermButton);
				//add the event handler
				addTermButton.addClickHandler(new ClickHandler(){
					public void onClick(ClickEvent event){				
						handleAddRemoveButtonClick((Button)(event.getSource()));
					}
				});
			}
			//if there's only one term then turn off AND/OR
			if(table.getRowCount() == 1)
			{
				booleanListBox.setEnabled(false);
			}
			
		}//end of removing
	}//end of HandleAddRemoveButtonClick()
	
	/**
	 * Use this to find what row a button is in
	 * @param button
	 * @return
	 */
	protected int getRowOfWidget(Button button)
	{
		//figure out what column we're looking in
		int column = button.getText().equals("+") ? 1 : 2;
		//init the row to zero
		int row = 0;
		//loop over the rows
		while(row < table.getRowCount())
		{
			try
			{
				if(table.getWidget(row, column) == button)
				{
					return row;
				}
			}
			catch(IndexOutOfBoundsException e)
			{
				
			}
			//don't forget to increment
			row++;
		}
		return -1;
	}//end of getRowOfWidget()
	
	
	/**
	 * This will start a search
	 */
	protected void search()
	{
		//first lets get the search terms
		ArrayList<String> terms = new ArrayList<String>();
		for(TextBox tb : searchTerms)
		{
			String temp = tb.getText();
			if(temp.length() == 0) //ignore zero length strings
			{
				continue;
			}
			terms.add(temp);
		}
		
		//get what boolean operator we're using
		boolean isAnd = booleanListBox.getSelectedIndex() == 1;
		
		//get the item types
		ArrayList<KoboItemTypes> itemTypes = new ArrayList<KoboItemTypes>();
		//if there are question items
		if(questionsCheckBox.getValue()) { itemTypes.add(KoboItemTypes.question);}
		//if there are blocks
		if(blockCheckBox.getValue()) { itemTypes.add(KoboItemTypes.block);}
		//if there are options
		if(optionsCheckBox.getValue()) { itemTypes.add(KoboItemTypes.options);}
			
		
		//let the Library helper deal with the rest
		LibraryHelper.search(terms, isAnd, itemTypes, this);
		
	}//end search()
	
	/**
	 * Used to turn on and initialize the status bar
	 * @param items number of points in the status bars span
	 */
	public void setupProgressBar(int items)
	{
		pb.setVisible(true);
		pb.reset();
		this.thingsToSearchThrough = items;
	}
	
	/**
	 * Used to update the progress bar
	 * @param value
	 */
	public void updateProgressBar()
	{
		currentlySearchingThroughThingNumber++;
		double value = (double)currentlySearchingThroughThingNumber / (double)thingsToSearchThrough;
		pb.updateProgress(value, "");
	}
	
	/**
	 * Used to hide and reset the progress bar 
	 */
	public void resetProgressBar()
	{
		pb.reset();
		pb.setVisible(false);
		currentlySearchingThroughThingNumber = 0;
		thingsToSearchThrough = 0;
	}
	
}//end of class SearchPanel
