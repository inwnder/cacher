package com.inwnder.util.tree;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import com.inwnder.util.tree.Tree.Node;

/**
 * Deep First Iterator
 * @author Inwnder
 *
 * @param <E> Type of the Node value
 */
public class DeepFirstIterator<E> implements Iterator<Tree.Node<E>>{

	Tree<E> tree;
	Stack<Iterator<Node<E>>> itorStack = new Stack<Iterator<Node<E>>>();
	int level;
	Tree.Node<E> current = null, next = null;
	
	DeepFirstIterator(Tree<E> tree){
		this.tree = tree;
		this.next = tree.getRoot();
	}
	
	@Override
	public boolean hasNext() {
		return next != null;
	}
	
	/**
	 * 获取当前迭代器在树中的层级
	 * @return 层级
	 */
	public int getLevel(){
		return level;
	}

	@Override
	public Node<E> next() {
		current = next;
		level = itorStack.size();
		next = getNext();
		return current;
	}

	@Override
	public void remove() {
		throw new RuntimeException("The remove method of TreeIterator is not supported.");
	}
	
	/**
	 * 移动到当前节点的下一个节点
	 * @return
	 */
	private Node<E> getNext(){
		return getNext(current, itorStack);
	}
	
	/**
	 * 移动到目标节点的下一个节点
	 * @param current 目标节点
	 * @param itorStack 节点迭代器栈
	 * @return
	 */
	private static <E> Node<E> getNext(Tree.Node<E> current, Stack<Iterator<Node<E>>> itorStack){
		List<Node<E>> children = current.getChildren();
		if(children != null && children.size() != 0){// has children
			// move to the first child
			Iterator<Node<E>> itor = children.iterator();
			itorStack.push(itor);// save itor
			return itor.next();
		}else{// hasn't a child
			// move to the next subling
			if(itorStack.empty()){
				return null;
			}else{
				Iterator<Node<E>> sublingsItor = itorStack.peek();
				if(sublingsItor.hasNext()){// has next subling
					return sublingsItor.next();
				}else{// return to parent
					while(true){
						itorStack.pop();
						if(itorStack.empty()){
							return null;
						}else{
							Iterator<Node<E>> superiorStage = itorStack.peek();
							if(superiorStage.hasNext()){
								return superiorStage.next();
							}else{
								continue;
							}
						}
					}
				}
			}
		}
	}
	
}