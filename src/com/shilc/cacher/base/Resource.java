package com.shilc.cacher.base;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 资源类
 * @author shili
 *
 * @param <T>
 */
public abstract class Resource<T> {

	private Class<T> resourceType;
	private boolean isUserUnified;
	
	/**
	 * R<sub>storage</sub>
	 */
	public double rStorage;
	
	/**
	 * t<sub>get</sub>
	 * 获取该资源所需的时间
	 */
	public int tGet;
	
	/**
	 * Cost<sub>comm</sub>
	 * 资源通信成本
	 */
	public int cost_comm;
	
	/**
	 * 使用缓存总\delta Cost的期望
	 * @param t_cache 缓存持续时间(s)=资源使用时刻-缓存开始时刻
	 * @param p_get 资源获取概率
	 * @param ftPD 资源获取时间分布
	 * @param t_get 资源获取时间(s)
	 * @param R_storage 单位时间资源存储成本(/s)
	 * @param cost_comm 资源请求通信成本
	 * @param t_release 资源最长缓存时间
	 * @return
	 */
	private static double getGain_p_precache(double t_cache, double p_get, ProbabilityDistribution ftPD, double t_get, double R_storage, double cost_comm, int t_release) {	
		if(t_cache >= t_get) {
			return CostDefiner.cost_sw(t_get) - R_storage * (p_get * (t_cache - t_get) + (1 - p_get) * t_release);
		}else if(t_cache > 0){
			return CostDefiner.cost_sw(t_get) - (p_get * CostDefiner.cost_sw(t_get - t_cache) + (1 - p_get) * R_storage * t_release);
		}else {
			return 0;
		}
	}
	
	/**
	 * 使用缓存总\delta Cost的期望
	 * @param t_cache 缓存持续时间(s)=资源使用时刻-缓存开始时刻
	 * @param p_get 资源获取概率
	 * @param ftPD 资源获取时间分布
	 * @param t_release 资源最长缓存时间
	 * @return
	 */
	public double getGain_p_precache(double t_cache, double p_get, ProbabilityDistribution ftPD, int t_release) {
		return getGain_p_precache(t_cache, p_get, ftPD, tGet, rStorage, cost_comm, t_release);
	}
	
	private static int getT_release(double cost_comm, double t_get, double R_storage, double p_get, ProbabilityDistribution ftPD) {
		return TRelease.get(cost_comm, t_get, R_storage, p_get, ftPD);
	}
	
	/**
	 * 获取资源最长缓存时间（持有缓存超过该时间则应该释放）
	 * @param p_get 资源获取概率
	 * @param ftPD 资源获取时间分布
	 * @return 资源最长缓存时间
	 */
	public int getT_release(double p_get, ProbabilityDistribution ftPD) {
		return getT_release(cost_comm, tGet, rStorage, p_get, ftPD);
	}
	
	private static double getE_p_predict(int t_delay, double p_get, ProbabilityDistribution ftPD, double t_get, double R_storage, double cost_comm, int t_release) {
		
		double sum = 0;
		
		double ft[] = ftPD.getNormalizdValue();
		for(int t_cache = 0; t_cache < ft.length - t_delay; t_cache++) {
			sum += ft[t_cache + t_delay] * getGain_p_precache(t_cache, p_get, ftPD, t_get, R_storage, cost_comm, t_release);
		}
		
		return sum;
	}
	
	/**
	 * 单次使用资源使用预取方案的增益期望
	 * @param t_delay 
	 * @param p_get
	 * @param ftPD
	 * @param t_release
	 * @return
	 */
	public double getE_p_predict(int t_delay, double p_get, ProbabilityDistribution ftPD, int t_release) {
		return getE_p_predict(t_delay, p_get, ftPD, tGet, rStorage, cost_comm, t_release);
	}
	
	/**
	 * 预测资源策略
	 * @param cost_comm
	 * @param t_get
	 * @param R_storage
	 * @param p_get
	 * @param ftPD
	 * @return (预取时间，释放时间)
	 */
	private static Map.Entry<Integer, Integer> getPredict(double cost_comm, double t_get, double R_storage, double p_get, ProbabilityDistribution ftPD){
		
		int t_release = getT_release(cost_comm, t_get, R_storage, p_get, ftPD);
		if(t_release < 0) {
			t_release = 0;
		}
		
		int tMax = -1;
		double max = -1;
		for(int t_delay = 0; t_delay < t_release; t_delay++) {
			double current = getE_p_predict(t_delay, p_get, ftPD, t_get, R_storage, cost_comm, t_release);
			//System.out.println("Get E_p="+current+" while delay="+t_delay+",p="+p_get+" and t_release="+t_release);
			if(CostDefiner.hasCostGain(current) && current > max) {
				tMax = t_delay;
				max = current;
			}
		}
		
		if(tMax >= 0) {
			return new AbstractMap.SimpleImmutableEntry<Integer, Integer>(tMax, t_release);
		}else {
			return null;
		}
	}
	
