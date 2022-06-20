package com.shilc.cacher.business;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.shilc.cacher.base.GetFromSourceMethod;
import com.shilc.cacher.base.Resource;
import com.shilc.cacher.base.ResourceForUser;

/**
 * ��Դ��
 * @author shili
 *
 */
public class ResourcePool {
	
	public static boolean USING_CACHE = true;
	
	// ȫ����Դ��ʶ
	private static final String PUBLIC_RESOURCE_ID = "PUBLIC_RESOURCE";
	
	// ��ԴƵ�ʼ��㴰��
	private static final long RESOURCE_FREQUENCY_WINDOW = 3600 * 24;
	
	//��Դ����->��Դ
	Map<Class<?>, Resource<?>> resourceMap = new HashMap<Class<?>, Resource<?>>();
	//��Դ����->�û�ID->�û���Դ
	Map<Resource<?>, Map<String, ResourceForUser<?>>> entityMap = new HashMap<Resource<?>, Map<String, ResourceForUser<?>>>();
	
	/**
	 * ��ʼ����Դ
	 * @param <T> ��Դ����
	 * @param resourceClass ��Դ����
	 * @param resourceID ��Դ����ID
	 * @param getMethod ��Դ��ȡ����
	 * @param N N>1Ϊ��Դ��������
	 * @param rStorage R<sub>storage</sub> ��Դ�洢�ɱ�
	 * @param tGet t<sub>get</sub> ��ȡ����Դ����ʱ��(s)
	 */
	public <T> void initResource(Class<T> resourceClass, 
			boolean isUserUnified, 
			GetFromSourceMethod<T> getMethod,
			double rStorage,
			int tGet
			) {
		Resource<T> resource = new Resource<T>(resourceClass, isUserUnified) {

			@Override
			public T getFromSource(String userID) {
				return getMethod.get(userID);
			}
			
		};
		resource.rStorage = rStorage;
		resource.tGet = tGet;
		
		resourceMap.put(resourceClass, resource);
		entityMap.put(resource, new HashMap<String, ResourceForUser<?>>());
	}
	
	@SuppressWarnings("unchecked")
	public <T> Resource<T> getResource(Class<T> resourceClass) {
		return (Resource<T>)resourceMap.get(resourceClass);
	}
	
	public <T> ResourceForUser<T> getResourceForUser(Resource<T> resource, String userID){
		Map<String, ResourceForUser<?>> resourcesForUser = entityMap.get(resource);
		
		if(!resource.isUserUnified()) {
			userID = PUBLIC_RESOURCE_ID;
		}
		
		if(resourcesForUser == null) {
			resourcesForUser = new HashMap<String, ResourceForUser<?>>();
			entityMap.put(resource, resourcesForUser);
			
			ResourceForUser<T> resourceForUser = new ResourceForUser<T>(resource, userID);
			resourcesForUser.put(userID, resourceForUser);
			return resourceForUser;
		} else {
			@SuppressWarnings("unchecked")
			ResourceForUser<T> resourceForUser = (ResourceForUser<T>) resourcesForUser.get(userID);
			if(resourceForUser == null) {
				resourceForUser = new ResourceForUser<T>(resource, userID);
				resourcesForUser.put(userID, resourceForUser);
			}
			return resourceForUser;
		}
	}
	
	/**
	 * ��ȡ��Դ
	 * @param <T>
	 * @param resourceClass ��Դ����
	 * @param userID �û�ID
	 * @return
	 */
	public <T> T get(long now, Class<T> resourceClass, String userID) {
		@SuppressWarnings("unchecked")
		Resource<T> resource = (Resource<T>)resourceMap.get(resourceClass);
		
		if(!resource.isUserUnified()) {
			userID = PUBLIC_RESOURCE_ID;
		}
		
		T entity;
		if(USING_CACHE) {
			entity = getFromCache(now, resource, userID);
			if(entity == null) {
				entity = resource.getFromSource(userID);
				setIntoCache(now, resource, userID, entity, true);
			}
		}else {
			entity = resource.getFromSource(userID);
		}
		
		return entity;
	}
	
	/**
	 * �������µ���Դ
	 * @param <T>
	 * @param resourceClass ��Դ����
	 * @param userID �û�ID
	 */
	public <T> void cache(long now, Class<T> resourceClass, String userID) {
		@SuppressWarnings("unchecked")
		Resource<T> resource = (Resource<T>)resourceMap.get(resourceClass);
		
		if(!resource.isUserUnified()) {
			userID = PUBLIC_RESOURCE_ID;
		}
		
		setIntoCache(now, resource, userID, resource.getFromSource(userID), false);
	}
	
	/**
	 * ��������е���Դ
	 * @param <T>
	 * @param resource
	 * @param userID
	 */
	public <T> void clear(Class<T> resourceClass, String userID) {
		@SuppressWarnings("unchecked")
		Resource<T> resource = (Resource<T>)resourceMap.get(resourceClass);
		
		if(!resource.isUserUnified()) {
			userID = PUBLIC_RESOURCE_ID;
		}
		
		Map<String, ResourceForUser<?>> resourcesForUser = entityMap.get(resource);
		ResourceForUser<?> resourceForUser = resourcesForUser.get(userID);
		if(resourceForUser != null) {
			resourceForUser.clearCached();
		}
	}
	
	/**
	 * �ӻ����л�ȡ��Դ
	 * @param <T>
	 * @param resource
	 * @param userID
	 * @return
	 */
	private <T> T getFromCache(long now, Resource<T> resource, String userID) {
		ResourceForUser<T> resourceForUser = getResourceForUser(resource, userID);
		if(resourceForUser != null) {
			logResourceUsage(resourceForUser, now);
			return resourceForUser.getCached();
		}else {
			return null;
		}
	}
	
	/**
	 * ����Դ�����ڻ�����
	 * @param <T>
	 * @param resource
	 * @param userID
	 * @param entity
	 */
	private <T> void setIntoCache(long now, Resource<T> resource, String userID, T entity, boolean forCurrentUse) {
		Map<String, ResourceForUser<?>> resourcesForUser = entityMap.putIfAbsent(resource, new HashMap<String, ResourceForUser<?>>());
		@SuppressWarnings("unchecked")
		ResourceForUser<T> resourceForUser = (ResourceForUser<T>) resourcesForUser.getOrDefault(userID, new ResourceForUser<T>(resource, userID));
		resourceForUser.setCached(entity);
		if(forCurrentUse) {
			logResourceUsage(resourceForUser, now);
		}
		resourcesForUser.putIfAbsent(userID, resourceForUser);
	}
	
	public void logResourceUsage(ResourceForUser<?> resource, long now) {
		resource.usageLog.add(now);
		//resource.usageLog.removeIf(time -> now - time>RESOURCE_FREQUENCY_WINDOW);
	}
}
