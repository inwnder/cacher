package com.shilc.cacher.base;

/**
 * 概率分布
 * @author shilc
 *
 */
public class ProbabilityDistribution {
	//分布
	private final double[] disb;
	
	public ProbabilityDistribution(final double[] disb) {
		this.disb = disb.clone();
	}
	
	public ProbabilityDistribution(final double[] disb, boolean noclone) {
		if(noclone) {
			this.disb = disb;
		}else {
			this.disb = disb.clone();
		}
	}
	
	public double[] getValue() {
		return disb;
	}
	
	/**
	 * 输出分布积分为p正规化的分布
	 * @param p 分布积分值 [0,1]
	 * @return
	 */
	public double[] getNormalizdValueWith(double p) {
		if(p < 0 || p > 1) {
			throw new RuntimeException("p should in range in 0-1.");
		}
		
		double sum = 0;
		for(int i = 0; i < disb.length; ++i) {
			sum += disb[i];
		}
		
		double[] norm = new double[disb.length];
		for(int i = 0; i < disb.length; ++i) {
			norm[i] = disb[i]/sum*p;
		}
		
		return norm;
	}
	
	/**
	 * 输出正规化的分布（分布积分为1）
	 * @return
	 */
	public double[] getNormalizdValue() {
		double sum = 0;
		for(int i = 0; i < disb.length; ++i) {
			sum += disb[i];
		}
		
		double[] norm = new double[disb.length];
		for(int i = 0; i < disb.length; ++i) {
			norm[i] = disb[i]/sum;
		}
		
		return norm;
	}
	
	/**
	 * 与新的概率分布融合
	 * @param another 新的概率分布(维度需要一致)
	 * @param weight 比重 新概率/现有概率 
	 * @return
	 */
	public ProbabilityDistribution add(ProbabilityDistribution another, double weight) {
		if(this.disb.length != another.disb.length) {
			throw new RuntimeException("Dimension not match.");
		}

		double[] target = new double[this.disb.length];
		for(int i = 0; i < target.length; ++i) {
			target[i] = (this.disb[i] + another.disb[i]*weight)/(1+weight);	
		}
		
		return new ProbabilityDistribution(target, true);
	}
	
	/**
	 * 发起概率融合批量
	 * @return
	 */
	public ProbabilityDistributionBatch getAddBatch(double weight) {
		return new ProbabilityDistributionBatch(this, weight);
	}
}
