package com.zingeek.support;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;

import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.zingeek.core.support.Utils;

import flex.messaging.io.SerializationContext;
import flex.messaging.io.amf.Amf3Output;

@Named
public class Result implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final boolean ENABLE_AMF = false;

	/**
	 * 根据错误代码和原因生成统一格式的错误返回
	 * 
	 * @param code
	 * @param reason
	 * @return
	 */
	public static String error(int code, String reason) {
		Map<String, Object> map = Utils.ofMap("result", false, "code", code, "reason", reason);
		return raw(map);
	}
	
	public static String errorNoBase64(int code, String reason) {
		Map<String, Object> map = Utils.ofMap("result", false, "code", code, "reason", reason);
		return rawNoBase64(map);
	}
	
	/**
	 * 根据错误代码和原因生成统一格式的错误返回
	 * 
	 * @param code
	 * @param reason
	 * @return
	 */
	public static String error(String reason) {
		return error(-1, reason);
	}

	/**
	 * 生成统一格式的成功返回，包含给定的对象信息
	 * 
	 * @param value
	 * @return
	 */
	public static String success(Object value) {
		Map<String, Object> map = Utils.ofMap("result", true, "value", value);
		return raw(map);
	}
	public static String successNoBase64(Object value) {
		Map<String, Object> map = Utils.ofMap("result", true, "value", value);
		return rawNoBase64(map);
	}
	/**
	 * 生成统一格式的成功返回，包含给定的Map数据
	 * 
	 * @param value
	 * @return
	 */
	public static String success(Map<String, Object> value) {
		Map<String, Object> map = Utils.ofMap("result", true, "value", value);
		return raw(map);
	}

	/**
	 * 生成统一格式的成功返回，返回的是一个数组
	 * 
	 * @param value
	 * @return
	 */
	public static String success(List<?> value) {
		Map<String, Object> map = Utils.ofMap("result", true, "value", value);
		return raw(map);
	}

	// #兼容原来不太规则的代码

	public static String success_with_ext(Object value, Object ext) {
		Map<String, Object> map = Utils.ofMap("result", true, "value", value, "ext", ext);
		return raw(map);
	}

	public static String success_reason(Object value) {
		Map<String, Object> map = Utils.ofMap("result", true, "reason", value);
		return raw(map);
	}

	public static String success_result(Object result, Object info) {
		Map<String, Object> map = null;
		if (info == null)
			map = Utils.ofMap("result", result);
		else
			map = Utils.ofMap("result", result, "info", info);
		return raw(map);
	}

	public static String success_result(Object result) {
		return success_result(result, null);
	}

	public static String success_raw_json(Object value) {
		return resultString(Utils.toJSONString(value));
	}

	public static String success_raw_text(String value) {
		return resultString(value);
	}

	public static String success_raw(Map<?, ?> value) {
		return raw(value);
		// JSONObject jo = JSON.parseObject(value);
		// return resultString(jo.toString());
	}

	public static String resultBytes(byte[] bytes) {
		try {
			HttpServletResponse res = Utils.getResponse();
			IOUtils.write(bytes, res.getOutputStream());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return "";
	}
	public static String resultStringCharset(String str, String type) {
		try {
			HttpServletResponse res = Utils.getResponse();
			IOUtils.write(str, res.getOutputStream(), type);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return "";
	}
	
	/**
	 * 根据给定的数据直接生成返回值
	 * 
	 * @param data
	 * @return
	 */
	private static String raw(Map<?, ?> data) {
		if (ENABLE_AMF) {
			return resultAMF(data);
		} else {
			return resultJSON(data);
		}
	}
	
	private static String rawNoBase64(Map<?, ?> data) {
		if (ENABLE_AMF) {
			return resultAMF(data);
		} else {
			return resultNoBase64JSON(data);
		}
	}
	
	/**
	 * 返回值JSON格式
	 * 
	 * @param data
	 * @return
	 */
	private static String resultJSON(Map<?, ?> data) {
		String result = Utils.toJSONString(data);
		return resultString(result);
	}

	private static String resultString(String result) {
		try {
			HttpServletResponse res = Utils.getResponse();
			// 启动GZIP压缩
			boolean zlib = (Boolean) Utils.getRequest().getAttribute("zlib");
			String rand = (String) Utils.getRequest().getParameter("RealTime");
			if (zlib) {
				OutputStream out = new DeflaterOutputStream(res.getOutputStream());
				IOUtils.write(result, out, "UTF-8");
				out.close();
			} else {
				IOUtils.write(result, res.getOutputStream(), "UTF-8");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return "";
	}

	private static String resultNoBase64JSON(Map<?, ?> data) {
		String result = Utils.toJSONString(data);
		return resultNoBase64String(result);
	}
	
	private static String resultNoBase64String(String result) {
		try {
			HttpServletResponse res = Utils.getResponse();
			// 启动GZIP压缩
			boolean zlib = (Boolean) Utils.getRequest().getAttribute("zlib");
			String rand = (String) Utils.getRequest().getParameter("RealTime");
			if (zlib) {
				OutputStream out = new DeflaterOutputStream(res.getOutputStream());
				IOUtils.write(result, out, "UTF-8");
				out.close();
			} else {
//				//实时过来的信息不进行BASE64
//				if(!(StringUtils.isNotEmpty(rand) && rand.equals("noBase64"))){
//					if(Base64.isUrlClient(Utils.getRequest().getRequestURI())) {
//						result = Base64.encodeBase64String(result.getBytes("UTF-8"));
//					}
//				}
				IOUtils.write(result, res.getOutputStream(), "UTF-8");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return "";
	}
	/**
	 * 利用AMF3设置返回值
	 * 
	 * @param obj
	 * @param gzip
	 *            启动压缩
	 * @return
	 */
	private static String resultAMF(Object obj) {
		try {
			HttpServletResponse res = Utils.getResponse();
			Amf3Output out = new Amf3Output(new SerializationContext());

			// 启动GZIP压缩
			boolean zlib = (Boolean) Utils.getRequest().getAttribute("zlib");
			if (zlib) {
				res.setContentType("application/x-gzip-compressed");

				DeflaterOutputStream stream = new DeflaterOutputStream(new DataOutputStream(
						res.getOutputStream()));
				out.setOutputStream(stream);

				out.writeObject(obj);

				stream.finish();
			} else {
				DataOutputStream stream = new DataOutputStream(res.getOutputStream());
				out.setOutputStream(new DataOutputStream(res.getOutputStream()));

				out.writeObject(obj);

				stream.flush();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return "";
	}



	
	private static final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
			'B', 'C', 'D', 'E', 'F' };

	private static char toHex(int nibble) {
		return hexDigit[(nibble & 0xF)];
	}
	
	/**
	 * 把中文字符编码为\u0000形式,目前不使用
	 */
	/*	public static String rawUnicode(Map<?, ?> data) {
		if (ENABLE_AMF) {
			return resultAMF(data);
		} else {
			return resultJSON_Unicode(data);
		}
	}

	private static String resultJSON_Unicode(Map<?, ?> data) {
		String result = toUnicode(JSON.parseObject(data).toString(), true);
		return resultString(result);
	}
*/
	/**
	 * 将字符串编码成 Unicode 。
	 * 
	 * @param theString
	 *            待转换成Unicode编码的字符串。
	 * @param escapeSpace
	 *            是否忽略空格。
	 * @return 返回转换后Unicode编码的字符串。
	 */
	@SuppressWarnings("unused")
	private static String toUnicode(String theString, boolean escapeSpace) {
		int len = theString.length();
		int bufLen = len * 2;
		if (bufLen < 0) {
			bufLen = Integer.MAX_VALUE;
		}
		StringBuffer outBuffer = new StringBuffer(bufLen);

		for (int x = 0; x < len; x++) {
			char aChar = theString.charAt(x);
			if ((aChar < 0x0020) || (aChar > 0x007e)) {
				outBuffer.append('\\');
				outBuffer.append('u');
				outBuffer.append(toHex((aChar >> 12) & 0xF));
				outBuffer.append(toHex((aChar >> 8) & 0xF));
				outBuffer.append(toHex((aChar >> 4) & 0xF));
				outBuffer.append(toHex(aChar & 0xF));
			} else {
				outBuffer.append(aChar);
			}
		}
		return outBuffer.toString();
	}
}