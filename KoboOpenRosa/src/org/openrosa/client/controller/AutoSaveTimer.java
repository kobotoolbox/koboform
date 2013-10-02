package org.openrosa.client.controller;

import com.google.gwt.user.client.Timer;


public class AutoSaveTimer extends Timer{
	private FormDesignerController fdc = null;

	public AutoSaveTimer(FormDesignerController fdc)
	{
		this.fdc = fdc;
	}
	
	@Override
	public void run() {
		fdc.autoSave();
		
	}
	
	

}
