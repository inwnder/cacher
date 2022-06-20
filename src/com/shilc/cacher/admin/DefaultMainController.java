package com.shilc.cacher.admin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import com.shilc.cacher.base.Path;
import com.shilc.cacher.base.Resource;
import com.shilc.cacher.base.Step;
import com.shilc.cacher.base.StepLib;
import com.shilc.cacher.base.StepLog;
import com.shilc.cacher.base.StepLogLoader;
import com.shilc.cacher.base.Timer;
import com.shilc.cacher.business.Decider;
import com.shilc.cacher.business.DefaultBusinessCacheController;
import com.shilc.cacher.business.ResourceDecider;
import com.shilc.cacher.business.ResourcePool;
import com.shilc.cacher.util.Logger;
import com.shilc.test.Account;
import com.shilc.test.Balance;
import com.shilc.test.CertainLargeResource;
import com.shilc.test.FPComment1;
import com.shilc.test.FPComment2;
import com.shilc.test.FPPrice;
import com.shilc.test.TransferLog1;
import com.shilc.test.TransferLog2;
import com.shilc.test.TransferLog3;
import com.shilc.test.TransferLog4;

public class DefaultMainController implements MainController{

	private Map<String, CacheControllerCanBeConfigured> controllers = new HashMap<String, CacheControllerCanBeConfigured>();
	
	@Override
	public void addCacheController(CacheControllerCanBeConfigured controller) {
		controllers.put(controller.getBusinessPointID(), controller);
	}

	@Override
	public void configController(CacheControllerCanBeConfigured controller, List<StepLog> logs, long now) {
		controllers.get(controller.getBusinessPointID()).configure(logs, now);
	}
	
