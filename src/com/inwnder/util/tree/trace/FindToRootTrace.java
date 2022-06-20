package com.inwnder.util.tree.trace;

import java.util.Iterator;

import com.inwnder.util.tree.CircleTracer;
import com.inwnder.util.tree.Tree;
import com.inwnder.util.tree.Tree.Node;

/**
 * A trace from a Node to the root
 * @author Inwnder
 *
 * @param <E>
 */
public class FindToRootTrace<E> implements Trace<E>{

	private Node<E> start;
	
	public FindToRootTrace(Node<E> start){
		this.start = start;
	}
	
	@Override
	public FindToRootIterator<E> iterator() {
		return new FindToRootIterator<E>(start);
	}
	
	/**
	 * An iterator of the trace from a Node to the root
	 * @author Inwnder
	 *
	 * @param <E>
	 */
	public class FindToRootIterator<ELE> implements Iterator<Tree.Node<ELE>>{
		private Node<ELE> current;
		private CircleTracer<ELE> tracer = new CircleTracer<ELE>();
		
		FindToRootIterator(Node<ELE> start){
			current = start;
		}
		
		@Override
		public boolean hasNext() {
			return current != null;
		}

		@Override
		public Node<ELE> next() {
			if(current == null){
				return null;
			}else{
				Node<ELE> tmp = current;
				tracer.trace(current);
				current = current.getParent();
				return tmp;
			}
		}

		@Override
		public void remove() {
			throw new RuntimeException("The remove method of TreeIterator is not supported.");
		}
		
	}
}
