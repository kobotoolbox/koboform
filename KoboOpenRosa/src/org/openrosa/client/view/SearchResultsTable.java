package org.openrosa.client.view;

import java.util.ArrayList;

import org.openrosa.client.controller.FormDesignerController;
import org.openrosa.client.library.KoboFormItem;
import org.purc.purcforms.client.locale.LocaleText;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;

public class SearchResultsTable extends CellTable<KoboFormItem> {


	protected FormDesignerController controller;
	
	/**
	 * The wonderful default constructor
	 */
	public SearchResultsTable(FormDesignerController controller)
	{
		//setup the controller
		this.controller = controller;
		//setup a final controller
		final FormDesignerController fController = controller;
		//setup the table		
		setWidth("100%");
		//create the label column
		TextColumn<KoboFormItem> lableColumn = new TextColumn<KoboFormItem>(){
			@Override
			public String getValue(KoboFormItem kfi) {
				return kfi.name;
			}
		};
		//create the type column
		TextColumn<KoboFormItem> typeColumn = new TextColumn<KoboFormItem>(){
			@Override
			public String getValue(KoboFormItem kfi) {
				return kfi.type.toString();
			}
		};
		//create the type column
		TextColumn<KoboFormItem> subTypeColumn = new TextColumn<KoboFormItem>(){
			@Override
			public String getValue(KoboFormItem kfi) {
				return kfi.subType.toString();
			}
		};
		//create the number of questions column
		TextColumn<KoboFormItem> numberColumn = new TextColumn<KoboFormItem>(){
			@Override
			public String getValue(KoboFormItem kfi) {
				if(kfi.numberOfQuestions >= 1)
				{
					return kfi.numberOfQuestions + "";
				}
				
				return "N/A";
			}
		};		
		
		//tags column
		TextColumn<KoboFormItem> tagsColumn = new TextColumn<KoboFormItem>(){
			@Override
			public String getValue(KoboFormItem kfi) {
				return kfi.getTagString();
			}
		};		
				
		//add the columns
		addColumn(lableColumn, LocaleText.get("label"));
		addColumn(typeColumn, LocaleText.get("type"));
		addColumn(subTypeColumn, LocaleText.get("subType"));
		addColumn(numberColumn, LocaleText.get("numberOfQuestionsShort"));
		addColumn(tagsColumn, LocaleText.get("tagColumn"));
		// include cells are special	    
	    //create the button cell
	    ButtonCell includeCell = new ButtonCell();
	    //create the custom column
	    Column<KoboFormItem, String> includeColumn = new Column<KoboFormItem, String>(includeCell) 
	    {
	      @Override
	      public String getValue(KoboFormItem object) {
	        return "+";
	      }
	    };
	    //add the change handler
	    includeColumn.setFieldUpdater(new FieldUpdater<KoboFormItem, String>()
	    {
	    	 @Override
	         public void update(int index, KoboFormItem kfi, String value) {
	    		 try
	    		 {
	    			 fController.insertBlockQuestions(kfi.data);
	    		 }
	    		 catch(Exception e)
	    		 {
	    			 Window.alert(e.getMessage());
	    			 for(StackTraceElement ste : e.getStackTrace())
	    			 {
	    				 System.out.println(ste.toString());
	    			 }
	    		 }
	         }
	    });
	    //add the custom column
	    addColumn(includeColumn, "include");
		
		
		
		
		
		

		setRowCount(0);
	}
	
	/**
	 * Method for displaying search results
	 * This will be refined as time goes by
	 * @param results - the search results
	 */
	public void displayResults(ArrayList<KoboFormItem> results)
	{
		setRowData(results);
	}
	
}
