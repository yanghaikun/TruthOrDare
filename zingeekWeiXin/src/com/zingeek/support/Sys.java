package com.zingeek.support;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.transaction.Transactional;
import org.jboss.solder.servlet.WebApplication;
import org.jboss.solder.servlet.event.Initialized;

import com.zingeek.support.observes.ObEvent;
import com.zingeek.support.observes.ObKey;
import com.zingeek.support.observes.ObParam;

@Named("sys")
@ApplicationScoped
public class Sys {
	public static String SYS_PRIVATE_CODE = "g@=g<nj:Au";		//私钥 g@=g<nj:Au
	public static String PRIVATE_KEY;		//私钥 g@=g<nj:Au
	
	public static String GAME_I18N;		//语言版本
	public static boolean SYS_MD5_CHECK;	//开启MD5参数验证
	
	@Inject ObEvent event;
	
	
	/**
	 * 初始化全部静态属性
	 */
	@Transactional
	public void init(@Observes @Initialized WebApplication webapp) {
		event.fire(ObKey.GAME_START, new ObParam());
	}
}