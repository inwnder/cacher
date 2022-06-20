package com.shilc.cacher.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 流程提取器
 * 从StepLog中提取流程
 * @author shilc
 *
 */
public class ProcessExtracter {

	private static final long SESSION_TIME = 5*60*1000;
	private Map<String, List<StepLog>> stepLib = new ConcurrentHashMap<String, List<StepLog>>();
	private ConcurrentSkipListMap<Date, Set<String>> timeIndex = new ConcurrentSkipListMap<Date, Set<String>>();
	private ArrayList<UserProcess> processCache = new ArrayList<UserProcess>();
	
	/**
	 * 记录step
	 * @param log 需按照时间顺序触发
	 */
	public List<StepLog> putStepLog(StepLog log) {
		UserProcess process = getProcess(log);
		stepLib.put(log.getUserID(), process.getSteps());
		
		if(process.getSteps().size() > 1) {
			Date lastTime = process.getSteps().get(process.getSteps().size() - 2).getLogTime();
			Date time = log.getLogTime();
			updateTimeIndex(log.getUserID(), lastTime, time);
		}
		return process.getSteps();
	}
	
	/**
	 * 记录step
	 * @param log 需按照时间顺序触发
	 */
	public UserProcess getProcess(StepLog log) {
		Date time = log.getLogTime();
		String userID = log.getUserID();
		
		Date lastTime = null;
		
		List<StepLog> steps;
		
		//判断是否是process
		if(stepLib.containsKey(userID)) {
			steps = stepLib.get(userID);
			
			if(steps.size() == 0) {
				steps = new ArrayList<StepLog>();
				steps.add(log);
			}else {
				lastTime = steps.get(steps.size() - 1).getLogTime();
				if(time.getTime() - lastTime.getTime() > SESSION_TIME) {//超时			
					//重新记录steps
					steps = new ArrayList<StepLog>();
					steps.add(log);
				}else {//未超时则追加到steps
					steps.add(log);
				}
			}
		}else {//没有出现过，新的steps
			steps = new ArrayList<StepLog>();
			steps.add(log);
		}
		return new UserProcess(steps);
	}
	
	/**
	 * 用现在时间触发
	 * @param currentTime
	 */
	public void trigTime(Date currentTime) {
		Date trigTime = new Date(currentTime.getTime() - SESSION_TIME);		
		NavigableMap<Date, Set<String>> headMap = timeIndex.headMap(trigTime, false);
		for(Date time:headMap.keySet()) {
			Set<String> index = timeIndex.get(time);
			for(String userID:index) {
				List<StepLog> steps = stepLib.get(userID);
				UserProcess process = new UserProcess(steps);
				processCache.add(process);
				
				steps = new ArrayList<StepLog>();
				stepLib.put(userID, steps);
			}
			
			timeIndex.remove(time);
		}
	}
	
	public boolean notEmpty() {
		return !processCache.isEmpty();
	}
	
	public List<UserProcess> extractProcess(){
		@SuppressWarnings("unchecked")
		ArrayList<UserProcess> processes = (ArrayList<UserProcess>) processCache.clone();
		processCache.clear();
		return processes;
	}
	
	private void updateTimeIndex(String userID, Date lastTime, Date time) {
		//删除timeIndex中该用户之前的记录
		try {
			Set<String> lastIndex = timeIndex.get(lastTime);
			if(lastIndex != null) {
				lastIndex.remove(userID);
			}
		}catch(java.lang.NullPointerException e) {
			// do nothing..
		}
		
		//在timeIndex中加入该用户新的记录
		Set<String> index = timeIndex.get(time);
		if(index != null) {
			index.add(userID);
		}else {
			index = new HashSet<String>();
			index.add(userID);
			timeIndex.put(time, index);
		}
	}
	
	public static void main(String args[]) {
		ProcessExtracter extracter = new ProcessExtracter();
		
		List<StepLog> logs = new ArrayList<StepLog>();
		logs.add(new StepLog("login", "test1", 1630216723823l));
		logs.add(new StepLog("pay", "test1", 1630216734326l));
		logs.add(new StepLog("login", "test2", 1630217349670l));
		logs.add(new StepLog("pay", "test2", 1630217360173l));
		
		for(StepLog log:logs) {
			extracter.putStepLog(log);
		}
		
		extracter.trigTime(new Date(System.currentTimeMillis()));
		//extracter.trigTime(new Date(1630217360173l));
		
		List<UserProcess> processes = extracter.extractProcess();
		for(UserProcess process:processes) {
			String out = process.getSteps().get(0).getUserID() + ": ";
			for(StepLog step:process.getSteps()) {
				out+=step.getStepID() + "->";
			}
			out+="ENDPROCESS";
			System.out.println(out);
		}
		
	}
	
}
