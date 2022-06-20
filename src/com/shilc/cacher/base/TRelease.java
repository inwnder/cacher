package com.shilc.cacher.base;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class TRelease{
	
	//N_req (t)ʱIs_req=1��˥����, Ϊ0ʱ��˥����Ϊ1ʱ��ȫ˥��
	private static double getRD(int currentT, double[] ftPD) {
		double sumN = 0;//N_req (t)ʱ����Ϊ�������¼����ܶȻ���
		double sum = 0;//���ܶȻ���
		for(int i = 0; i < ftPD.length; ++i) {
			if(i < currentT) {
				sumN += ftPD[i];
			}
			sum += ftPD[i];
		}
		
		return sumN/sum;
	}
	
	private static double pGetN(int currentT, double p_get, double[] ftPD) {
		double r_d = getRD(currentT, ftPD);
		return (1 - r_d) * p_get / (1 - p_get * r_d);
	}
	
	private static double[] getFtPDN(int currentT, double p_get, double[] ftPD) {
		double r_d = getRD(currentT, ftPD);
		double[] ftPDN = new double[ftPD.length];
		for(int i = 0; i < ftPD.length; ++i) {
			if(i < currentT) {
				ftPDN[i] = 0;
			}else {
				ftPDN[i] = 1/(1 - p_get * r_d) * ftPD[i];
			}
		}
		return ftPDN;
	}
	
	/**
	 * ���������Ԥȡ����Դδ������������
	 * @param t ��ǰʱ�䣨֮ǰδ������Դ��
	 * @param tDuration ��Դ���豣�ֻ����ʱ��
	 * @param p_get
	 * @param ftPD
	 * @param cost_comm
	 * @param R_storage
	 * @param t_get
	 * @return
	 */
	private static double getEGain(int t, int tDuration, double p_get, double[] ftPD, double cost_comm, double R_storage, double t_get) {
		double[] ftPDN = getFtPDN(t, p_get, ftPD);
		double pGetN =  pGetN(t, p_get, ftPD);
		
		double sumGet = 0;
		for(int t_cache = 0; t_cache < tDuration; t_cache++) {
			sumGet += ftPDN[t_cache + t] * getGain_precache(t_cache, t_get, R_storage);
		}
		
		double sumNGet = -cost_comm - R_storage * tDuration;
		return pGetN * sumGet + (1 - pGetN) * sumNGet;
	}
	
	/**
	 * ����δʹ������µ���Դ��Ҫ���ͷŻ����ʱ�䣨���ڲ�̫���ܱ�ʹ�ã�
	 * @param cost_comm ��ȡ��Դ��ͨ�ųɱ�
	 * @param t_get ��Դ��ȡʱ��
	 * @param R_storage ��Դ��λ�洢�ɱ�
	 * @param p_get ��Դ��ȡ���ʣ�ǰһ����ʱ��
	 * @param ftPDOrigin ��Դ�ֲ���ǰһ����ʱ��
	 * @return
	 */
	public static int get(double cost_comm, double t_get, double R_storage, double p_get, ProbabilityDistribution ftPDOrigin) {
		double[] ftPD = ftPDOrigin.getNormalizdValueWith(p_get);
		
		int t = ftPD.length;
		int maxT = -1;
		
		//��max(t)��ʹʽ3.26����
		while(t > 0) {
			for(int tDuration = 0; tDuration < ftPD.length - t; ++tDuration) {
				
				double e_gain = getEGain(t, tDuration, p_get, ftPD, cost_comm, R_storage, t_get);

				if(CostDefiner.hasCostGain(e_gain)) {//����e_gain==0ʱ���������Ȳ�������
					//System.out.println("Get t_release="+t+" when e_gain="+e_gain);
					maxT = t;
					break;
				}
			}
			
			if(maxT >= 0) {
				break;
			}
			
			--t;
		}
		
		return maxT;
	}
	
	private static double getGain_precache(double t_cache, double t_get, double R_storage) {
		
		if(t_cache >= t_get) {
			return CostDefiner.cost_sw(t_get) - R_storage * (t_cache - t_get);
		}else if(t_cache > 0){
			return CostDefiner.cost_sw(t_get) - CostDefiner.cost_sw(t_get - t_cache);
		}else {
			return 0;
		}
	}
	
	public static void main(String[] args) {
		double rStorage = 0.1;
		double tGet = 2;
		double cost_comm = 0.1;
		double p_get = 0.5;
		//int t = 5;
		
		double ft[] = {1, 2, 7, 20, 46, 103, 212, 406, 765, 1249, 1892, 2656, 3403, 4254, 4936, 5445, 5727, 5714, 5475, 5097, 4442, 3784, 3045, 2344, 1764, 1251, 848, 552, 339, 215, 145, 82, 44, 21, 12, 6, 0, 0, 0, 0, 0, 0};
		ProbabilityDistribution pd = new ProbabilityDistribution(ft);
		double[] ftPD = pd.getNormalizdValueWith(p_get);
		
		try {
			FileWriter writer = new FileWriter(new File("C:/data/exp/e_gain.csv"));
			
			int t = 0;
			while(t < ftPD.length) {
				writer.append(String.valueOf(t));
				for(int t_release = 0; t_release < ftPD.length; ++t_release) {
					int tDuration = t_release - t;
					double e_gain;
					if(tDuration < 0) {
						e_gain = 0;
						writer.append(',').append("NULL");
					}else {
						e_gain = getEGain(t, tDuration, p_get, ftPD, cost_comm, rStorage, tGet);
						writer.append(',').append(String.valueOf(e_gain));
					}

				}
				writer.append('\n');
				
				++t;
			}
			
			
			/*
			for(int t = 0;  t < ftPD.length; ++t) {
				double[] newft = getFtPDN(t, p_get, ftPD);
				boolean first = true;
				for(double unit:newft) {
					if(first) {
						first = false;
						writer.append(String.valueOf(t)).append(',');
					}else {
						writer.append(',');
					}
					writer.append(String.valueOf(unit));
				}
				writer.append('\n');
			}
			*/
		
//		for(int tDuration = 0; tDuration < ftPD.length - t; ++tDuration) {
//			
//			double e_gain = getEGain(t, tDuration, p_get, ftPD, cost_comm, rStorage, tGet);
//			System.out.println("Get E_gain="+e_gain+" while delay="+t+",p="+p_get+" and tDuration="+tDuration);
//
//		}
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}