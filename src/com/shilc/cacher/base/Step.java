package com.shilc.cacher.base;

import java.util.Set;

public class Step {
	private Set<Resource<?>> resources;
	private String stepID;
	
	public Step(String stepID, Set<Resource<?>> resources) {
		this.resources = resources;
		this.stepID = stepID;
	}
	
	public void addResource(Resource<?> resource) {
		resources.add(resource);
	}
	
	public Set<Resource<?>> getResources(){
		return resources;
	}

	public String getStepID() {
		return stepID;
	}

	
}
