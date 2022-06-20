package com.shilc.cacher.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.shilc.cacher.base.ProbabilityDistribution;
import com.shilc.cacher.base.Resource;
import com.shilc.cacher.base.ResourceForUser;
import com.shilc.cacher.base.Timer;

public class ResourceDecider {
	
	private ResourcePool resourcePool;
	private boolean isLog = true;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	public List<DecideResult> decide(String userID, long now, Map<Resource<?>, List<Map.Entry<Double, ProbabilityDistribution>>> pdForResource) {
		List<DecideResult> results = new ArrayList<DecideResult>();
		
		if(pdForResource == null) {
			return results;
		}
		
		for(Entry<Resource<?>, List<Map.Entry<Double, ProbabilityDistribution>>> pd:pdForResource.entrySet()) {
			
			ResourceForUser<?> resourceForUser = resourcePool.getResourceForUser(pd.getKey(), userID);
			DecideResult result = new DecideResult();
			result.resourceType = resourceForUser.getResource().getType();
			result.userID = userID;
			
			StringBuilder logBuilder = new StringBuilder();
			logBuilder.append(resourceForUser.getResource().getType())
				.append(" for ").append(resourceForUser.getUserID()).append(": ");
			
			if(resourceForUser != null) {
				
				if(resourceForUser.isFreqUseResource()) {
					// 频繁重用资源
					logBuilder.append("freq used, cached. ");	
					
					result.predictTime = 0;
					result.cacheTime = now;
				}else {
					// 非频繁重用资源
					logBuilder.append("not freq used. ");	
					
					Map.Entry<Integer, Integer> predictResult = resourceForUser.getResource().predictPrecacheTimeOnMultitree(pd.getValue());
					if(predictResult != null) {
						result.predictTime = predictResult.getKey();
						result.cacheTime = now + predictResult.getKey()*1000;
						result.releaseTime = predictResult.getValue();
						result.releaseTimeStamp = result.cacheTime + predictResult.getValue()*1000;
						logBuilder.append("Need to precache at ")
							.append(sdf.format(new Date(result.cacheTime)))
							.append(" and release at ")
							.append(sdf.format(new Date(result.releaseTimeStamp)))
							.append('.');
					}else {
						logBuilder.append("Don't need to precache.");
						log(logBuilder.toString());
						continue;
					}
				}
				
			}
			
			log(logBuilder.toString());
			results.add(result);
			
		}
		
		return results;
	}
	
	
	/**
	 * 接收资源概率分布通知，判断预缓存资源
	 * @param userID
	 * @param pdForResource
	 */
	public void notice(String userID, long now, Map<Resource<?>, List<Map.Entry<Double, ProbabilityDistribution>>> pdForResource) {
		
		if(pdForResource == null) {
			return;
		}
		
		for(Entry<Resource<?>, List<Map.Entry<Double, ProbabilityDistribution>>> pd:pdForResource.entrySet()) {
			
			ResourceForUser<?> resourceForUser = resourcePool.getResourceForUser(pd.getKey(), userID);
			
			if(resourceForUser != null) {
				if(resourceForUser.isFreqUseResource()) {
					// 频繁重用资源
					System.out.println(resourceForUser.getResource()+" is freq used. Caching. ");
					resourcePool.cache(now, resourceForUser.getResource().getType(), userID);
				}else {
					// 非频繁重用资源
					System.out.println(resourceForUser.getResource()+" is not freq used. ");
					
					Map.Entry<Integer, Integer> predictResult = resourceForUser.getResource().predictPrecacheTimeOnMultitree(pd.getValue());
					if(predictResult != null) {
						long cacheTime = now + predictResult.getKey()*1000;
						System.out.println("Precache "+resourceForUser.getResource().getType()+" of "+userID+" on "+cacheTime);
						Timer.setEvent(cacheTime, ()->{
							resourcePool.cache(cacheTime, resourceForUser.getResource().getType(), userID);
						});
						long releaseTime = cacheTime + predictResult.getValue()*1000;
						System.out.println("Release "+resourceForUser.getResource().getType()+" of "+userID+" on "+releaseTime);
						Timer.setEvent(releaseTime, ()->{
							resourcePool.clear(resourceForUser.getResource().getType(), userID);
						});
					}
				}
			}
			
		}
		
	}
	
	public void setResourcePool(ResourcePool resourcePool) {
		this.resourcePool = resourcePool;
	}
	
	public ResourcePool getResourcePool() {
		return this.resourcePool;
	}
	
	private void log(String str) {
		if(isLog) {
			System.out.println(str);
		}
	}
	
}
