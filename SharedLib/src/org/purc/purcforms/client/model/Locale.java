package org.purc.purcforms.client.model;


/**
 * Represents a locale or language supported by the form designer and runner.
 * 
 * @author daniel
 *
 */
public class Locale {

	/** The locale key. */
	private String key;
	
	/** The name of the locale. */
	private String name;
	
	
	/**
	 * Constructs a new locale object with a given key and name.
	 * 
	 * @param key the locale key.
	 * @param name the locale name.
	 */
	public Locale(String key, String name){
		this.key = key;
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString()
	{
		return this.name;
	}
	
	/**
	 * Overide .equals for our purposes
	 */
	public boolean equals(Object object)
	{
		if(object instanceof Locale)
		{
			//not sure if this should be case sensative or not, but I'm guessing not
			if(((Locale)object).getName().toLowerCase().equals(this.name.toLowerCase()))
			{
				return true;
			}
		}
		return false;
	}
}
