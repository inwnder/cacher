package com.shilc.cacher.base;

public class TimerEvent {

	private long trigTime;
	private Runnable event;
	
	public TimerEvent(long trigTime, Runnable event) {
		this.trigTime = trigTime;
		this.event = event;
	}
	
	public long getTrigTime() {
		return trigTime;
	}
	
	public void trigEvent() {
		event.run();
	}
}
