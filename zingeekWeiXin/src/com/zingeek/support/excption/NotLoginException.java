package com.zingeek.support.excption;


/**
 * 当用户未登录时 抛出的异常
 */
public class NotLoginException extends RuntimeException {
	private static final long serialVersionUID = -8297248530227685523L;

	public NotLoginException() {
		super("玩家未登录");
	}
	
	public NotLoginException(String str) {
		super(str);
	}
	
	public NotLoginException(Throwable e) {
		super(e);
	}
	
	public NotLoginException(String str, Throwable e) {
		super(str, e);
	}
}