package org.openrosa.client.model;

import com.extjs.gxt.ui.client.data.BaseModel;

public class ItextModel extends BaseModel {

	public ItextModel(){
		
	}
	
	/**
	 * Copy constructor
	 * @param copy
	 */
	public ItextModel(ItextModel copy)
	{
		for(String key: copy.getPropertyNames())
		{
			Object val = copy.get(key);
			this.set(key, val);
		}
	}
	
	public String toString()
	{
		String retVal = "";
		for(String key : super.map.keySet())
		{
			retVal += key + ":" + super.map.get(key) + ", ";
		}
		return retVal;
	}
}
