package com.shilc.cacher.business;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.shilc.cacher.admin.CacheControllerCanBeConfigured;
import com.shilc.cacher.base.Path;
import com.shilc.cacher.base.ProbabilityDistribution;
import com.shilc.cacher.base.Resource;
import com.shilc.cacher.base.Step;
import com.shilc.cacher.base.StepLet;
import com.shilc.cacher.base.StepLib;
import com.shilc.cacher.base.StepLog;
import com.shilc.cacher.base.UserProcess;

public class DefaultBusinessCacheController implements BusinessCacheController, CacheControllerCanBeConfigured {
	
	private String BPID;
	private Decider decider;
	private StepLib stepLib;
	private ResourceDecider resourceDecider;
	private ResourcePool resourcePool;
	//private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	public DefaultBusinessCacheController(String BPID, StepLib stepLib, ResourcePool resourcePool) {
		this.BPID = BPID;
		this.decider = new Decider(stepLib);
		this.stepLib = stepLib;
		
		this.resourceDecider = new ResourceDecider();
		this.resourceDecider.setResourcePool(resourcePool);
		
		this.resourcePool = resourcePool;
		
		this.decider.initTrain();
	}

	@Override
	public String getBusinessPointID() {
		return BPID;
	}

	@Override
	public void configure(List<StepLog> logs, long now) {
		configResourceUseInLogs(logs, now);
		decider.configure(logs, now);
		decider.train();
	}

	@Override
	public void log(StepLog log) {
		List<StepLog> steps = decider.configure(log);
		decider.train();
		//获取下一步的资源概率分布
		Map<Resource<?>, List<Map.Entry<Double, ProbabilityDistribution>>> pdForResource = decider.decideNextResources(new UserProcess(steps).toPath(stepLib));
		resourceDecider.notice(log.getUserID(), log.getLogTime().getTime(), pdForResource);
	}
	
	private void configResourceUseInLogs(List<StepLog> logs, long now) {
		System.out.println("Start logging resource usage. ");
		int i = 0;
		for(StepLog log:logs) {
			
			if(i % 1000 == 0) {
				System.out.println("Logged: "+i);
			}
			i++;
			
			Step step = stepLib.getStep(log.getStepID());
			for(Resource<?> resource:step.getResources()) {
				resourcePool.logResourceUsage(resourcePool.getResourceForUser(resource, log.getUserID()), now);
			}
		}
	}

	@Override
	public <T> T getResource(long now, Class<T> resourceClass, String userID) {
		return resourcePool.get(now, resourceClass, userID);
	}
	
	/**
	 * 获取下一个步骤的准备完成时间
	 * 使用decider分析当前流程
	 * @param log
	 * @param next
	 * @return
	 */
	public long getNextStandbyTime(StepLog log, StepLog next) {
		Path path = decider.decideProcess(log).toPath(stepLib);
		return getNextStandbyTime(path, log, next);
	}
	
	/**
	 * 获取下一个步骤的准备完成时间
	 * 提供当前流程
	 * @param path
	 * @param log
	 * @param next
	 * @return
	 */
	public long getNextStandbyTime(Path path, StepLog log, StepLog next) {

		Map<Resource<?>, List<Map.Entry<Double, ProbabilityDistribution>>> pdForResource = decider.decideNextResources(path);
		List<DecideResult> results = resourceDecider.decide(log.getUserID(), log.getLogTime().getTime(), pdForResource);
		
		Map<Class<?>, DecideResult> decideIndex = new HashMap<Class<?>, DecideResult>();
		for(DecideResult result:results) {
			decideIndex.put(result.resourceType, result);
		}
		
		Step nextStep = stepLib.getStep(next.getStepID());
		Set<Resource<?>> resources = nextStep.getResources();
		
		long maxTime = 0;
		for(Resource<?> resource:resources) {
			DecideResult decideResult = decideIndex.get(resource.getType());
			
			long standByTime;
			if(decideResult == null) {
				standByTime = next.getLogTime().getTime() + (long)(resource.tGet*1000*(1.1 - Math.random()*0.2));
			}else {
				standByTime = decideResult.cacheTime;
			}
			
			//System.out.println("StandBy for "+resource.getType()+": "+sdf.format(new Date(standByTime)));
			if(standByTime > maxTime) {
				maxTime = standByTime;
			}
		}
		//System.out.println("StandBy for all: "+sdf.format(new Date(maxTime)));
		return maxTime;
		
	}

}
