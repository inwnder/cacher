package com.shilc.cacher.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Logger {

	private static Map<String, Long> timer = new ConcurrentHashMap<String, Long>();
	
	public static void logTime() {
		System.out.println(System.currentTimeMillis());
	}
	
	public static void logTime(String tag) {
		System.out.println(tag+": "+System.currentTimeMillis());
	}
	
	public static void logStart(String event) {
		long now = System.currentTimeMillis();
		timer.put(event, now);
		logTime(event + " start");
	}
	
	public static void logEnd(String event) {
		long now = System.currentTimeMillis();
		Long start = timer.get(event);
		if(start == null) {
			throw new RuntimeException("Cannot find start time: "+event);
		}
		timer.remove(event);
		
		logTime(event + " end");
		
		long usedTime = now - start;
		System.out.println(event+" used time: " + usedTime + "ms");
	}
	
}
