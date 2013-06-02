package com.zingeek.weixin.entity;

import java.util.ArrayList;
import java.util.List;

public abstract class Room<U extends User> {
	public int id;
	public int count;					//人数
	public boolean ready;
	public long timeUpdate;		//最后活跃时间
	public List<U> users;
	
	public Room(int id) {
		this.id = id;
		this.timeUpdate = System.currentTimeMillis();
		users = new ArrayList<U>();
	}
	
	public void addUser(U user) {
		users.add(user);
		user.num = users.size();
	}
	public void removeUser(U user) {
		users.remove(user);
	}
	
	public boolean canJoin() {
		if(users.size() == 0 || users.size() < count) {
			return true;
		}
		return false;
	}
	
	public void join(U user) {
		if(!canJoin()) {
			return;
		}
		users.add(user);
		user.roomId = id;
		user.num = users.size();
	}
	public void clear() {
		for(U u : users) {
			u = null;
		}
		users.clear();
		count = 0;
		ready = false;
	}
	
	//判断是否准备好了
	public abstract boolean isReady();
}
