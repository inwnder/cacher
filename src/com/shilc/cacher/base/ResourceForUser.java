package com.shilc.cacher.base;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ��Դʵ��
 * ��ʹ������ص�ʵ����Դ
 * @author shili
 *
 * @param <T>
 */
public class ResourceForUser<T> {
	private Resource<T> resource;
	private String userID;
	public Queue<Long> usageLog = new LinkedBlockingQueue<Long>(); 
	private T cached;//������Դ
	
	public ResourceForUser(Resource<T> resource, String userID){
		this.resource = resource;
		this.userID = userID;
	}
	
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public T getCached() {
		return cached;
	}
	public void setCached(T cached) {
		this.cached = cached;
	}
	
	public void clearCached() {
		this.cached = null;
	}
	
	public Resource<T> getResource(){
		return resource;
	}
	
	public boolean isFreqUseResource() {
		// let F = usage.size/3600
		return usageLog.size()/3600 > getResource().getCRes();
	}
}
