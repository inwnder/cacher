package com.shilc.cacher.base;

import java.util.ArrayList;
import java.util.List;

public class UserProcess {
	private List<StepLog> steps;
	
	public UserProcess(List<StepLog> steps) {
		this.steps = steps;
	}
	
	public List<StepLog> getSteps(){
		return steps;
	}
	
	public Path toPath(StepLib stepLib) {
		List<Step> steps = new ArrayList<Step>(this.steps.size());
		for(StepLog step:this.steps) {
			steps.add(stepLib.getStep(step.getStepID()));
		}
		
		return new Path(steps);
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for(StepLog step:steps) {
			if(first) {
				first = false;
			}else {
				builder.append("->");
			}
			builder.append(step.getStepID());
		}
		return builder.toString();
	}
}
