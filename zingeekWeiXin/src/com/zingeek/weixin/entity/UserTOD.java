package com.zingeek.weixin.entity;

import com.zingeek.weixin.support.enumkey.StatusKeyTOD;

public class UserTOD extends User{
	public RoomTOD room;
	public StatusKeyTOD status = StatusKeyTOD.闲;
	public boolean knew = false;	//已经知道本局的结果了		
	public UserTOD(String id) {
		super(id);
	}
}
