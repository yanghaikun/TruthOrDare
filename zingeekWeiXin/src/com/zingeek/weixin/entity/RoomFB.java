package com.zingeek.weixin.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zingeek.support.RandomUtils;

public class RoomFB extends Room<UserFB>{
	public int id;
	public boolean ready;
	public List<UserFB> users;
	public Map<String, String> result;
	public long timeUpdate;		//最后活跃时间
	
	public RoomFB(int id) {
		super(id);
		users = new ArrayList<UserFB>();
		result = new HashMap<String, String>();
	}
	
	public static void main(String [] args) {
		for(int i = 0; i < 10; i++) {
			System.out.println(RandomUtils.nextInt(1));
		}
	}
	
	public void play() {
		if(!ready) {
			return;
		}
		
		//打乱结果
		int size = users.size();
		
		for(UserFB user : users) {
			int where = RandomUtils.nextInt(size);
			String temp = user.where;
			UserFB userTemp = users.get(where);
			user.where = userTemp.where;
			userTemp.where = temp;
			
			int what = RandomUtils.nextInt(size);
			temp = user.what;
			userTemp = users.get(what);
			user.what = userTemp.what;
			userTemp.what = temp;
		}
		
		//生成回复结果
		List<UserFB> foo = new ArrayList<UserFB>(Arrays.asList(new UserFB[size]));
		Collections.copy(foo, users);
		for(UserFB user : users) {
			int n = RandomUtils.nextInt(size);
			UserFB u = foo.get(n);
			result.put(user.id, u.who + "在" + u.where + u.what);
			foo.remove(u);
			size = foo.size();
		}
		
		timeUpdate = System.currentTimeMillis();
		
		//清理工作
		for(UserFB user : users) {
			user.status = 33;
			user.where = "";
			user.what = "";
		}
	}
	
	public void check() {
		ready = true;
		for(UserFB u : users) {
			if(u.status != 33) {
				ready =false;
				break;
			}
		}
		play();
	}
	@Override
	public void clear() {
		super.clear();
		result.clear();
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}
}
