package com.shilc.cacher.base;

public class StepLet implements Comparable<StepLet>{
	private String stepID;
	private int f = 0;
	private ProbabilityDistributionBuilder pdBuilder;
	private final static int DISTRIBUTION_SIZE = 500;
	private StepLet() {
		pdBuilder = new ProbabilityDistributionBuilder(DISTRIBUTION_SIZE);
	}
	
	public static StepLet createFor(Step step) {
		StepLet stepLet = new StepLet();
		stepLet.setStepID(step.getStepID());
		return stepLet;
	}
	
	public static StepLet createFor(Step step, int initDisbX) {
		StepLet stepLet = new StepLet();
		stepLet.setStepID(step.getStepID());
		stepLet.setF(1);
		stepLet.addSample(initDisbX);
		return stepLet;
	}
	
	public static StepLet createFor(String stepID) {
		StepLet stepLet = new StepLet();
		stepLet.setStepID(stepID);
		return stepLet;
	}
	
	public static StepLet createFor(String stepID, int initDisbX) {
		StepLet stepLet = new StepLet();
		stepLet.setStepID(stepID);
		stepLet.setF(1);
		stepLet.addSample(initDisbX);
		return stepLet;
	}
	
	public void addSample(int x) {
		pdBuilder.addSample(x);
	}

	public ProbabilityDistribution getProbDisb(){
		return pdBuilder.build();
	}
	
	public String getStepID() {
		return stepID;
	}
	public void setStepID(String stepID) {
		this.stepID = stepID;
	}
	public int getF() {
		return f;
	}
	public void setF(int f) {
		this.f = f;
	}
	public void addF() {
		++this.f;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof StepLet) {
			return ((StepLet)obj).getStepID().equals(stepID);
		}else {
			return obj.equals(this);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(stepID).append('*').append(f);
		return builder.toString();
	}

	@Override
	public int compareTo(StepLet o) {
		return this.stepID.hashCode() - o.stepID.hashCode();
	}
}
