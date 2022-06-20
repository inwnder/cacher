package com.shilc.cacher.business;

import com.shilc.cacher.base.StepLog;

public interface BusinessCacheController {
	public void log(StepLog log);
	public <T> T getResource(long now, Class<T> resourceClass, String userID);
}
