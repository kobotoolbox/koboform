package org.openrosa.client.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for data items in an instance
 * @author etherton
 *
 */
public abstract class DataDefBase implements IFormElement {
	
	/** Keeps track of the kids*/
	private List<IFormElement> children;
	
	/** Parent, if any**/
	private IFormElement parent = null;
	
	/**
	 * Constructor
	 */
	public DataDefBase(IFormElement parent)
	{
		children = new ArrayList<IFormElement>();
		this.parent = parent;
	}
	
	/**
	 * Get the number of kids
	 * @return
	 */
	public int getChildCount(){
		if(children == null)
			return 0;
		return children.size();
	}

	/**
	 * Get the kid at a specified index
	 * @param index
	 * @return
	 */
	public IFormElement getChildAt(int index){
		return children.get(index);
	}
	
	/**
	 * Get all the kids
	 * @return
	 */
	public List<IFormElement> getChildren(){
		return children;
	}
	
	/**
	 * Add in a kid
	 * @param element
	 */
	public void addChild(DataDef element){
		children.add(element);
	}
	
	/**
	 * Gets rid of a kid
	 * @param index
	 */
	public void removeChildAt(int index)
	{
		children.remove(index);
	}
	
	/**
	 * Removes a specific kid
	 * @param kid
	 */
	public void removeChild(DataDef kid)
	{
		children.remove(kid);
	}
	
	/**
	 * Returns the string representation of the path of this item
	 * @return
	 */
	public abstract String getPath();
	
	/**
	 * Returns the string representation of the absolute path
	 * @return
	 */
	public abstract String getAbsolutePath();
	
	/**
	 * The "name" of a given element
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * Lets you find a kid by name
	 * @param name
	 * @return
	 */
	public DataDefBase getChildByName(String name)
	{
		for(IFormElement element : children)			
		{
			DataDefBase def = (DataDefBase)element;
			if(def.getName().equals(name))
			{
				return def;
			}
		}
		return null;
	}
	
	/***********************************************Things for IFormElement*********************************/


	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setText(String text) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getHelpText() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setHelpText(String helpText) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public int getDataType() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void setDataType(int dataType) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getBinding() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setBinding(String binding) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setChildren(List<IFormElement> children) {
		this.children = children;
		
	}


	@Override
	public void addChild(IFormElement element) {
		children.add(element);
		
	}


	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getIndexOfChild(IFormElement element) {
		return children.indexOf(element);
	}


	@Override
	public void insert(int index, IFormElement element) {
		children.add(index, element);
		
	}





	@Override
	public IFormElement getParent() {
		return  parent;
	}


	@Override
	public void setParent(IFormElement parent) {
		this.parent = parent;
		
	}


	@Override
	public IFormElement copy(IFormElement parent) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void clearChangeListeners() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getItextId() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setItextId(String id) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean removeChild(IFormElement element) {
		return children.remove(element);
	}


	@Override
	public FormDef getFormDef() {
		if(parent instanceof FormDef)
		{
			return (FormDef)parent;
		}
		return parent.getFormDef();
	}


	@Override
	public boolean isLocked() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isRequired() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public String getDefaultValue() {
		// TODO Auto-generated method stub
		return null;
	}


}
