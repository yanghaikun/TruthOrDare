package com.zingeek.support;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.zingeek.core.support.Utils;
import com.zingeek.support.excption.SysException;

@WebFilter(urlPatterns = "/rest/*", dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC, DispatcherType.ERROR})
public class HttpResponseHeaderFilter implements Filter {
	@Inject Req req;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		
		httpRequest.setCharacterEncoding("UTF-8");
		httpResponse.setHeader("Cache-Control", "no-cache");
		httpResponse.setContentType("text/html;charset=UTF-8");
		
		//设置参数
		req.setHttpRequest(httpRequest);
		
		//访问路径
		String uri = httpRequest.getRequestURI();
		
		
		//获取参数，并且验证
		String r = httpRequest.getParameter("r");
		if(StringUtils.isNotEmpty(r)){
               
			if(Sys.SYS_MD5_CHECK) {
				if(Base64.isUrlExecute(uri)) {
					r = new String(Base64.decodeBase64(r), "UTF-8");
				}
			}
			
			//首先get_session_key 不进行MD5验证。 在get_session_key的接口吧 client_key 发送到前端。
			//前端通过client_key和alchemy 联合加密 获得SYS_PRIVATE_CODE
			//加MD5参数验证
			if(Sys.SYS_MD5_CHECK) {
				//获取请求验证码，生成本地验证码
				String clientCode = httpRequest.getParameter("c");
				String serverCode = Utils.md5(Utils.md5(r) + Sys.SYS_PRIVATE_CODE);
				//验证码不匹配，报错
				if(!serverCode.equalsIgnoreCase(clientCode)) {
					throw new SysException("非法的请求：uri=" + uri + " query=" + r);
				}
			}
			
			req.setParams(r);
		}
		
		//启动zlib压缩
		request.setAttribute("zlib", req.getInt("zlib") == 1);

		//开始请求
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		
	}
}