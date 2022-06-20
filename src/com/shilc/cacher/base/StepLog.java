package com.shilc.cacher.base;

import java.util.Date;

public class StepLog {
	private String stepID;
	private String userID;
	private Date logTime;
	
	public StepLog() {
		
	}
	
	public StepLog(String stepID, String userID, long logTime) {
		this.stepID = stepID;
		this.userID = userID;
		this.logTime = new Date(logTime);
	}
	
	public String getStepID() {
		return stepID;
	}
	public void setStepID(String stepID) {
		this.stepID = stepID;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public Date getLogTime() {
		return logTime;
	}
	public void setLogTime(Date logTime) {
		this.logTime = logTime;
	}
	
	@Override
	public String toString() {
		return userID+"::"+stepID+"@"+logTime;
	}
	
}
