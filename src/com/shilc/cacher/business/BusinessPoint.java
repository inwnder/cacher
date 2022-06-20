package com.shilc.cacher.business;

import com.shilc.cacher.admin.CacheControllerCanBeConfigured;

/**
 * ҵ���
 */
public class BusinessPoint {

	private String BPID;
	private DefaultBusinessCacheController cacheController;
	
	/**
	 * @param BPID ҵ���ID
	 */
	public BusinessPoint(String BPID) {
		this.BPID = BPID;
	}
	
	/**
	 * ��ʼ��ҵ���
	 */
	public void init() {
		//TODO ��ʼ��ҵ���
	}
	
	/**
	 * ��ȡҵ���ID
	 * @return BPID
	 */
	public String getBPID() {
		return BPID;
	}
	
	/**
	 * ��ȡҵ��㻺�������
	 * @return
	 */
	public BusinessCacheController getBusinessCacheController() {
		return cacheController;
	}
	
	/**
	 * ��ȡ������ҵ���
	 * @return
	 */
	public CacheControllerCanBeConfigured getCacheController() {
		return cacheController;
	}
	
}
