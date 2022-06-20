package com.shilc.cacher.business;

import java.util.Date;
import java.util.List;

import com.shilc.cacher.base.Path;
import com.shilc.cacher.base.StepLog;
import com.shilc.cacher.base.UserProcess;

public interface ProcessDecider {
	public UserProcess decideCurrentProcess(String userID, Path current);
	public void configure(List<StepLog> logs);
	public void configure(StepLog log);
	public void trigTime(Date now);
}
