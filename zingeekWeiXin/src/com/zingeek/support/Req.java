package com.zingeek.support;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.zingeek.core.support.Utils;
import com.zingeek.support.excption.SysException;

@RequestScoped
public class Req implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JSONObject params = null;
	private HttpServletRequest httpReq = null;

	public HttpServletRequest getHttpServletRequest() {
		return httpReq;
	}
	public Object get(String key) {
		if (this.params == null) {
			return httpReq.getParameter(key);
		}
		return params.get(key);
	}

	public int getInt(String key) {
		int value = getSignedInt(key);
		if (value >= 0)
			return value;
		else
			throw new SysException(key + " must >= 0");
	}
	
	public long getLong(String key) {
		if (this.params == null) {
			return Utils.longValue(httpReq.getParameter(key));
		}
		return params.getLongValue(key);
	}

	public int getSignedInt(String key) {
		if (this.params == null) {
			return Utils.intValue(httpReq.getParameter(key));
		}
		return params.getIntValue(key);
	}

	public String getString(String key) {
		if (this.params == null) {
			return httpReq.getParameter(key);
		}

		String result = params.getString(key);
		if (StringUtils.isEmpty(result))
			return null;
		else
			return result;
	}

	public boolean getBoolean(String key) {
		if (this.params == null) {
			return Utils.booleanValue(httpReq.getParameter(key));
		}
		return params.getBooleanValue(key);
	}

	public double getDouble(String key) {
		if (this.params == null)
			return Utils.doubleValue(httpReq.getParameter(key));
		else
			return params.getDoubleValue(key);
	}

	void setParams(String params) {

		this.params = Utils.str2JSONObject(params);
		// if(this.params == null)
		// this.params = new JSONObject();
	}

	void setHttpRequest(HttpServletRequest httpReq) {
		this.httpReq = httpReq;
	}
}
