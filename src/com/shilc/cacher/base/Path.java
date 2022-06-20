package com.shilc.cacher.base;

import java.util.List;

public class Path {
	private List<Step> path;
	
	public Path(List<Step> path) {
		this.path = path;
	}
	
	public List<Step> getSteps(){
		return path;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		boolean first = true;
		for(Step step:path) {
			if(first) {
				first = false;
			}else {
				builder.append('-');
			}
			builder.append(step.getStepID());
		}
		
		return builder.toString();
	}
}
