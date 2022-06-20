package com.shilc.cacher.business;

import java.util.List;

import com.shilc.cacher.base.FutureStep;
import com.shilc.cacher.base.StepLog;
import com.shilc.cacher.base.UserProcess;

public interface StepDecider {
	public FutureStep decideNextStep(String userID, UserProcess currentProcess, String currentStepID);
	public void configure(List<StepLog> logs);
	public void configure(StepLog log);
}
