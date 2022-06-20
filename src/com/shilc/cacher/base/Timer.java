package com.shilc.cacher.base;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * œµÕ≥ ±÷”
 * @author shilc
 *
 */
public class Timer {

	private static List<TimerEvent> eventList = new LinkedList<TimerEvent>(); 
	
	private static long now = 0;
	
	public static void setEvent(long trigTime, Runnable event) {
		
		TimerEvent timer = new TimerEvent(trigTime, event);
		
		synchronized(eventList){
			ListIterator<TimerEvent> itor = eventList.listIterator();
			TimerEvent itorEvent = null;
			boolean added = false;
			while(itor.hasNext()) {
				itorEvent = itor.next();
				if(itorEvent.getTrigTime() > trigTime) {
					itor.previous();
					itor.add(timer);
					added = true;
					break;
				}
			}
			
			if(!added) {
				itor.add(timer);
				added = true;
			}
		}
		
	}
	
	public static void trig(long trigTime) {
		
		synchronized(eventList){
			
			now = trigTime;
			
			ListIterator<TimerEvent> itor = eventList.listIterator();
			TimerEvent itorEvent = null;
			while(itor.hasNext()) {
				itorEvent = itor.next();
				if(itorEvent.getTrigTime() < trigTime) {
					try {
						itorEvent.trigEvent();
					}catch(RuntimeException e) {
						e.printStackTrace();
					}
					itor.remove();
				}
			}
		}
		
	}
	
	public static long getTime() {
		return now;
	}
	
	
}
