package com.shilc.cacher.base;

public class CostDefiner {

	/**
	 * ��Դ��ʱ�ȴ��ɱ�����
	 * @param t_wait ��Դ�ȴ�ʱ��
	 */
	public static double cost_sw(double t_wait) {
		return Math.pow(t_wait, 3);
	}
	
	/**
	 * �ж�����Ϊ�������븡�㾫�������жϣ�
	 * @param gain
	 * @return
	 */
	public static boolean hasCostGain(double gain) {
		if(gain > 1e-3) {
			return true;
		}else {
			return false;
		}
	}
	
}
