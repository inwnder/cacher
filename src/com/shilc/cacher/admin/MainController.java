package com.shilc.cacher.admin;

import java.util.List;

import com.shilc.cacher.base.StepLog;

public interface MainController {
	public void addCacheController(CacheControllerCanBeConfigured controller);
	public void configController(CacheControllerCanBeConfigured controller, List<StepLog> logs, long now);
}
