package org.openrosa.client.library;

public enum KoboItemTypes {
	
		block ("block"),
		question ("question"),
		options ("option"),
		template ("template"),
		questionInBlock ("Question in a Block");
		
		private final String text;
		/**
		 * Constructor
		 */
		KoboItemTypes(String text)
		{
			this.text = text;
		}
		
		/**
		 * To String
		 */
		public String toString()
		{
			return text;
		}
		
		/**
		 * Used to do a textual comparison
		 * @param text
		 * @return
		 */
		public boolean compareString(String text)
		{
			return text.equals(this.text);						
		}
		
		/**
		 * Given a string this creates an enum
		 * @param text
		 * @return
		 */
		public static KoboItemTypes fromString(String text)
		{
			for(KoboItemTypes type : KoboItemTypes.values())
			{
				if(type.compareString(text))
				{
					return type;				
				}
			}
			return null;
		}
	
}//end class
