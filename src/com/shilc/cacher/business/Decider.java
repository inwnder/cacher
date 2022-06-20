package com.shilc.cacher.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.inwnder.util.tree.Tree;
import com.shilc.cacher.base.Path;
import com.shilc.cacher.base.ProbabilityDistribution;
import com.shilc.cacher.base.ProcessExtracter;
import com.shilc.cacher.base.Resource;
import com.shilc.cacher.base.Step;
import com.shilc.cacher.base.StepLet;
import com.shilc.cacher.base.StepLib;
import com.shilc.cacher.base.StepLog;
import com.shilc.cacher.base.UserProcess;

public class Decider{

	private LinkedList<UserProcess> processLib = new LinkedList<UserProcess>();
	
	private ProcessExtracter extracter = new ProcessExtracter();
	private StepLib stepLib;
	private Tree<StepLet> model;
	
	public Decider(StepLib stepLib) {
		this.stepLib = stepLib;
	}
	
	/**
	 * ����logs�������ܵ�process����processLib
	 * @param logs
	 */
	public void configure(List<StepLog> logs, long now) {
		synchronized(processLib) {
			if(logs == null) {
				return;
			}
			
			logs.sort((StepLog log1, StepLog log2)->{
				return log1.getLogTime().compareTo(log2.getLogTime());
			});
			
			for(StepLog log:logs) {
				extracter.putStepLog(log);
			}
			
			extracter.trigTime(new Date(now));
			processLib.addAll(extracter.extractProcess());
		}
	}

	/**
	 * ���õ���log�������ܵ�process����processLib
	 * @param log
	 */
	public List<StepLog> configure(StepLog log) {
		synchronized(processLib) {
			List<StepLog> steps = extracter.putStepLog(log);
			if(extracter.notEmpty()) {
				processLib.addAll(extracter.extractProcess());
			}
			
			return steps;
		}
	}
	
	/**
	 * ����ʱ��
	 * @param now
	 */
	public void trigTime(Date now) {
		extracter.trigTime(now);
		if(extracter.notEmpty()) {
			processLib.addAll(extracter.extractProcess());
		}
	}
	
	
	public void initTrain() {
		Tree.Node<StepLet> root = new Tree.Node<StepLet>(StepLet.createFor("begin", 0));
		model = Tree.newInstance(root);
	}
	
	/**
	 * ʹ�����е�processLibѵ��Ԥ��ģ��
	 * Ƶ��ģ��
	 */
	public void train() {
		System.out.println("Start training. ");
		if(model == null) {
			throw new RuntimeException("Train needs to be inited.");
		}
		
		Tree.Node<StepLet> root = model.getRoot();
		Tree.Node<StepLet> currentNode;
		
		synchronized(processLib) {
			while(!processLib.isEmpty()) {
				UserProcess process = processLib.poll();
				
				currentNode = root;
				long currentTime = -1;//��ʼ��״̬
				
				List<StepLog> steps = process.getSteps();
				for(StepLog step:steps) {
					StepLet stepLet = StepLet.createFor(stepLib.getStep(step.getStepID()));
					
					//���㵱ǰstep��֮ǰstep�ļ��ʱ��
					long newTime = step.getLogTime().getTime();
					long betweenTime;
					if(currentTime == -1) {
						betweenTime = 0;
					}else {
						betweenTime = (newTime - currentTime)/1000;
					}
					currentTime = newTime;
					
					//����step��
					Tree.Node<StepLet> child;
					if((child = currentNode.getChild(stepLet)) != null) {
						child.getValue().addF();
						child.getValue().addSample((int)betweenTime);
					}else {
						child = new Tree.Node<StepLet>(StepLet.createFor(stepLib.getStep(step.getStepID()), (int)betweenTime));
						currentNode.addChild(child);
					}
	
					currentNode = child;
				}
				
				//����End�ڵ�
				Tree.Node<StepLet> endNode;
				if((endNode = currentNode.getChild(StepLet.createFor(stepLib.getStep("end")))) != null) {
					endNode.getValue().addF();
					endNode.getValue().addSample(0);
				}else {
					endNode = new Tree.Node<StepLet>(StepLet.createFor(stepLib.getStep("end")));
					currentNode.addChild(endNode);
				}
			}
		}
	}
	
	/**
	 * ����modelͨ�����е�path�ƶ���һ��step
	 * @param current
	 * @return
	 */
	private List<StepLet> decideNext(Path current){
		Tree.Node<StepLet> currentNode = model.getRoot();
		
		for(Step step:current.getSteps()) {
			Tree.Node<StepLet> nextNode = currentNode.getChild(StepLet.createFor(step.getStepID()));
			if(nextNode == null) {
				return null;
			}
			currentNode = nextNode;
		}
		
		if(currentNode.getChildren() == null) {
			return null;
		}
		
		List<StepLet> stepLets = new ArrayList<StepLet>(currentNode.getChildren().size());
		for(Tree.Node<StepLet> child:currentNode.getChildren()) {
			stepLets.add(child.getValue());
		}
		
		return stepLets;
	}
	
	public UserProcess decideProcess(StepLog log) {
		return extracter.getProcess(log);
	}
	
	/**
	 * ��ȡ��һ��step�ĸ�����
	 * @param current
	 * @return
	 */
	private TreeMap<StepLet, Double> decideNextProb(Path current) {
		List<StepLet> nexts = decideNext(current);
		if(nexts == null) {
			return null;
		}
		int sumF = 0;
		
		TreeMap<StepLet, Double> nextProb = new TreeMap<StepLet, Double>();
		for(StepLet next:nexts) {
			sumF += next.getF();
		}
		
		for(StepLet next:nexts) {
			nextProb.put(next, ((double)next.getF())/((double)sumF));
		}
		
		return nextProb;
	}
	
	/**
	 * ��ȡ��һ��step��Դ�ĸ�����
	 * @param current
	 * @return <ÿ����Դ, ��ÿ�����ܵķ�֧�ϵĸ��ʺ�ʱ��ֲ�>
	 */
	public Map<Resource<?>, List<Map.Entry<Double, ProbabilityDistribution>>> decideNextResources(Path current){
		TreeMap<StepLet, Double> nexts = decideNextProb(current);
		
		if(nexts == null) {
			return null;
		}
		
		Map<Resource<?>, List<Map.Entry<Double, ProbabilityDistribution>>> branchesOfResources = new HashMap<Resource<?>, List<Map.Entry<Double, ProbabilityDistribution>>>();
		
		for(Entry<StepLet, Double> next:nexts.entrySet()) {
			ProbabilityDistribution stepPD = next.getKey().getProbDisb();
			for(Resource<?> resource:stepLib.getStep(next.getKey().getStepID()).getResources()) {
				List<Map.Entry<Double, ProbabilityDistribution>> branches = branchesOfResources.get(resource);
				if(branches == null) {
					branches = new ArrayList<Map.Entry<Double, ProbabilityDistribution>>();
					branchesOfResources.put(resource, branches);
				}
				branches.add(Map.entry(next.getValue(), stepPD));
			}
		}
		
		return branchesOfResources;
	}
	
}
