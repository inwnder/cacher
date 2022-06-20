package com.shilc.cacher.business;

import com.shilc.cacher.admin.CacheControllerCanBeConfigured;

/**
 * 业务点
 */
public class BusinessPoint {

	private String BPID;
	private DefaultBusinessCacheController cacheController;
	
	/**
	 * @param BPID 业务点ID
	 */
	public BusinessPoint(String BPID) {
		this.BPID = BPID;
	}
	
	/**
	 * 初始化业务点
	 */
	public void init() {
		//TODO 初始化业务点
	}
	
	/**
	 * 获取业务点ID
	 * @return BPID
	 */
	public String getBPID() {
		return BPID;
	}
	
	/**
	 * 获取业务点缓存管理器
	 * @return
	 */
	public BusinessCacheController getBusinessCacheController() {
		return cacheController;
	}
	
	/**
	 * 获取可配置业务点
	 * @return
	 */
	public CacheControllerCanBeConfigured getCacheController() {
		return cacheController;
	}
	
}
