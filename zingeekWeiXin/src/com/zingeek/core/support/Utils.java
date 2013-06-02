package com.zingeek.core.support;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jboss.solder.beanManager.BeanManagerLocator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zingeek.support.excption.SysException;

public class Utils {
	
	public static final long SEC = 1000L;		//秒
	public static final long MIN = SEC * 60;		//分
	public static final long HOUR = MIN * 60;	//时
	public static final long DAY = HOUR * 24;	//天
	public static final long WEEK = DAY * 7;		//周
	
	/**
	 * 获取组件
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getComponent(final Class<T> type) {
		BeanManager manager = new BeanManagerLocator().getBeanManager();
		
        T result = null;
        Bean<T> bean = (Bean<T>) manager.resolve(manager.getBeans(type));
        if (bean != null) {
            CreationalContext<T> context = manager.createCreationalContext(bean);
            if (context != null) {
                result = (T) manager.getReference(bean, type, context);
            }
        }
        
        return result;
    }
	
	/**
	 * MD5加密
	 * @param s	被加密的字符串
	 * @return	加密后的字符串
	 */
	public static String md5(String s) {
		if(s == null) s = "";
		char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
		try {
			byte[] strTemp = s.getBytes("UTF-8");
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 按字节截取字符串<br />
	 * 双字节字符的长度算2<br />
	 * 单字节字符的长度算1
	 * @param str	被截取的字符串
	 * @param length	保留的长度
	 * @return	截取后的字符串
	 */
	public static String subString(String str, int length) {
		return subString(str, length, "");
	}
	
	/**
	 * 按字节截取字符串<br />
	 * 双字节字符的长度算2<br />
	 * 单字节字符的长度算1
	 * @param str	被截取的字符串
	 * @param length	保留的长度
	 * @param fill	如果被截取, 填充的字符串
	 * @return	截取后的字符串
	 */
	public static String subString(String str, int length, String fill) {
		if(length < 0) length = 0;
		
		int nowLength = 0;
		String returnStr = "";
		char[] tempCharArray = str.toCharArray();
		for(char tempChar : tempCharArray) {
			if (nowLength >= length) {
				if(!StringUtils.isEmpty(fill))returnStr += fill;
				break;
			}
			int size = countStringLength(String.valueOf(tempChar));
			nowLength += size;
			returnStr += tempChar;
		}

		return returnStr;
	}
	
	/**
	 * 获取字符串的长度<br />
	 * 双字节字符的长度算2<br />
	 * 单字节字符的长度算1
	 * @return
	 */
	public static int countStringLength(String str) {
		int result = 0;
		char[] chars = str.toCharArray();
		for(char c : chars) {
			if(String.valueOf(c).matches("[^x00-xff]")) {	//双字节
				result += 2;
			} else {										//单字节
				result += 1;
			}
		}
		
		return result;
	}
	
	/**
	 * 获得类泛型第一个参数的Class
	 * @param clazz
	 * @return
	 */
	public static <T> Class<T> getSuperClassGenricType(Class<?> clazz) {
		return getSuperClassGenricType(clazz, 0);
	}
	
	/**
	 * 获得类泛型参数的Class
	 * @param clazz
	 * @param index	第几个参数, 从0开始
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getSuperClassGenricType(Class<?> clazz, int index) {
		Type genType = clazz.getGenericSuperclass();
		//如果不是ParameterizedType接口的子类 就不可能含有泛型参数
		if (!(genType instanceof ParameterizedType)) return null;
		//泛型信息
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		//索引错误
		if (index >= params.length || index < 0) return null;
		//不是class类型
		if (!(params[index] instanceof Class)) return null;

		Class<T> result = (Class<T>) params[index];
		
		return result;
	}
	
	/**
	 * 生成一个仅包括数字和字母的随机字符串
	 * @param count
	 * @return
	 */
	public static String randomString(int count) {
		if(count < 0) count = 0;
		return RandomStringUtils.random(count, "abcdefghijklmnopqrstuvwxyz1234567890");
	}
	
	/**
	 * 获取文件的后缀名
	 * @param fileName 文件名称
	 * @return
	 */
	public static String getSuffix(String fileName) {
		return StringUtils.substringAfterLast(fileName, ".");
	}
	
	/**
	 * 返回log专用的时间格式 yyyy-MM-dd
	 * @param time
	 * @return
	 */
	public static String getDayStr4Log(long time) {
		return formatTimestamp(time, "yyyy-MM-dd");
	}
	
	/**
	 * 格式化时间戳
	 * @param value
	 * @param pattern
	 * @return
	 */
	public static String formatTimestamp(long timestamp, String pattern) {
		SimpleDateFormat format;
		//使用缓存format类
		if(DATA_FORMAT_CACHE.containsKey(pattern)) {
			format = DATA_FORMAT_CACHE.get(pattern);
		} else {	//新建format类
			format = new SimpleDateFormat(pattern);
		}

		return format.format(new Date(timestamp));
	}
	
	/**
	 * 根据时分秒配置 获取今天配置时间点
	 * @param suffix
	 * @return
	 */
	public static long formatDateStr(String dateStr, String pattern) {
		try {
			SimpleDateFormat bartDateFormat;
			//使用缓存format类
			if(DATA_FORMAT_CACHE.containsKey(pattern)) {
				bartDateFormat = DATA_FORMAT_CACHE.get(pattern);
			} else {	//新建format类
				bartDateFormat = new SimpleDateFormat(pattern);
			}
			
			return bartDateFormat.parse(dateStr).getTime();
		} catch (Exception ex) {
			throw new SysException(ex);
		}
	}
	private final static Map<String, SimpleDateFormat> DATA_FORMAT_CACHE = Utils.ofMap("yyyy-MM-dd HH:mm:ss", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
																						 "yyyy-MM-dd", new SimpleDateFormat("yyyy-MM-dd"),
																						 "HH:mm:ss", new SimpleDateFormat("HH:mm:ss"));
	
	/**
	 * 将秒改为时间格式
	 * @param sec
	 * @return
	 */
	public static String secondRangeToTime(int sec) {
		if(sec < 0) sec = 0;
		
		int temp = sec;
		//秒
		int s = temp % 60;
		temp = temp / 60;
		//分
		int m = temp % 60;
		temp = temp / 60;
		//时
		int h = temp;
		//时间
		String result = h < 10 ? "0" + h : h + "";
		result += ":" + (m < 10 ? "0" + m : m + "");
		result += ":" + (s < 10 ? "0" + s : s + "");
		
		return result;
	}
	
	/**
	 * 获取response
	 * @return
	 */
	public static HttpServletResponse getResponse() {
		return Utils.getComponent(HttpServletResponse.class);
	}

	/**
	 * 获取request
	 * 
	 * @return
	 */
	public static HttpServletRequest getRequest() {
		return Utils.getComponent(HttpServletRequest.class);
	}
	
	/**
	 * 获取session
	 * 
	 * @return
	 */
	public static HttpSession getSession() {
		return Utils.getComponent(HttpSession.class);
	}
	
	/**
	 * String转为int型
	 * 如果出错 则为0
	 * @param value
	 * @return
	 */
	public static int intValue(String value) {
		if(StringUtils.isNotEmpty(value) && NumberUtils.isNumber(value))
			return Double.valueOf(value).intValue();
		else
			return 0;
	}
	
	public static int intValue(Integer value) {
		if(null == value)
			return 0;
		else
			return value;
	}
	/**
	 * String转为long型
	 * 如果出错 则为0
	 * @param value
	 * @return
	 */
	public static long longValue(String value) {
		if(StringUtils.isNotEmpty(value) && NumberUtils.isNumber(value))
			return Double.valueOf(value).longValue();
		else
			return 0;
	}
	public static long longValue(Long value) {
		if(null == value)
			return 0;
		else
			return value;
	}
	/**
	 * String转为double型
	 * 如果出错 则为0.0
	 * @param value
	 * @return
	 */
	public static double doubleValue(String value) {
		if(StringUtils.isNotEmpty(value) && NumberUtils.isNumber(value))
			return Double.valueOf(value);
		else
			return 0.0D;
	}
	public static double doubleValue(Double value){
		if(null == value)
			return 0.0D;
		else
			return value;
	}
	
	/**
	 * String转为boolean型
	 * 如果出错 则为false
	 * @param value
	 * @return
	 */
	public static boolean booleanValue(String value) {
		if("true".equalsIgnoreCase(value) && value != null)
			return true;
		else
			return false;
	}
	
	/**
	 * 构造List对象
	 * @param <T>
	 * @param ts
	 * @return
	 */
	public static <T> List<T> ofList(T... ts) {
		List<T> result = new ArrayList<T>();
		for(T t : ts) {
			result.add(t);
		}
		
		return result;
	}
	
	/**
	 * 增加一个对象到数组最后面
	 * 
	 * @param t  要增加的对象
	 * @param ts 目标数组
	 * @return 合并后的数组
	 */
	public static <T> T[] arrAdd(T t, T[] ts){
		List<T> tempList = Utils.ofList(ts);
		tempList.add(t);
		return tempList.toArray(ts);
	}
	
	/**
	 * 将一个对象增加到数组的指定位置
	 * 
	 * @param t  要增加的对象
	 * @param index  放入的位置（数组下标）
	 * @param ts  目标数组
	 * @return
	 */
	public static <T> T[] arrAdd(T t, int index, T[] ts){
		List<T> tempList = Utils.ofList(ts);
		tempList.add(index, t);
		return tempList.toArray(ts);
	}
	
	/**
	 * 在一个数组中减去一个对象
	 * 
	 * @param t 要减去的对象
	 * @param ts 目标数组
	 * @param ta 空数组
	 * @return
	 */
	public static <T> T[] arrSub(T t, T[] ts, T[] ta){
		List<T> tempList = Utils.ofList(ts);
		tempList.remove(t);
		
		return tempList.toArray(ta);
	}
	
	/**
	 * 把Integer[] 转换成 int[]
	 * @param og
	 * @return
	 */
	public static int[] ofIntArray(Integer[] og) {
		int[] de = new int[og.length];
		for (int i = 0; i < og.length; i++) {
			de[i] = og[i];
		}
		return de;
	}
	
	/**
	 * 把List<Integer> 转换成 int[]
	 * @param og
	 * @return
	 */
	public static int[] ofIntArray(List<Integer> og) {
		int[] de = new int[og.size()];
		for (int i = 0; i < de.length; i++) {
			de[i] = og.get(i);
		}
		return de;
	}
	
	/**
	 * 把Double[] 转换成 double[]
	 * @param og
	 * @return
	 */
	public static double[] ofDoubleArray(Double[] og) {
		double[] de = new double[og.length];
		for (int i = 0; i < og.length; i++) {
			de[i] = og[i];
		}
		return de;
	}
	
	/**
	 * 把List<Double> 转换成 double[]
	 * @param og
	 * @return
	 */
	public static double[] ofDoubleArray(List<Double> og) {
		double[] de = new double[og.size()];
		for (int i = 0; i < de.length; i++) {
			de[i] = og.get(i);
		}
		return de;
	}
	
	/**
	 * 把int[] 转换成 Integer[]
	 * @param og
	 * @return
	 */
	public static Integer[] toIntegerArray(int[] og){
		Integer[] de = new Integer[og.length];
		for (int i = 0; i < og.length; i++) {
			de[i] = og[i];
		}
		return de;
	}
	
	/**
	 * 把double[] 转换成 Double[]
	 * @param og
	 * @return
	 */
	public static Double[] toDoubleArray(double[] og){
		Double[] de = new Double[og.length];
		for (int i = 0; i < og.length; i++) {
			de[i] = og[i];
		}
		return de;
	}
	
	/**
	 * 把int[] 转换成 List<Integer>
	 * @param og
	 * @return
	 */
	public static List<Integer> toIntegerList(int[] og){
		List<Integer> de = new ArrayList<Integer>();
		for (int i = 0; i < og.length; i++) {
			de.add(og[i]);
		}
		return de;
	}
	
	/**
	 * 把double[] 转换成 List<Double>
	 * @param og
	 * @return
	 */
	public static List<Double> toDoubleList(double[] og){
		List<Double> de = new ArrayList<Double>();
		for (int i = 0; i < og.length; i++) {
			de.add(og[i]);
		}
		return de;
	}
	
	/**
	 * 构造Map对象
	 * @param <T>
	 * @param ts
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> ofMap(Object...params) {
		LinkedHashMap<K, V> result = new LinkedHashMap<K, V>();
		
		//无参 返回空即可
		if(params == null || params.length == 0) {
			return result;
		}
		
		//处理成对参数
		int len = params.length;
		for (int i = 0; i < len; i += 2) {
			K key = (K) params[i];
			V val = (V) params[i + 1];
			
			result.put(key, val);
		}
		
		return result;
	}
	
	/**
	 * 基于参数创建字符串
	 * #0开始
	 * @param str
	 * @param params
	 * @return
	 */
	public static String createStr(String str, Object...params) {
		for(int i = 0; i < params.length; i++) {
			String s = params[i] == null ? "" : params[i].toString();
			str = str.replace("{" + i + "}", s);
		}
		
		return str;
	}
	
//	/**
//	 * 
//	 * @param urlStr
//	 * @param data
//	 * @param seed
//	 * @return
//	 */
//	public static String requestServer(String urlStr, String data, String seed){
//		try {
//			String urlStrTemp = createStr("#0?data=#1", urlStr, URLEncoder.encode(encode(data, seed), "UTF-8"));
//			URL url = new URL(urlStrTemp);
//			String result = IOUtils.toString(url.openStream(), "UTF-8");
//			return decode(result, seed);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
	
	/**
	 * 通过HTTP请求获取json格式的返回
	 * @param urlStr
	 * @param data
	 * @return
	 */
	public static JSONObject httpGetJSON(String urlStr, Map<Object, Object> params) {
		try {
			return JSON.parseObject(httpGet(urlStr, params));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 通过HTTP请求获取json格式的返回
	 * @param urlStr
	 * @param data
	 * @return
	 */
	public static String httpGet(String urlStr, Map<Object, Object> params) {
		try {
			if(params == null) params = new HashMap<Object, Object>();
			
			StringBuilder sb = new StringBuilder(urlStr);
			
			if(!params.isEmpty()) {
				sb.append("?");
			}
			
			for(Entry<Object, Object> entry : params.entrySet()) {
				sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue().toString(), "UTF-8")).append("&");
			}
			
			String urlStrFinal = sb.toString();
			
			//去除末尾的&
			if(urlStrFinal.endsWith("&")) {
				urlStrFinal = urlStrFinal.substring(0, urlStrFinal.length() - 1);
			}
			
			URL url = new URL(urlStrFinal);
			InputStream is = url.openStream();
			String result = IOUtils.toString(is);
			is.close();
			
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 通过HTTP请求获取json格式的返回
	 * @param urlStr
	 * @param data
	 * @return
	 */
	public static JSONObject httpPostJSON(String urlStr, Map<Object, Object> params) {
		String html = null;
		try {
			if (params == null)
				params = new HashMap<Object, Object>();

			StringBuilder sb = new StringBuilder(urlStr);
			if (!params.isEmpty()) {
				sb.append("?");
			}

			for (Entry<Object, Object> entry : params.entrySet()) {
				sb.append(entry.getKey())
						.append("=")
						.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8")).append("&");
			}

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			for (Entry<Object, Object> entry : params.entrySet()) {
				nvps.add(new BasicNameValuePair(entry.getKey().toString(),
						entry.getValue().toString()));
			}

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(urlStr);
			httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity httpEntity = response.getEntity();
			if (httpEntity != null) {
				html = EntityUtils.toString(httpEntity);
			}
			return JSON.parseObject(html);
		} catch (Exception e) {
			throw new SysException("返回内容为:" + html,  e);
		}
	}
	
	/**
	 * 添加一个COOKIE
	 * @param key
	 * @param value
	 * @param maxAge
	 */
	public static void addCookie(String key, String value, int maxAge) {
		addCookie(key, value, maxAge, getResponse());
	}
	
	/**
	 * 添加一个COOKIE
	 * @param key
	 * @param value
	 * @param maxAge
	 */
	public static void addCookie(String key, String value, int maxAge, HttpServletResponse response) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(maxAge);
		cookie.setPath("/");

		response.addCookie(cookie);
	}
	
	/**
	 * 获取COOKIE值
	 * @param key
	 * @param value
	 * @param maxAge
	 */
	public static String getCookieValue(String key, HttpServletRequest request) {
		for(Cookie c : request.getCookies()) {
			if(key.equals(c.getName())) {
				return c.getValue();
			}
		}
		
		return null;
	}
	
	/**
	 * 两个日期相差的天数
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static int getDaysBetween(Calendar d1, Calendar d2) {
		if (d1.after(d2)) {
			Calendar swap = d1;
			d1 = d2;
			d2 = swap;
		}
		
		int days = d2.get(Calendar.DAY_OF_YEAR) - d1.get(Calendar.DAY_OF_YEAR);
		int y2 = d2.get(Calendar.YEAR);
		if (d1.get(Calendar.YEAR) != y2) {
			d1 = (Calendar) d1.clone();
			do {
				days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);
				d1.add(Calendar.YEAR, 1);
			} while (d1.get(Calendar.YEAR) != y2);
		}
		return days;
	} 
	
	public static Integer[] builtArrByNum(int num){
		Integer[] arrs = new Integer[num];
		for (int i = 0; i < arrs.length; i++) {
			arrs[i] = i+1;
		}
		return arrs;
	}
	
	/**
	 * 将回车和空格转换成相应的HTML字符
	 * @param str
	 * @return
	 */
	public static String formatContentStr(String str) {
		str = str.replaceAll("<.*?>", "");
		str = str.replaceAll("\n", "<br/>");
		str = str.replaceAll("&", "&amp;");
		str = str.replaceAll(" ", "&#160;");
		str = str.replaceAll("\t", "&#160;&#160;&#160;&#160;");
		//UBB代码
		str = str.replaceAll("\\[url=(.+?)\\](.+?)\\[/url\\]", "<a href=\"$1\" target=\"_blank\">$2</a>");
		str = str.replaceAll("\\[class=(.+?)\\](.+?)\\[/class\\]", "<span class=\"$1\">$2</span>");
		
		return str;
	}
	
	/**
	 * 判断两个时间戳是否是同一天
	 * @param ta
	 * @param tb
	 * @return
	 */
	public static boolean isSameDay(long ta, long tb) {
		return Utils.formatTimestamp(ta, "yyyyMMdd").equals(Utils.formatTimestamp(tb, "yyyyMMdd"));
	}
	
	/**
	 * 获取给定时间当前凌晨的时间对象
	 * @param time 取当天凌晨的话传入 System.currentTimeMillis() 即可
	 * @return
	 */
	public static Calendar getBeginningOfToday(long time) {
		Calendar ca = Calendar.getInstance();
		ca.setTimeInMillis(time);
		ca.set(Calendar.HOUR_OF_DAY, 0);
		ca.set(Calendar.MINUTE, 0);
		ca.set(Calendar.SECOND, 0);
		ca.set(Calendar.MILLISECOND, 0);
		return ca;
	}
	
	/**
	 * 获取给定的时间与给定时间当天凌晨相隔的时间(毫秒数)
	 * @param time
	 * @return
	 */
	public static long getTimeFromBeginningOfDay(long time) {
		Calendar ca = Calendar.getInstance();
		ca.setTimeInMillis(time);
		ca.set(Calendar.HOUR_OF_DAY, 0);
		ca.set(Calendar.MINUTE, 0);
		ca.set(Calendar.SECOND, 0);
		ca.set(Calendar.MILLISECOND, 0);
		return time - ca.getTimeInMillis();
	}
	
	/**
	 * 判断当前时间是否在给定的小时范围内
	 * @param startHour 起始小时(包含)
	 * @param endHour 结束小时(不包含)
	 * @return
	 */
	public static boolean isBetweenHours(int startHour, int endHour) {
		Calendar ca = Calendar.getInstance();
		int nowHour = ca.get(Calendar.HOUR_OF_DAY);
		return nowHour >= startHour && nowHour < endHour;
	}
	
	/**
	 * 从字符串转为JSONObject，主要目的是包装一下异常处理
	 * @param str 
	 * @return 正常返回对象，否则返回长度为0的JSONObject
	 */
	public static JSONObject str2JSONObject(String str) {
		if(StringUtils.isEmpty(str)) {
			str = "{}";
		}
		return JSON.parseObject(str);
	}
	
	/**
	 * 从字符串转为JSONArray，主要目的是包装一下异常处理
	 * @param str 
	 * @return 正常返回对象，否则返回长度为0的JSONArray
	 */
	public static JSONArray str2JSONArray(String str) {
		if(StringUtils.isEmpty(str)) {
			str = "[]";
		}
		return JSON.parseArray(str);
	}
	
	public static String toJSONString(Object obj){
		return JSON.toJSONString(obj, SerializerFeature.DisableCircularReferenceDetect);
	}
	
	public static String joinStringList(List<String> list, char c){
		StringBuffer str = new StringBuffer();
		for(String s: list){
			str.append(s);
			str.append(c);
		}
		if(str.length() > 0)
			str.deleteCharAt(str.length() - 1);
		return str.toString();
	}
//	
//	/**
//	 * 将普通对象转换为JSON
//	 * @param obj
//	 * @return
//	 */
//	public static String objectToJSON(Object obj){
//		if(null == obj)
//			return null;
//		JSONObject jo = null;
//		if(obj instanceof Map)
//			jo = JSON.parseObject((Map<?,?>)obj);
//		else
//			jo = JSON.parseObject(obj);
//		return jo.toString();
//	}
//	
	/**
	 * 把一个double转换为0.0%的字符串
	 * @param d
	 * @return
	 */
	public static String double2Persent(Double d){
		NumberFormat format = new DecimalFormat("0.0'%'");
		return format.format(d);
	}
	
	/**
	 * 转换为Unicode码
	 */
	public static String encodeUnicode(final String gbString) {
		if(gbString == null) return "";
		
		char[] utfBytes = gbString.toCharArray();
		String unicodeBytes = "";
		for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
			String hexB = Integer.toHexString(utfBytes[byteIndex]);
			if (hexB.length() <= 2) {
				hexB = "00" + hexB;
			}
			unicodeBytes = unicodeBytes + "\\u" + hexB;
		}
		
		return unicodeBytes;
	}
	
	/**
	 * 对字符串进行URL编码
	 * @param str
	 * @return
	 */
	public static String urlEncoder(String str) {
		try {
			return URLEncoder.encode(str, "UTF8");
		} catch (Exception e) {
			throw new SysException(e);
		}
	}
	
	/**
	 * 获取物理路径
	 * @return
	 */
	public static String getRealPath() {
		if (StringUtils.isEmpty(REAL_PATH)) {
			REAL_PATH = Utils.class.getResource("/").getPath();
			REAL_PATH += "/../../../../../standalone/deployments/zingeekWeiXin.war";
		}
		return REAL_PATH;
	}
	private static String REAL_PATH;		//项目物理路径
}
