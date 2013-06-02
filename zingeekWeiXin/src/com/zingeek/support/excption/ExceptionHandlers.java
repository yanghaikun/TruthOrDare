package com.zingeek.support.excption;

import javax.inject.Inject;
import javax.transaction.NotSupportedException;

import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.Handles;
import org.jboss.solder.exception.control.HandlesExceptions;
import org.jboss.solder.logging.Logger;

import com.zingeek.core.support.StopExceptionThread;


@HandlesExceptions
public class ExceptionHandlers {
	@Inject Logger log;
	
	/**
	 * ARJUNA016051异常处理
	 * ARJUNA016051: thread is already associated with a transaction
	 * @param e
	 */
	public void handlerNotSupportedException(@Handles CaughtException<NotSupportedException> e){
		try{			
			Thread thread = Thread.currentThread();
			StopExceptionThread.pushMsg(thread);
			
			thread.wait();
			
			e.handled();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}