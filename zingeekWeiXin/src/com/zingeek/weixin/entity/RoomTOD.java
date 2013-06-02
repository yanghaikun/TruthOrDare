package com.zingeek.weixin.entity;

import com.zingeek.support.RandomUtils;
import com.zingeek.weixin.support.enumkey.StatusKeyTOD;

public class RoomTOD extends Room<UserTOD>{
	public int random = 0;
	public String truthOrDare;
	public String choice;
	public boolean canPlay = true;
	public boolean start = false;			//本局游戏已经开始了
	public RoomTOD(int id) {
		super(id);
	}
	
	@Override
	public boolean isReady() {
		if(users.size() == count) {
			return true;
		}
		return false;
	}

	/**
	 * 随机结果
	 */
	public void random() {
		random = 0;
		choice = "";
		truthOrDare = "";
		random = 1 + RandomUtils.nextInt(count);
		start = true;
		
		for(UserTOD user : users) {
			user.status = StatusKeyTOD.准;
			user.knew = false;
		}
		timeUpdate = System.currentTimeMillis();
	}
	
	@Override
	public void clear() {
		super.clear();
		canPlay = true;
		start = false;
		random = 0;
		choice = "";
		truthOrDare = "";
		timeUpdate = System.currentTimeMillis();
	}
}