	public final static void main(String[] args) {
		
		/** 时钟 **/
		java.util.Timer timer = new java.util.Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				long now = System.currentTimeMillis();
				
				//System.out.println("Timer trigged: "+now);
				Timer.trig(now);
			}
			
		}, 0, 500);
		
		/** 配件 **/
		
		//资源池
		ResourcePool resourcePool = new ResourcePool();
		// 添加先验资源信息
		resourcePool.initResource(Account.class, true, (userID)->{
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			return new Account();
			}, 0.01, 1);
		resourcePool.initResource(Balance.class, true, (userID)->{
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			return new Balance();
			}, 0.1, 2);
		resourcePool.initResource(CertainLargeResource.class, false, (userID)->{
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			return new CertainLargeResource();
			}, 1, 0);
		resourcePool.initResource(FPPrice.class, false, (userID)->{
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			return new FPPrice();
			}, 1, 5);
		resourcePool.initResource(FPComment1.class, false, (userID)->{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			return new FPComment1();
			}, 1, 0);
		resourcePool.initResource(FPComment2.class, false, (userID)->{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			return new FPComment2();
			}, 1, 0);
		resourcePool.initResource(TransferLog1.class, true, (userID)->{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			return new TransferLog1();
			}, 1, 5);
		resourcePool.initResource(TransferLog2.class, true, (userID)->{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			return new TransferLog2();
			}, 1, 5);
		resourcePool.initResource(TransferLog3.class, true, (userID)->{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			return new TransferLog3();
			}, 1, 5);
		resourcePool.initResource(TransferLog4.class, true, (userID)->{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			return new TransferLog4();
			}, 1, 5);
		
		//步骤仓库
		StepLib stepLib = new StepLib(new ArrayList<Step>() {

			private static final long serialVersionUID = 1L;
			
			{
				//添加先验步骤信息
				add(new Step("login", new HashSet<Resource<?>>() {

					private static final long serialVersionUID = 7461122198054528953L;
					
					{
						add(resourcePool.getResource(Account.class));
					}
					
				}));
				
				add(new Step("pay", new HashSet<Resource<?>>() {

					private static final long serialVersionUID = 7461122198054528954L;
					
					{
						add(resourcePool.getResource(Account.class));
						add(resourcePool.getResource(Balance.class));
						add(resourcePool.getResource(CertainLargeResource.class));
					}
					
				}));
				
				add(new Step("query", new HashSet<Resource<?>>() {

					private static final long serialVersionUID = 7461122198054528955L;
					
					{
						add(resourcePool.getResource(Account.class));
						add(resourcePool.getResource(Balance.class));
					}
					
				}));
				
				add(new Step("buyFP", new HashSet<Resource<?>>() {

					private static final long serialVersionUID = 7461122198054528956L;
					
					{
						add(resourcePool.getResource(Account.class));
						add(resourcePool.getResource(Balance.class));
						add(resourcePool.getResource(FPPrice.class));
					}
					
				}));
				
				add(new Step("transfer", new HashSet<Resource<?>>() {

					private static final long serialVersionUID = 7461122198054528957L;
					
					{
						add(resourcePool.getResource(Account.class));
						add(resourcePool.getResource(Balance.class));
					}
					
				}));
				
				add(new Step("read_comment", new HashSet<Resource<?>>() {

					private static final long serialVersionUID = 7461122198054528957L;
					
					{
						add(resourcePool.getResource(FPComment1.class));
					}
					
				}));
				
				add(new Step("read_comment1", new HashSet<Resource<?>>() {

					private static final long serialVersionUID = 7461122198054528957L;
					
					{
						add(resourcePool.getResource(FPComment2.class));
					}
					
				}));
				
				add(new Step("queryTransfer", new HashSet<Resource<?>>() {

					private static final long serialVersionUID = 7461122198054528957L;
					
					{
						add(resourcePool.getResource(Account.class));
						add(resourcePool.getResource(TransferLog1.class));
					}
					
				}));
				
				add(new Step("queryTransfer2", new HashSet<Resource<?>>() {

					private static final long serialVersionUID = 7461122198054528957L;
					
					{
						add(resourcePool.getResource(Account.class));
						add(resourcePool.getResource(TransferLog2.class));
					}
					
				}));
				
				add(new Step("queryTransfer3", new HashSet<Resource<?>>() {

					private static final long serialVersionUID = 7461122198054528957L;
					
					{
						add(resourcePool.getResource(Account.class));
						add(resourcePool.getResource(TransferLog3.class));
					}
					
				}));
				
				add(new Step("queryTransfer4", new HashSet<Resource<?>>() {

					private static final long serialVersionUID = 7461122198054528957L;
					
					{
						add(resourcePool.getResource(Account.class));
						add(resourcePool.getResource(TransferLog4.class));
					}
					
				}));
				
				add(new Step("end", new HashSet<Resource<?>>() {

					private static final long serialVersionUID = 7461122198054528957L;
					
				}));
			}
			
		});
		
		// 步骤历史
		StepLogLoader logLoader = new StepLogLoader(stepLib);
		//List<StepLog> logs = logLoader.loadFromCSV(new File("C:\\data\\train_new.csv"));
		List<StepLog> logs = new ArrayList<StepLog>();
		
		/** 配件结束 **/
		
		//主控制器
		MainController mainController = new DefaultMainController();
		
		//业务控制器
		DefaultBusinessCacheController businessController001 = new DefaultBusinessCacheController("BC01", stepLib, resourcePool);
		
		mainController.addCacheController(businessController001);
		mainController.configController(businessController001, logs, System.currentTimeMillis());//添加日志
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		/**
		 * 业务逻辑开始
		 */
		
		List<StepLog> testlogs = logLoader.loadFromCSV(new File("C:\\data\\test_common.csv"));
		
		try {
			FileWriter writer = new FileWriter(new File("C:\\data\\outWithCustomCache.csv"));
		
			StringBuffer builder = new StringBuffer();
			
			LinkedList<StepLog> logList = new LinkedList<StepLog>();
			String currentUser = null;
			for(StepLog log:testlogs) {
				if(log != null) {
					if(!log.getUserID().equals(currentUser)) {
						currentUser = log.getUserID();
						
						logList = new LinkedList<StepLog>();
						logList.add(log);
					} else {
						List<Step> steps = new ArrayList<Step>(logList.size()); 
						for(StepLog stepLog:logList) {
							steps.add(stepLib.getStep(stepLog.getStepID()));
						}
						Path path = new Path(steps);
						
						builder.append(currentUser).append(',') //userID
							.append(path).append(',')    //this step ID
							.append(logList.getLast().getLogTime().getTime()).append(',')   //this step time
							.append(log.getStepID()).append(',')    //next step ID
							.append(log.getLogTime().getTime()).append(',');  //next step time
							
						long standByTime = businessController001.getNextStandbyTime(path, logList.getLast(), log);
						builder.append(standByTime).append(',');        //precache standby time
						long gaintime = log.getLogTime().getTime() - standByTime;
						gaintime = gaintime < 0?gaintime:0;
						builder.append(gaintime).append("\n"); //gain time
						
						logList.add(log);
						writer.append(builder.toString());
						builder.setLength(0);
						writer.flush();
					}
					
				}
			}
		
			writer.append(builder.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		
		
		/*
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		String userID = "user";
		long startTime;
		try {
			startTime = sdf.parse("2022-06-09 00:00:00.000").getTime();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		System.out.println("Start: "+sdf.format(new Date(startTime)));
		long endTime = startTime + 1000*10;
		System.out.println("End: "+sdf.format(new Date(endTime)));

		Path path = new Path(new ArrayList<Step>() {

			private static final long serialVersionUID = -7623564023414928132L;
			
			{
				add(stepLib.getStep("login"));
				add(stepLib.getStep("queryTransfer"));
				add(stepLib.getStep("queryTransfer2"));
				add(stepLib.getStep("queryTransfer3"));
			}
			
		});
		StepLog thisLog = new StepLog("login", userID, startTime);
		StepLog nextLog = new StepLog("pay", userID, endTime);
		//businessController001.log(loginLog);
		long standByTime = businessController001.getNextStandbyTime(path, thisLog, nextLog);
		System.out.println("StandBy: "+sdf.format(new Date(standByTime)));
		System.out.println("Precached: "+(endTime - standByTime));
		*/
		
		/*
		String userID = "user";
		
		//登录流程
		Logger.logStart("login");
		businessController001.log(new StepLog("login", userID, System.currentTimeMillis()));
		businessController001.getResource(System.currentTimeMillis(), Account.class, userID);
		Logger.logEnd("login");
		
		try {
			Thread.sleep(10000);//人工操作
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//支付流程
		Logger.logStart("pay");
		businessController001.log(new StepLog("pay", userID, System.currentTimeMillis()));
		businessController001.getResource(System.currentTimeMillis(), Account.class, userID);
		businessController001.getResource(System.currentTimeMillis(), Balance.class, userID);
		businessController001.getResource(System.currentTimeMillis(), CertainLargeResource.class, userID);
		Logger.logEnd("pay");
		
		
		
		try {
			Thread.sleep(10000);//人工操作
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		userID = "user2";
		
		//登录流程
		Logger.logStart("login");
		businessController001.log(new StepLog("login", userID, System.currentTimeMillis()));
		businessController001.getResource(System.currentTimeMillis(), Account.class, userID);
		Logger.logEnd("login");
		
		try {
			Thread.sleep(10000);//人工操作
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//支付流程
		Logger.logStart("pay");
		businessController001.log(new StepLog("pay", userID, System.currentTimeMillis()));
		businessController001.getResource(System.currentTimeMillis(), Account.class, userID);
		businessController001.getResource(System.currentTimeMillis(), Balance.class, userID);
		businessController001.getResource(System.currentTimeMillis(), CertainLargeResource.class, userID);
		Logger.logEnd("pay");*/
		
		
		timer.cancel();
	}

}
