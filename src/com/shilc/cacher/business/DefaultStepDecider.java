package com.shilc.cacher.business;

import java.util.List;

import com.shilc.cacher.base.FutureStep;
import com.shilc.cacher.base.StepLog;
import com.shilc.cacher.base.UserProcess;

public class DefaultStepDecider implements StepDecider {

	//private List<>
	
	@Override
	public FutureStep decideNextStep(String userID, UserProcess currentProcess, String currentStepID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void configure(List<StepLog> logs) {
		for(StepLog log:logs) {
			configure(log);
		}
	}

	@Override
	public void configure(StepLog log) {
		
	}

}
