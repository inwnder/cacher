package com.inwnder.util.tree;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.inwnder.util.tree.exception.CircledTreeException;

/**
 * 循环追踪器
 * @author Inwnder
 *
 */
public class CircleTracer<T>{
	private Set<Tree.Node<T>> tracer = new LinkedHashSet<Tree.Node<T>>();
	
	/**
	 * 追踪循环，如果在整个过程中出现某个step和已出现过的step重复，则抛出整个循环
	 * @param step
	 * @throws CircledTreeException 出现的循环
	 */
	public void trace(Tree.Node<T> step) throws CircledTreeException{
		if(tracer.contains(step)){//已追踪集合中包括了该节点，说明存在循环
			//记录整个循环
			List<Tree.Node<?>> circle = new ArrayList<Tree.Node<?>>();
			boolean start = false;
			
			for(Tree.Node<T> current:tracer){
				if(!start && current == step){
					start = true;
				}
				if(start){
					circle.add(current);
				}
			}
			circle.add(step);
			throw new CircledTreeException(circle);
		}else{//已追踪集合中不包括该节点，加入追踪
			tracer.add(step);
		}
	}
}