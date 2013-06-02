package com.zingeek.weixin.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import com.zingeek.core.support.Utils;
import com.zingeek.support.RandomUtils;
import com.zingeek.weixin.action.ActionTOD;
import com.zingeek.weixin.entity.RoomTOD;
import com.zingeek.weixin.entity.UserTOD;

 
/**
 * 真心话大冒险manager
 * @author Vincent Hiven Yang
 *
 */
@ApplicationScoped
public class ManagerTOD extends ManagerBase<UserTOD, RoomTOD>{
	private Map<Integer, String> 真心话 = new HashMap<Integer, String>();
	private Map<Integer, String> 大冒险 = new HashMap<Integer, String>();
	
	@Override
	public UserTOD getUser(String userId) {
		UserTOD user = users.get(userId);
		if(user == null) {
			user = new UserTOD(userId);
			users.put(userId, user);
		}
		
		return user;
	}

	@Override
	public RoomTOD getRoom(int roomId) {
		return rooms.get(roomId);
	}

	
	@Override
	public void createRoom(UserTOD user, int num) {
		RoomTOD room = null;
		for(RoomTOD r : rooms.values()) {
			//如果该房间长时间没活动，或者没有人了
			if((System.currentTimeMillis() - r.timeUpdate) > timeRoomClear || r.users.size() == 0) {
				for(UserTOD u : r.users) {
					removeUser(u);
				}
				r.clear();
				room = r;
				break;
			}
		}
		if(room == null) {
			largestRoomId += 10;
			room = new RoomTOD(largestRoomId);
			rooms.put(largestRoomId, room);
		}
		room.count = num;
		room.addUser(user);
		user.roomId = room.id;
		user.room = room;
	}

	public String truthOrDare(String key) {
		String result = "";
		if(ActionTOD.真.contains(key)) {
			if(真心话.isEmpty()) {
				File file = new File(Utils.getRealPath() + "/WEB-INF/classes/META-INF/truthOrDare/truth.txt");
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					String line;
					int i = 0;
					while((line = br.readLine()) != null) {
						真心话.put(i, line);
						i++;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			int rand = RandomUtils.nextInt(真心话.size());
			result =  真心话.get(rand);
			
		} else if(ActionTOD.大.contains(key)) {
			if(大冒险.isEmpty()) {
				File file = new File(Utils.getRealPath() + "/WEB-INF/classes/META-INF/truthOrDare/dare.txt");
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					String line;
					int i = 0;
					while((line = br.readLine()) != null) {
						大冒险.put(i, line);
						i++;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
			int rand = RandomUtils.nextInt(大冒险.size());
			result =  大冒险.get(rand);
		}
		return result;
	}
	
}
