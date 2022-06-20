package com.shilc.cacher.base;

/**
 * ���ʷֲ�
 * @author shilc
 *
 */
public class ProbabilityDistribution {
	//�ֲ�
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
	 * ����ֲ�����Ϊp���滯�ķֲ�
	 * @param p �ֲ�����ֵ [0,1]
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
	 * ������滯�ķֲ����ֲ�����Ϊ1��
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
	 * ���µĸ��ʷֲ��ں�
	 * @param another �µĸ��ʷֲ�(ά����Ҫһ��)
	 * @param weight ���� �¸���/���и��� 
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
	 * ��������ں�����
	 * @return
	 */
	public ProbabilityDistributionBatch getAddBatch(double weight) {
		return new ProbabilityDistributionBatch(this, weight);
	}
}
