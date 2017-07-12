package fulton.shaw.android.tellme.datafeeder;

import java.util.HashMap;

public interface UserManager{

	/**
	 * HashMap contains
	 *  	id(type:int)
	 *   	username(type:String)
	 *   	password(type:String)
	 *   	regdate(type:Date)
	 *   
	 * 
	 * @param username
	 * @return
	 */
	public HashMap<String, String> getUserInfo(int id);
	
	/**
	 * Return the id of a given username.If username does not exist,
	 * return -1.
	 * 
	 * @param username
	 * @return
	 */
	public int getUserId(String username);
	
	/**
	 * Register a user , the username must be given.Other infomation
	 * is stored in HashMap.It contains at least the following keys:
	 *   password (type:String)
	 *   regdate (type:Date)
	 * 
	 * !REMEMBER! you must add id when inserting it to the database
	 * @param username
	 * @param extraInfo
	 * @return the registered id.
	 */
	public int registerUser(String username,HashMap<String, String> extraInfo);
	
	public void updateUserInfo(int id,HashMap<String, String> extraInfo);
	
	/**
	 * post feedback.HashMap contains
	 * 	message (type:String)
	 * 	date (type:Date)
	 * 
	 * @param userid
	 * @param extraInfo
	 */
	public void postFeedBack(int userid,HashMap<String, String> extraInfo);
}
