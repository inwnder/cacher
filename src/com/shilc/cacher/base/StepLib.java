package com.shilc.cacher.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepLib {
	private Map<String, Step> stepStore = new HashMap<String, Step>();
	
	public StepLib(List<Step> steps) {
		for(Step step:steps) {
			stepStore.put(step.getStepID(), step);
		}
	}
	
	public Step getStep(String stepID) {
		return stepStore.get(stepID);
	}
	
	public Step getStartStep() {
		return new Step("start", null);
	}
}
