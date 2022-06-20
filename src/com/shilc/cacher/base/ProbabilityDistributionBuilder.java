package com.shilc.cacher.base;

/**
 * 概率分布构造器
 * @author shilc
 *
 */
public class ProbabilityDistributionBuilder {
	
	/**
	 * 采样邻域宽度
	 * 标志着一个采样点可以影响的概率宽度
	 */
	public final static int NEIGHBOUR_WIDTH = 3;
	private int[] samples;
	
	//体积，采样个数*概率宽度
	private int volume;
	
	public ProbabilityDistributionBuilder(int size) {
		samples = new int[size];
		for(int i = 0; i < size; ++i) {
			samples[i] = 0;
		}
		volume = 0;
	}
	
	public void addSample(int x) {
		int start = x - NEIGHBOUR_WIDTH;
		if(start < 0) {
			start = 0;
		}
		
		int end = x + NEIGHBOUR_WIDTH;
		
		for(int i = start; i <= end; ++i) {
			++samples[i];
		}
		
		volume += (end - start);
	}
	
	/**
	 * 构造概率分布
	 * @return
	 */
	public final ProbabilityDistribution build() {
		double[] pd = new double[samples.length];
		for(int i = 0; i < samples.length; ++i) {
			if(samples[i] != 0) {
				pd[i] = ((double)samples[i])/volume;
			}else {
				pd[i] = 0;
			}
		}
		return new ProbabilityDistribution(pd, true);
	}
	
}
