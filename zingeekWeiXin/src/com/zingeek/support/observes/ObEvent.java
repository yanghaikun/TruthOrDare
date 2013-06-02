package com.zingeek.support.observes;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.zingeek.core.support.Utils;

@ApplicationScoped
public class ObEvent {
	@Inject Event<ObParam> eventParam;
	
	public static ObEvent instance(){
		return Utils.getComponent(ObEvent.class);
	}
	
	@SuppressWarnings("serial")
	public void fire(final ObKey key, ObParam param) {
		eventParam.select(new ObBinding() {
			public ObKey value() {
				return key;
			}
		}).fire(param);
		
	}
}