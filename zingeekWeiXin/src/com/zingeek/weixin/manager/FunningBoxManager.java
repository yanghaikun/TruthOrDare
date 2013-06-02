package com.zingeek.weixin.manager;

import javax.enterprise.context.ApplicationScoped;

import com.zingeek.core.support.Utils;
import com.zingeek.weixin.entity.RoomFB;
import com.zingeek.weixin.entity.UserFB;

 
/**
 * 定义用户的所有操作
 * 充血模型，像钢铁侠的盔甲一样，有了这个类，user就可以有各种行为
 * @author Vincent Hiven Yang
 *
 */
@ApplicationScoped
public class FunningBoxManager extends ManagerBase<UserFB, RoomFB>{
	@Override
	public UserFB getUser(String id) {
		UserFB user =  users.get(id);
		if(user == null) {
			user = new UserFB(id);
		}
		users.put(id, user);
		return user;
	}
	@Override
	public RoomFB getRoom(int roomId) {
		RoomFB room = rooms.get(roomId);
		if(room == null) {
			room = new RoomFB(roomId);
			rooms.put(roomId, room);
		}
		return room;
	}
	
	public void start(UserFB user) {
		int roomId = createRoomId();
		user.status = 11;
		user.roomId = roomId;
		RoomFB room = getRoom(roomId);
		
		room.users.add(user);
	}
	
	public void out(UserFB user) {
		RoomFB room = rooms.get(user.roomId);
		user.roomId = 0;
		user.status = 0;
		if(room != null) {
			room.users.remove(user);
		}
		users.remove(user);
	}
	public int createRoomId() {
		if(largestRoomId < 10000) {
			return ++ largestRoomId;
		}
		
		for(RoomFB room : rooms.values()) {
			if(room.timeUpdate < (System.currentTimeMillis() - Utils.MIN * 10)) {
				room.clear();
				return room.id;
			}
		}
		
		return ++largestRoomId;
	}
	@Override
	public void createRoom(UserFB user, int num) {
		// TODO Auto-generated method stub
		
	}
}