	/**
	 * 预测资源策略
	 * @param p_get
	 * @param ftPD
	 * @return (预取时间，释放时间)
	 */
	public Map.Entry<Integer, Integer> getPredict(double p_get, ProbabilityDistribution ftPD){
		return getPredict(cost_comm, tGet, rStorage, p_get, ftPD);
	}
	
	
	
	public static void main(String[] args) {
		double rStorage = 0.1;
		double tGet = 0.1;
		double cost_comm = 1;
		
		try {
			FileWriter writer = new FileWriter(new File("C:/data/exp/E_p_predict.csv"));
			double ft[] = {1, 2, 7, 20, 46, 103, 212, 406, 765, 1249, 1892, 2656, 3403, 4254, 4936, 5445, 5727, 5714, 5475, 5097, 4442, 3784, 3045, 2344, 1764, 1251, 848, 552, 339, 215, 145, 82, 44, 21, 12, 6, 0, 0, 0, 0, 0, 0};
			ProbabilityDistribution pd = new ProbabilityDistribution(ft);
			
			int t_release2 = getT_release(cost_comm, tGet, rStorage, 0.2, pd);
			int t_release4 = getT_release(cost_comm, tGet, rStorage, 0.4, pd);
			int t_release6 = getT_release(cost_comm, tGet, rStorage, 0.6, pd);
			int t_release8 = getT_release(cost_comm, tGet, rStorage, 0.8, pd);
			int t_release10 = getT_release(cost_comm, tGet, rStorage, 1, pd);
			
			for(int i = 0; i < 40; i+=1) {
				writer.append(String.valueOf(getE_p_predict(i, 0.2, pd, tGet, rStorage, cost_comm, t_release2))).append(',');
				writer.append(String.valueOf(getE_p_predict(i, 0.4, pd, tGet, rStorage, cost_comm, t_release4))).append(',');
				writer.append(String.valueOf(getE_p_predict(i, 0.6, pd, tGet, rStorage, cost_comm, t_release6))).append(',');
				writer.append(String.valueOf(getE_p_predict(i, 0.8, pd, tGet, rStorage, cost_comm, t_release8))).append(',');
				writer.append(String.valueOf(getE_p_predict(i, 1, pd, tGet, rStorage, cost_comm, t_release10)));
				writer.append('\n');
			}
			
			
			/*
			for(int i = 0; i < 30; i+=1) {
				
				writer.append(String.valueOf(getPredict(cost_comm, tGet, rStorage, pd))).append(',');
			}*/
			
//			System.out.println(String.valueOf(getPredict(cost_comm, tGet, rStorage, 0.1, pd)));
//			System.out.println(String.valueOf(getPredict(cost_comm, tGet, rStorage, 0.3, pd)));
//			System.out.println(String.valueOf(getPredict(cost_comm, tGet, rStorage, 0.5, pd)));
//			System.out.println(String.valueOf(getPredict(cost_comm, tGet, rStorage, 0.7, pd)));
//			System.out.println(String.valueOf(getPredict(cost_comm, tGet, rStorage, 0.5, pd)));
			
			
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * 预测资源在多叉树情况下预取时间
	 * @param branches
	 * @return
	 */
	public Map.Entry<Integer, Integer> predictPrecacheTimeOnMultitree(List<Map.Entry<Double, ProbabilityDistribution>> branches) {
		
		double p_get = 0;//资源获取的综合概率
		ProbabilityDistributionBatch ftPDBatch = null;
		
		for(Map.Entry<Double, ProbabilityDistribution> branch:branches) {
			p_get += branch.getKey();
			
			if(ftPDBatch == null) {
				ftPDBatch = branch.getValue().getAddBatch(branch.getKey());
			}else {
				ftPDBatch.add(branch.getValue(), branch.getKey());
			}
		}
		
		//资源获取的综合时间分布
		ProbabilityDistribution ftPD = ftPDBatch.build();
		
		return getPredict(p_get, ftPD);
		
	}
	
	public Resource(Class<T> type, boolean isUserUnified){
		resourceType = type;
		this.isUserUnified = isUserUnified;
	}
	
	public abstract T getFromSource(String userID);
	
	public Class<T> getType(){
		return resourceType;
	}
	
	public boolean isUserUnified() {
		return isUserUnified;
	}
	
	/**
	 * 资源获取成本
	 * @param tGet
	 * @return
	 */
	public double getCostGet(int tGet) {
		return CostDefiner.cost_sw(tGet);
	}
	
	/**
	 * 资源缓存常数
	 * @return C<sub>res</sub>
	 */
	public double getCRes() {
		return rStorage/getCostGet(tGet);
	}
	
	/**
	 * 缓存等待极限常量
	 * @return t<sub>get</sub><sup>N</sup>/R<sub>storage</sub>
	 */
	public double getTWaitmax() {
		return CostDefiner.cost_sw((double)tGet)/rStorage;
	}
	
	public String toString() {
		return resourceType.getName();
	}
}
