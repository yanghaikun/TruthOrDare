package com.zingeek.weixin.entity;

public class UserFB extends User{
	public RoomFB room;
	//状态-1：错误；0；什么都没做；1：输入了开；10：输入了进；11：在房间中;12:已经玩过一局，开下一局；30： 要玩游戏；31：输入了名字；32：输入了在哪儿；33：输入了干什么
	public String who;
	public String where;
	public String what;
	
	public UserFB(String id) {
		super(id);
	}
}
