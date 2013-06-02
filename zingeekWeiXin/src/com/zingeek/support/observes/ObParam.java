package com.zingeek.support.observes;

import java.io.Serializable;
import java.util.Map;

import com.zingeek.core.support.Utils;


public class ObParam implements Serializable {
	private static final long serialVersionUID = 1L;
	private Map<String, Object> map;

	/**
	 * 构造对象，发布事件支持多参数，初始化发布事件时的参数
	 * 例如event.fire(ObKey.FAMILY_USER_JOIN, new ObParam("user", user, "param", param));
	 * @param params
	 */
	public ObParam(Object...params) {
		map = Utils.ofMap(params);
	}
	
	/**
	 * 给对象添加新键值
	 * @param key
	 * @param value
	 * @return
	 */
	public ObParam put(String key, Object value) {
		map.put(key, value);
		return this;
	}
	
	/**
	 * 返回相应键值返回的value，已经经过强制转换处理
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) map.get(key);
	}
}