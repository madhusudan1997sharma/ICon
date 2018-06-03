package com.iconnect.icon.interfaces;
import com.iconnect.icon.types.FriendInfo;
import com.iconnect.icon.types.MessageInfo;


public interface IUpdateData {
	public void updateData(MessageInfo[] messages, FriendInfo[] friends, FriendInfo[] unApprovedFriends, String userKey);

}
