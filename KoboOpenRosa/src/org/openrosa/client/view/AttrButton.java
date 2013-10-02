package org.openrosa.client.view;

import com.google.gwt.user.client.ui.Button;

public class AttrButton extends Button {

	private String attributeName;
	
	public AttrButton(String s)
	{
		super(s);
	}
	
	/**
	 * Getter
	 * @return
	 */
	public String getAttributeName()
	{
		return attributeName;
	}
	
	/**
	 * Setter
	 * @param attributeName
	 */
	public void setAttributeName(String attributeName)
	{
		this.attributeName = attributeName;
	}
}
