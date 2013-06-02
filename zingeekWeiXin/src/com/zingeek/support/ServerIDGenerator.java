package com.zingeek.support;

import java.io.Serializable;
import java.util.Properties;
import java.util.UUID;

import javax.persistence.MappedSuperclass;

import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.AbstractUUIDGenerator;
import org.hibernate.type.Type;


@MappedSuperclass
public class ServerIDGenerator extends AbstractUUIDGenerator implements org.hibernate.id.Configurable {
	
	public ServerIDGenerator() {
		super();
	}
	
	public Serializable generate(SessionImplementor session, Object object) {
		//UUID32位后缀，由原来36位UUID去除"-"后得到的32位id
		String suffix = UUID.randomUUID().toString().replaceAll("-", "");
		
		return "slots-" + suffix;
	}
	
	@Override
	public void configure(Type arg0, Properties arg1, Dialect arg2)
			throws MappingException {
		// TODO Auto-generated method stub
		
	}
}