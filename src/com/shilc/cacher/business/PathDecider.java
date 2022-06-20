package com.shilc.cacher.business;

import com.shilc.cacher.base.Path;
import com.shilc.cacher.base.StepLog;

public interface PathDecider {
	public Path decidePath(StepLog log);
}
