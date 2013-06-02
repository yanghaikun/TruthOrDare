package com.zingeek.core.support;

import java.util.concurrent.LinkedBlockingQueue;

import javax.enterprise.event.Observes;
import javax.servlet.ServletContext;

import org.jboss.solder.servlet.event.Initialized;

public class StopExceptionThread {

	//消息队列
	private static final LinkedBlockingQueue<Thread> _listThread = new LinkedBlockingQueue<Thread>();	
	
	/**
	 * 初始化处理线程
	 */
	public void init(@Observes @Initialized ServletContext ctx) {
		StopExceptionThread.startSendThread();
	}

	/**
	 * 开启处理队列
	 */
	private static void startSendThread() {
		new Thread(new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				while (true) {
					Thread thread = null;
					
					try {
						//取得需要发送的信息 如没有消息 会自动等待
						thread = _listThread.take();
						
						//输出当前状态
						System.out.println("[ARJUNA016051]待杀死队列剩余数" + _listThread.size() + "：" + _listThread);
						System.out.println("[ARJUNA016051]" + thread.getName() + "队列中状态：" + thread.getState());
						System.out.println("[ARJUNA016051]当前线程" + thread.getState().toString() + "尝试重新停止线程, try to restop thread" + thread.getName());
						System.out.println("[ARJUNA016051]ARJUNA016051 is found! will stop：" + thread.getName());
						
						//停止错误线程
						thread.join(1000);
						thread.stop();
						
						//未能杀死该线程
						if(thread.isAlive()) {
							System.out.println("[ARJUNA016051]未能杀死该线程：" + thread.getName());
							
							pushMsg(thread);
						}
					} catch (Exception e) {
						System.out.println("[ARJUNA016051]尝试杀死线程时出错：" + thread);
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	/**
	 * 装入异常线程
	 * @param thread
	 */
	public static void pushMsg(Thread thread) {
		try {
			System.out.println("[ARJUNA016051]出现错误队列：" + thread.getName());
			
			//无需重复加入
			if(_listThread.contains(thread)) return;
			
			//加入队列
			_listThread.put(thread);
		} catch (InterruptedException e) {
			System.out.println("[ARJUNA016051]尝试加入待杀死线程队列时出错。");
			e.printStackTrace();
		}
	}
}
