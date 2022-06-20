package com.shilc.cacher.base;

/**
 * ���ʷֲ�������
 * @author shilc
 *
 */
public class ProbabilityDistributionBuilder {
	
	/**
	 * ����������
	 * ��־��һ�����������Ӱ��ĸ��ʿ��
	 */
	public final static int NEIGHBOUR_WIDTH = 3;
	private int[] samples;
	
	//�������������*���ʿ��
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
	 * ������ʷֲ�
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
