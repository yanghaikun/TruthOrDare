package com.zingeek.weixin.manager;

import java.util.HashMap;
import java.util.Map;

import com.zingeek.core.support.Utils;
import com.zingeek.weixin.entity.Room;
import com.zingeek.weixin.entity.User;


public abstract class ManagerBase <U extends User, R extends Room<U>>{
	protected int largestRoomId = 1000;
	protected long timeRoomClear = Utils.MIN * 10;		//10分钟没有人活动则清理房间
	
	protected Map<String, U> users = new HashMap<String, U>();
	protected Map<Integer, R> rooms = new HashMap<Integer, R>();
	
	//获取用户
	public abstract U getUser(String userId) ;
	
	//获取房间
	public abstract R getRoom(int roomId);
	
	//创建房间
	public abstract void createRoom(U user, int num);
	
	/**
	 * 回复文本消息
	 * @param ToUserName
	 * @param FromUserName
	 * @param createTime
	 * @param content
	 * @return
	 */
	public String responseText(String ToUserName, String FromUserName, long CreateTime, String Content) {
		String response = "<xml>" +
		 "<ToUserName><![CDATA[" + ToUserName + "]]></ToUserName>" +
		 "<FromUserName><![CDATA[" + FromUserName + "]]></FromUserName>" +
		 "<CreateTime>" + CreateTime + "</CreateTime>" +
		 "<MsgType><![CDATA[" + "text" + "]]></MsgType>" +
		 "<Content><![CDATA[" + Content + "]]></Content>" +
		 "<FuncFlag>" + 0 + "</FuncFlag>" +
		 "</xml>";
		
		return response;
	}
	
	public Map<String, U> getUsers() {
		return users;
	}
	public Map<Integer, R> getRooms() {
		return rooms;
	}
	public U removeUser(U u) {
		return users.remove(u.id);
	}
	public R removeRoom(R r) {
		return rooms.remove(r.id);
	}
}
