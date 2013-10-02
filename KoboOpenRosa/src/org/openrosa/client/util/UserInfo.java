package org.openrosa.client.util;

import java.util.Vector;

import org.openrosa.client.view.UserHeader;
import org.purc.purcforms.client.locale.LocaleText;

import com.google.code.gwt.storage.client.Storage;


/**
 * This class is used to store information on the user that are logged in locally, that is to say
 * users that are using this in "offline" mode.
 * 
 * This class uses HTML5 storage to keep track of users and their stuff in a 
 * persistent way.
 * 
 * @author etherton
 *
 */
public class UserInfo {
	
	
	
	protected static Storage localStore = null;
	
	protected final static String KEY_PATH = "org.openrosa.client.usersInfo.";
	protected final static String USERNAME = KEY_PATH + "username.";
	protected final static String PASSWORD = KEY_PATH + "password.";
	protected final static String CURRENT_USER = KEY_PATH + "currentUser";
	protected final static String CURRENT_FORM = KEY_PATH + "currentForm";
	protected final static String USER_LIST = KEY_PATH + "userList";
	protected final static String FORM = "form";
	protected final static String BLOCK = "block";
	protected final static String LAST_AUTO_SAVE = KEY_PATH + "lastAutoSave";
	protected final static String GRACEFUL_SHUT_DOWN = KEY_PATH + "gracefulShutDown";
	
	protected static UserHeader userHeader = null;
	
	/**
	 * Sets the userHeader
	 * @param userHeader the userHeader for this instance
	 */
	public static void setUserHeader(UserHeader userHeader) {
		UserInfo.userHeader = userHeader;
	}

	/**
	 * Check that storage works on the client's browser returns true if yes, false otherwise
	 * @return true if storage is supported, false otherwise.
	 */
	public static boolean isStorageSupported()
	{
		return Storage.isSupported();
	}
	
	/**
	 * use this to initialize things once you're sure that storage is supported
	 */
	public static void init(UserHeader userHeader)
	{
		localStore = Storage.getLocalStorage();
		UserInfo.userHeader = userHeader;
		
		//see if anyone is currently logged in
		String currentlyLoggedInUser = localStore.getItem(CURRENT_USER);
		if(currentlyLoggedInUser != null)
		{
			userHeader.setUserStatusText(currentlyLoggedInUser);
		}
		else
		{
			userHeader.setUserStatusText(LocaleText.get("notLoggedIn"));
		}
	}
	
	/**
	 * Given a user's name, this will return the last autosave, assuming one exists,
	 * if one doesn't exists, it'll return null.
	 * @param userName
	 * @return Returns the name of the last autosave, otherwise, returns false
	 */
	public static String getLastAutoSaveName(String userName)
	{
		return localStore.getItem(LAST_AUTO_SAVE + "." + userName);
	}
	
	/**
	 * Given a user name and the name of the last auto save this saves that autosave name for that user
	 * @param userName Name of the user who's getting auto saved
	 * @param autoSaveName Name of the auto saved form 
	 */
	public static void setLastAutoSaveName(String userName, String autoSaveName)
	{
		localStore.setItem(LAST_AUTO_SAVE + "." + userName, autoSaveName);
	}
	
	/**
	 * Sets if this was a graceful shut down or not
	 * @param userName name of the user in question
	 * @param gsd the gracefulshutdown enum
	 */
	public static void setGracefulShutDown(String userName, GracefulShutDown gsd )
	{
		localStore.setItem(GRACEFUL_SHUT_DOWN + "." + userName, gsd.toString());
	}
	
	/**
	 * Gets the graceful shut down state for a user
	 * @param userName the user in question
	 * @return is it graceful or not
	 */
	public static GracefulShutDown getGracefulShutDown(String userName)
	{
		String value = localStore.getItem(GRACEFUL_SHUT_DOWN + "." + userName);
		if (value == null)
		{
			return GracefulShutDown.unknown;
		}
		return GracefulShutDown.valueOf(value);
	}
	
