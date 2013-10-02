package org.openrosa.client.view;

import com.google.gwt.user.client.ui.TextBox;

public class AttrTextBox extends TextBox {
	
	private String attributeName;
	
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
