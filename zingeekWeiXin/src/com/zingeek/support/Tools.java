package com.zingeek.support;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

import com.zingeek.core.support.Utils;
import com.zingeek.support.excption.SysException;

public class Tools {
	/**
	 * 将bean对象转换为与客户端兼容的map对象
	 * @param bean
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> bean2ClientMap(Object bean) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		//获取bean对象的类对象
		Class klass = bean.getClass();
		
		//如果是系统内置类则不处理父级类
		boolean includeSuperClass = klass.getClassLoader() != null;
		//includeSuperClass = false;
		
		//获取类全部方法
		Method[] methods = (includeSuperClass) ? klass.getMethods() : klass.getDeclaredMethods();
		for (int i = 0; i < methods.length; i += 1) {
			try {
				Method method = methods[i];
				boolean isTransient = method.isAnnotationPresent(Transient.class);
				boolean isJoinColumn = method.isAnnotationPresent(PrimaryKeyJoinColumn.class);
				boolean isDate = method.isAnnotationPresent(ResultDate.class);
				//不处理外链和不对应表项的getter方法
				if (!isTransient && !isJoinColumn && Modifier.isPublic(method.getModifiers())) {
					String name = method.getName();
					String key = "";
					if (name.startsWith("get")) {
						if (name.equals("getClass") || name.equals("getDeclaringClass")) {
							key = "";
						} else {
							key = name.substring(3);
						}
					} else if (name.startsWith("is")) {
						key = name.substring(2);
					}
					boolean isRaw = false;
					//处理raw_开头的变量
					if (key.startsWith("Raw_")) {
						key = key.substring(4);
						isRaw = true;
					}
					if (key.length() > 0 &&( isRaw || Character.isUpperCase(key.charAt(0)) && method.getParameterTypes().length == 0)) {
						if (key.length() == 1) {
							key = key.toLowerCase();
						} else if (!Character.isUpperCase(key.charAt(1))) {
							key = key.substring(0, 1).toLowerCase() + key.substring(1);
						}
						Object result = method.invoke(bean, (Object[])null);
						if (result != null) {
							if(isRaw)
							{
								String strResult = (String)result;
								Object obj = null;
								char c = strResult.charAt(0);
								if(c == '{')
									obj = Utils.str2JSONObject(strResult);
								else if(c == '[')
									obj = Utils.str2JSONArray(strResult);
								result = obj;
							}
							else if(isDate){
								long lResult = (Long)result;
								if(lResult <= 0)
									result = null;
								else
									result = Utils.formatTimestamp(lResult - 8 * Utils.HOUR, "yyyy-MM-dd'T'HH:mm:ss'Z'");
							}
							map.put(key, result);
						}
					}
				}
			} catch (Exception e) {
				throw new SysException(e);
			}
		}
		
		return map;
	}
}