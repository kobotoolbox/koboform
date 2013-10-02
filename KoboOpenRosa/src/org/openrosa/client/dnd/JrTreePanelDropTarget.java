package org.openrosa.client.dnd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.openrosa.client.model.DataDef;
import org.openrosa.client.model.DataDefBase;
import org.openrosa.client.model.DataInstanceDef;
import org.openrosa.client.model.IFormElement;
import org.openrosa.client.model.OptionDef;
import org.openrosa.client.model.QuestionDef;
import org.openrosa.client.model.TreeModelItem;
import org.openrosa.client.view.FormsTreeView;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.StatusProxy;
import com.extjs.gxt.ui.client.dnd.TreePanelDropTarget;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.store.TreeStoreModel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.TreeNode;


/**
 * This class acts as our drop target during movement of questions in the tree widget on the form outline.
 * 
 * @author daniel
 *
 */
public class JrTreePanelDropTarget extends TreePanelDropTarget{

	FormsTreeView formsTreeView;
	
	List<ModelData> list = null; //list of things that are being dragged
	
	
	public JrTreePanelDropTarget(TreePanel tree, FormsTreeView formsTreeView) {
		super(tree);
		
		this.formsTreeView = formsTreeView;
	}

	/**
	 * Used to make sure we don't drop stuff where it shouldn't go
	 */
	protected void showFeedback(DNDEvent event)
	{
		List<ModelData> models = event.getData();
		if (models.size() == 0 || !(models.get(0) instanceof TreeStoreModel)) 
			return; //No item to be dropped.
		
		
		TreePanel source = (TreePanel) event.getDragSource().getComponent();
		list = source.getSelectionModel().getSelection();
		
		//Get the model of the item being dropped and its position in the parent list.
		TreeModelItem modelItem = (TreeModelItem)((ModelData)models.get(0)).get("model");
		if(modelItem.getParent() == null)
		{
			StatusProxy sp = event.getStatus();
			sp.setStatus(false);
			return;
		}
		
		
		IFormElement droppedElement = (IFormElement)(modelItem.getUserObject());
		IFormElement droppedElementParent = droppedElement.getParent();

		final TreeNode overItem = tree.findNode(event.getTarget());
		if(overItem == null)
		{
			StatusProxy sp = event.getStatus();
			sp.setStatus(false);
			return;
		}
		TreeModelItem dropPointModelItem = ((TreeModelItem)overItem.getModel());
		IFormElement dropPointElement = (IFormElement)(dropPointModelItem.getUserObject());
		IFormElement dropPointElementParent = dropPointElement.getParent();
		
	
		//check if a question is being dropped into a data element or vice versa and then don't allow it
		if(droppedElement instanceof DataInstanceDef)
		{
			StatusProxy sp = event.getStatus();
			sp.setStatus(false);
			return;
		}
		else if(droppedElement instanceof DataDef && !(dropPointElement instanceof DataDefBase))
		{
			StatusProxy sp = event.getStatus();
			sp.setStatus(false);
			return;
		}
		else if(!(droppedElement instanceof DataDefBase) && dropPointElement instanceof DataDefBase)
		{
			StatusProxy sp = event.getStatus();
			sp.setStatus(false);
			return;
		}
		
		else if(droppedElement instanceof QuestionDef && dropPointElement instanceof QuestionDef)
		{
			feedback = Feedback.INSERT; // you can only insert a question between other questions
		}
		
		else if(droppedElement instanceof QuestionDef && dropPointElement instanceof OptionDef)
		{
			StatusProxy sp = event.getStatus();
			sp.setStatus(false);
			return;
		}
		else if(droppedElement instanceof OptionDef && !(dropPointElement instanceof OptionDef))
		{
			StatusProxy sp = event.getStatus();
			sp.setStatus(false);
			return;
		}
		/* Not sure how to get this exactly like I want, and I'm burning up hours, so I'll save this for a later time.
		else if(droppedElement instanceof DataDef && dropPointElement instanceof DataInstanceDef)
		{
			StatusProxy sp = event.getStatus();
			sp.setStatus(true);
			return;
		}
		else if(droppedElement instanceof DataDef && dropPointElement instanceof DataDef)
		{
			StatusProxy sp = event.getStatus();
			sp.setStatus(true);
			return;
		}
		*/
		super.showFeedback(event);
	}
	
	protected void handleAppendDrop(DNDEvent event, TreeNode item)
	{
		List<ModelData> models = event.getData();
		if (models.size() == 0 || !(models.get(0) instanceof TreeStoreModel)) 
			return; //No item to be dropped.
		    	
		//Get the model of the item being dropped and its position in the parent list.
		TreeModelItem modelItem = (TreeModelItem)((ModelData)models.get(0)).get("model");
		
		//Check if item the same item but not moved anywhere
		TreeModelItem dropPointModelItem = ((TreeModelItem)item.getModel());
		if(modelItem.getUserObject() == dropPointModelItem.getUserObject())
			return;
		
		IFormElement droppedElement = (IFormElement)(modelItem.getUserObject());
		
		IFormElement dropPointElement = (IFormElement)(dropPointModelItem.getUserObject());

		
		super.handleAppendDrop(event, item);
		
	}
	
	protected void handleInsertDrop(DNDEvent event, TreeNode item, int index) {
		
		List<ModelData> models = event.getData();
		if (models.size() == 0 || !(models.get(0) instanceof TreeStoreModel)) 
			return; //No item to be dropped.
		
		
		//Get the model of the item being dropped and its position in the parent list.
		TreeModelItem modelItem = (TreeModelItem)((ModelData)models.get(0)).get("model");
		int orgpos = modelItem.getParent().indexOf(modelItem);
		
		//Check if item the same item but not moved anywhere
		TreeModelItem dropPointModelItem = ((TreeModelItem)item.getModel());
		if(modelItem.getUserObject() == dropPointModelItem.getUserObject())
			return;
		
		
		IFormElement droppedElement = (IFormElement)(modelItem.getUserObject());
		
		IFormElement dropPointElement = (IFormElement)(dropPointModelItem.getUserObject());
		IFormElement dropPointElementParent = dropPointElement.getParent();		
		
		int newpos = item.getParent().indexOf(item);		
		
	
		int newItemIndex =newpos; //set the new position
		//if we've moved up the list, then add one to the drop
		boolean moveup = newpos < orgpos;
		if(moveup)
		{
			newItemIndex = newpos+1;
		}
		
		
		//adjust the drop spot depending on the direction of the move				
		
		
		
		super.handleInsertDrop(event, item, index);
		
		HashMap<Integer, IFormElement> mapToMove = new HashMap<Integer, IFormElement>();
		ArrayList<Integer> indexes = new ArrayList<Integer>();		

		for(ModelData md : list)
		{
			TreeModelItem tmi = (TreeModelItem)(md);
			int j = tmi.getParent().indexOf(tmi);
			Integer jObj = (Integer)j;
			IFormElement element = (IFormElement)(tmi.getUserObject());
			mapToMove.put(jObj, element);
			indexes.add(jObj);
		}
		
		//now sort
		Collections.sort(indexes);
		
		//now make the list 
		ArrayList<IFormElement> thingsToMove = new ArrayList<IFormElement>();
		for(Integer j : indexes)
		{
			thingsToMove.add(0,mapToMove.get(j));
		}

		formsTreeView.moveDropHandler(thingsToMove, dropPointElementParent, newItemIndex);
		
		
	}
}