	/**
	 * Removes a form
	 * @param userName Name of the owner of the form
	 * @param formName Name of the form in question
	 */
	public static void deleteForm(String userName, String formName)
	{
		localStore.removeItem(KEY_PATH + userName + "." + FORM + "." + formName);
	}
	
	
	/**
	 * Takes a user name and password and attempts to log that user in
	 * If they have correct credentials they'll be set to the new active user and true
	 * will be returned
	 * @param username username
	 * @param password password
	 * @return true if they were logged in, false otherwise
	 */
	public static boolean logIn(String username, String password)
	{
		//first check if there's a user name by that name
		String usernameVal = localStore.getItem(USERNAME + username);
		if(usernameVal == null)
		{
			return false;
		}
		
		//now get the password
		String passwordVal = localStore.getItem(PASSWORD + username);
		if(passwordVal == null) //the password shouldn't be null if there's already a user by that name, but you never know.
		{
			return false;
		}
		
		if(passwordVal.equals(password))
		{
			userHeader.setUserStatusText(username);
			
			//sets the graceful exit as graceful for the current user
			String userName = getCurrentUser();
			if(userName != null)
			{
				setGracefulShutDown(userName, GracefulShutDown.graceful);
			}

			
			//set this user as the current user
			localStore.setItem(CURRENT_USER, username);
			
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * Logs the current user out
	 */
	public static void logOut()
	{
		//sets the graceful exit as graceful
		String userName = getCurrentUser();
		if(userName != null)
		{
			setGracefulShutDown(userName, GracefulShutDown.graceful);
		}
		
		//removes this entry from storage
		localStore.removeItem(CURRENT_USER);
		userHeader.setUserStatusText(LocaleText.get("notLoggedIn"));
	}
	
	/**
	 * Lets new users sign up. Currently all we do is check to make sure the user doesn't already
	 * exists. We probably should make sure the password is legit and stuff at some point
	 * 
	 * @param username Username of new user
	 * @param password Password of new user
	 * @return returns true if there were no problems signing up. Returns false if the user already exists. 
	 */
	public static boolean signUp(String username, String password)
	{
		//make sure the username doesn't already exist
		String usernameVal = localStore.getItem(USERNAME + username);
		if(usernameVal != null)
		{
			return false;
		}
		
		
		//sets the graceful exit as graceful for the current user
		String userName = getCurrentUser();
		if(userName != null)
		{
			setGracefulShutDown(userName, GracefulShutDown.graceful);
		}
		
		
		//add the user to storage
		localStore.setItem(USERNAME + username, "true");
		localStore.setItem(PASSWORD + username, password);
		localStore.setItem(CURRENT_USER, username);
		
		//add the user to our list of users
		String userListStr = localStore.getItem(USER_LIST);
		if(userListStr == null)
		{
			userListStr = username;
		}
		else
		{
			userListStr += "\r\n" + username;
		}
		localStore.setItem(USER_LIST, userListStr);
		
		userHeader.setUserStatusText(username);
		return true;
	}
	
	/**
	 * Returns the current user, or null if there isn't one
	 * @return the current user, or null if there isn't one
	 */
	public static String getCurrentUser()
	{
		return localStore.getItem(CURRENT_USER);
	}
	
	/**
	 * Returns the current form, or null if there isn't one
	 * @return the current form, or null if there isn't one
	 */
	public static String getCurrentForm()
	{
		return localStore.getItem(CURRENT_FORM);
	}
	
	/**
	 * Gets a form given a form name and a user name
	 * @param formName The name of the form to retrieve
	 * @param userName The name of the user who's form we're looking for
	 * @return The form, or null if it doesn't exists
	 */
	public static String getForm(String userName, String formName)
	{
		return localStore.getItem(KEY_PATH + userName + "." + FORM + "." + formName);
	}
	
	/**
	 * Gets a block given a block name and a user name
	 * @param blockName The name of the block to retrieve
	 * @param userName The name of the user who's form we're looking for
	 * @return The block, or null if it doesn't exists
	 */
	public static String getBlock(String userName, String blockName)
	{
		return localStore.getItem(KEY_PATH + userName + "." + BLOCK + "." + blockName);
	}
	
	/**
	 * Saves a form to the browser's storage
	 * @param userName user name to store the form under
	 * @param formName name to store the form under
	 * @param formText form data itself
	 */
	public static void setForm(String userName, String formName, String formText)
	{
		localStore.setItem(KEY_PATH + userName + "." + FORM + "." + formName, formText);
	}
	
	/**
	 * Use this when you want to set a setting
	 * @param userName User name of the user that the setting belongs to
	 * @param settingKey  the name of the setting
	 * @param settingValue  the setting value
	 */
	public static void setSetting(String userName, String settingKey, String settingValue)
	{
		localStore.setItem(KEY_PATH + userName + "." + settingKey, settingValue);
	}
	
	
	/**
	 * Saves a block to the browser's storage
	 * @param userName user name to store the form under
	 * @param blockName name to store the block under
	 * @param blockText bock data itself
	 */
	public static void setBlock(String userName, String blockName, String blockText)
	{
		localStore.setItem(KEY_PATH + userName + "." + BLOCK + "." + blockName, blockText);
	}
	
	/**
	 * Used to retrieve a setting	
	 * @param userName Name of the user the setting belongs to
	 * @param settingKey the name of the setting
	 * @return the value of the setting
	 */
	public static String getSetting(String userName, String settingKey)
	{
		return localStore.getItem(KEY_PATH + userName + "." + settingKey);
	}
	
	/**
	 * Given a user name this returns a list of form names that belong to that user
	 * @param userName
	 * @return a list of form names that belong to the given user
	 */
	public static String[] getFormForUser(String userName)
	{

		Vector<String> formList = new Vector<String>();		
		for (int i=0; i < localStore.getLength(); i++)
		{
			//get the key
			String key = localStore.key(i);
			
			if(key.startsWith(KEY_PATH + userName + "." + FORM + "."))
			{
				//get the form name
				String formName = key.substring((KEY_PATH + userName + "." + FORM + ".").length());
				formList.add(formName);
			}
		}
		
		String[] arr;
		arr = (String[])formList.toArray(new String[formList.size()]);
		
		return arr;
	}
	
	
	/**
	 * Given a user name this returns a list of block names that belong to that user
	 * @param userName
	 * @return a list of block names that belong to the given user
	 */
	public static String[] getBlocksForUser(String userName)
	{

		Vector<String> formList = new Vector<String>();		
		for (int i=0; i < localStore.getLength(); i++)
		{
			//get the key
			String key = localStore.key(i);
			
			if(key.startsWith(KEY_PATH + userName + "." + BLOCK + "."))
			{
				//get the form name
				String formName = key.substring((KEY_PATH + userName + "." + BLOCK + ".").length());
				formList.add(formName);
			}
		}
		
		String[] arr;
		arr = (String[])formList.toArray(new String[formList.size()]);
		
		return arr;
	}
	
	
	/**
	 * Returns the number of bytes stored in storage. This way we can keep track of how much data we're using up.
	 * @return the number of bytes stored in storage.
	 */
	public static int getTotalBytesUsed()
	{
		int size = 0;
		for (int i=0; i < localStore.getLength(); i++)
		{
			//get the key
			String key = localStore.key(i);
			size += (localStore.getItem(key).length())*2;
			
		}
		return size;
	}

}


