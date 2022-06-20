package com.shilc.cacher.base;

/**
 * ���������ں�
 * @author shilc
 *
 */
public class ProbabilityDistributionBatch {

	private double weight = 0;
	private double[] target;
	
	/**
	 * ����Ȩ�س�ʼ�������ں�
	 * @param init ��ʼ������
	 * @param weight Ȩ��
	 */
	public ProbabilityDistributionBatch(ProbabilityDistribution init, double weight) {
		target = init.getValue().clone();
		for(int i = 0; i < target.length; ++i) {
			target[i] = target[i]*weight;
		}
		this.weight += weight;
	}
	
	/**
	 * ��ʼ�������ں�
	 * @param init ��ʼ�����ʣ�����Ȩ��Ϊ1
	 */
	public ProbabilityDistributionBatch(ProbabilityDistribution init) {
		target = init.getValue().clone();
		this.weight += 1;
	}
	
	/**
	 * �ںϸ���
	 * @param addee ���ںϸ���
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
	 * ����ںϺ����
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
