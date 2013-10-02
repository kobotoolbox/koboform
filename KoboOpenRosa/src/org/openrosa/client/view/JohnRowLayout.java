package org.openrosa.client.view;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SplitBarEvent;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.SplitBar;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class JohnRowLayout extends RowLayout{

protected boolean hasResizedOccured = false;

	/**
	 * Creates a new row layout with the given orientation.
	 * 
	 * @param orientation the orientation of row layout
	 */
	public JohnRowLayout(Orientation orientation) 
	{
		super(orientation);
	  
	}
	
	protected void layoutHorizontal(El target) 
	{
		Size size = target.getStyleSize();
		
		//get the total width we have to play with
		int w = size.width - (this.isAdjustForScroll() ? 19 : 0);
		//the total height.
		int h = size.height;
		//the number of containers we have
		int count = container.getItemCount();
		//current X location
		int tx = 0;
		//current Y location
		int ty = 0;
		//the margin between rows
		final int margin = 4;
		//the initial width of each row is the total width, minus the number of margins, which is
		//one less than the number of rows, divided by the number of rows
		int tw = (w - ((count - 2) * margin)) / (count - 1);
		//the height of our rows equals the total available height
		int th = h;
		//we want to keep track of the row to the left of the current row
		Component componentToYourLeft = null;
		//loop over the components
		for (int i = 0; i < count; i++) 
		{
		
			final Component c = container.getItem(i);
			if(!c.isVisible(false) || i == 0)
			{
				continue;
			}
		
		
			if(i > 1 && c.getData("splitBar") == null)
			{
				//create a split bar
				SplitBar bar = new SplitBar(LayoutRegion.WEST, (BoxComponent)c);
				//use finals for whatever reason, guess I should learn and not just copy and paste
				final SplitBar fbar = bar;
				final Component fComponentToYourLeft = componentToYourLeft;
				//set the data of the item
				c.setData("splitBar", bar);
				c.setData("componentToYourLeft", componentToYourLeft);
				c.setData("jWidth", tw);
				
				//setup the listener when you start
				Listener<ComponentEvent> splitBarListener = new Listener<ComponentEvent>() 
				{
					public void handleEvent(ComponentEvent ce) 
					{		 
						//we can't have things smaller than 20px or bigger than the size of both things minus 20
						fbar.setMinSize(20);
						fbar.setMaxSize(c.getOffsetWidth() + fComponentToYourLeft.getOffsetWidth() - 20);
					}
				};
				//set the drag start listener
				bar.addListener(Events.DragStart, splitBarListener);
				bar.setAutoSize(false);
				
				//setup the listener when you finish
				bar.addListener(Events.DragEnd, new Listener<SplitBarEvent>() 
				{			
					public void handleEvent(SplitBarEvent sbe) 
					{
						//if it's less than one ignore it	
						if (sbe.getSize() < 1) 
						{
							return;
						}
						//get the component in question
						Component c = sbe.getSplitBar().getResizeWidget();
						//get the component to the component in question's left
						Component ctyl = (Component)(c.getData("componentToYourLeft"));
						//calc the old width of those two combinded
						int oldWidth = (Integer)(ctyl.getData("jWidth")) + (Integer)(c.getData("jWidth"));
						//now set the new size of the current component
						c.setData("jWidth", sbe.getSize());
						//now set the size of the component to your left
						fComponentToYourLeft.setData("jWidth", oldWidth - sbe.getSize());
						//a resize has occured so calculate things differently now
						hasResizedOccured = true;
						//force a redraw
						layout();
					}
				});
			}//end if initalizing stuff
			else if(i == 1 && c.getData("jWidth") == null)
			{
				c.setData("jWidth", tw);
			}
			
			//somehow this makes it so things render correctly
			c.el().makePositionable(true);
			c.el().setStyleAttribute("margin", "0px");
			
			//a resize occured, so don't use the regular width			
			if(hasResizedOccured)
			{
				tw = (Integer)(c.getData("jWidth"));
				setPosition(c, tx, ty);				
			}
			//catch the last component in case the width has changed since a resize
			if(hasResizedOccured && i == (count-1) )
			{
				if((tx + tw + margin) < w)
				{
					tw += w - (tx + tw + margin);
				}
			}
			//update the position
			setPosition(c, tx, ty);
			//update the size
			setSize(c, tw, th);
			//move things over to the right
			tx += tw + margin;
			

			
			//assign the previous component
			componentToYourLeft = c;

		}//end for loop
	}//end layoutHorizonal()
}//end class
