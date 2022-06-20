package com.shilc.cacher.admin;

import java.util.List;

import com.shilc.cacher.base.StepLog;

public interface CacheControllerCanBeConfigured {

	/**
	 * 获取业务点ID
	 * @return
	 */
	public String getBusinessPointID();
	
	public void configure(List<StepLog> logs, long now);
	
}
