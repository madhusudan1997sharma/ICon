package com.iconnect.icon.tools;

import com.iconnect.icon.types.FriendInfo;

/*
 * This class can store friendInfo and check userkey and username combination 
 * according to its stored data
 */
public class FriendController 
{
	
	private static FriendInfo[] friendsInfo = null;
	private static FriendInfo[] unapprovedFriendsInfo = null;
	private static String activeFriend;
	
	public static void setFriendsInfo(FriendInfo[] friendInfo)
	{
		FriendController.friendsInfo = friendInfo;
	}
	
	
	
	public static FriendInfo checkFriend(String name, String username, String userKey)
	{
		FriendInfo result = null;
		if (friendsInfo != null) 
		{
			for (int i = 0; i < friendsInfo.length; i++) 
			{
				if ( friendsInfo[i].name.equals(name) &&
					 friendsInfo[i].userName.equals(username) && 
					 friendsInfo[i].userKey.equals(userKey)
					)
				{
					result = friendsInfo[i];
					break;
				}				
			}			
		}		
		return result;
	}
	
	public static void setActiveFriend(String friendName){
		activeFriend = friendName;
	}
	
	public static String getActiveFriend()
	{
		return activeFriend;
	}



	public static FriendInfo getFriendInfo(String name, String username) 
	{
		FriendInfo result = null;
		if (friendsInfo != null) 
		{
			for (int i = 0; i < friendsInfo.length; i++) 
			{
				if ( friendsInfo[i].name.equals(name) &&
					 friendsInfo[i].userName.equals(username) )
				{
					result = friendsInfo[i];
					break;
				}				
			}			
		}		
		return result;
	}
	



	public static void setUnapprovedFriendsInfo(FriendInfo[] unapprovedFriends) {
		unapprovedFriendsInfo = unapprovedFriends;		
	}



	public static FriendInfo[] getFriendsInfo() {
		return friendsInfo;
	}



	public static FriendInfo[] getUnapprovedFriendsInfo() {
		return unapprovedFriendsInfo;
	}
	
	
	

}
