package com.zingeek.support.excption;

import com.zingeek.core.support.Utils;


/**
 * 当程序出现可预知错误，并希望用户看到出错信息的时候抛出此异常
 * TODO 利用实时RealTime向用户发送错误信息
 */
public class MsgException extends RuntimeException {
	private static final long serialVersionUID = -8297248530227685523L;

	public MsgException(String userId, String msg) {
		super(msg);
	}
	
	public MsgException(String userId, String msg, Throwable e) {
		super(msg, e);
	}
	
	public MsgException(String userId, String msg, Object...params) {
		this(userId,  Utils.createStr(msg, params));
	}
	
	public MsgException(String userId, String msg, Throwable e, Object...params) {
		this(userId,  Utils.createStr(msg, params), e);
	}
}