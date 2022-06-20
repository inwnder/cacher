package com.shilc.cacher.base;

/**
 * 批量概率融合
 * @author shilc
 *
 */
public class ProbabilityDistributionBatch {

	private double weight = 0;
	private double[] target;
	
	/**
	 * 设置权重初始化概率融合
	 * @param init 初始化概率
	 * @param weight 权重
	 */
	public ProbabilityDistributionBatch(ProbabilityDistribution init, double weight) {
		target = init.getValue().clone();
		for(int i = 0; i < target.length; ++i) {
			target[i] = target[i]*weight;
		}
		this.weight += weight;
	}
	
	/**
	 * 初始化概率融合
	 * @param init 初始化概率，设置权重为1
	 */
	public ProbabilityDistributionBatch(ProbabilityDistribution init) {
		target = init.getValue().clone();
		this.weight += 1;
	}
	
	/**
	 * 融合概率
	 * @param addee 被融合概率
	 * @return
	 */
	public ProbabilityDistributionBatch add(ProbabilityDistribution addee, double weight) {
		double[] addeeValue = addee.getValue();
		if(target.length != addeeValue.length) {
			throw new RuntimeException("Dimension not match.");
		}
		
		for(int i = 0; i < target.length; ++i) {
			target[i] = target[i] + addeeValue[i]*weight;	
		}
		this.weight += weight;
		return this;
	}
	
	/**
	 * 输出融合后概率
	 * @return
	 */
	public ProbabilityDistribution build() {
		double[] out = target.clone();
		for(int i = 0; i < out.length; ++i) {
			out[i] = out[i]/weight;	
		}
		return new ProbabilityDistribution(out, true);
	}
	
}
