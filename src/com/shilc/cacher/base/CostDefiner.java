package com.shilc.cacher.base;

public class CostDefiner {

	/**
	 * 资源短时等待成本函数
	 * @param t_wait 资源等待时间
	 */
	public static double cost_sw(double t_wait) {
		return Math.pow(t_wait, 3);
	}
	
	/**
	 * 判断收益为正（加入浮点精度问题判断）
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
