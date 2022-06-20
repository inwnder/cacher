package com.inwnder.util.tree;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.inwnder.util.tree.Tree.Node;

/**
 * Bottom First Iterator<br>
 * Ensure to reach the children later than their parent 
 * @author Inwnder
 *
 * @param <E> Type of the Node value
 */
public class BottomFirstIterator<E> implements Iterator<Tree.Node<E>>{

	private Tree<E> tree;
	private Stack<Map.Entry<Iterator<Node<E>>, Node<E>>> itorStack = new Stack<Map.Entry<Iterator<Node<E>>, Node<E>>>();
	private Tree.Node<E> current = null, next = null;
	
	BottomFirstIterator(Tree<E> tree){
		this.tree = tree;
		this.next = getNext();
	}
	
	@Override
	public boolean hasNext() {
		return next != null;
	}

	@Override
	public Node<E> next() {
		current = next;
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
		return getNext(tree.getRoot(), current, itorStack);
	}
	
	/**
	 * 移动到目标节点的下一个节点
	 * @param root 搜索的根节点
	 * @param current 目标节点
	 * @param itorStack 节点迭代器栈
	 * @return
	 */
	private static <E> Node<E> getNext(Tree.Node<E> root, Tree.Node<E> current, Stack<Map.Entry<Iterator<Node<E>>, Node<E>>> itorStack){
		Tree.Node<E> tmp;
		if(current == root){
			return null;
		}else if(current == null){// initial
			// move to the first leaf
			tmp = root;
			List<Node<E>> children = tmp.getChildren();
			while(children != null && children.size() != 0){
				Iterator<Node<E>> itor = children.iterator();
				tmp = itor.next();
				children = tmp.getChildren();
				itorStack.add(new AbstractMap.SimpleEntry<Iterator<Node<E>>, Node<E>>(itor, tmp));
			}
			return tmp;
		}else{
			Map.Entry<Iterator<Node<E>>, Node<E>> sublingsItor = itorStack.peek();
			if(sublingsItor.getKey().hasNext()){// has next subling
				tmp = sublingsItor.getKey().next();
				sublingsItor.setValue(tmp);
				List<Node<E>> children = tmp.getChildren();
				while(children != null && children.size() != 0){
					Iterator<Node<E>> itor = children.iterator();
					tmp = itor.next();
					children = tmp.getChildren();
					itorStack.add(new AbstractMap.SimpleEntry<Iterator<Node<E>>, Node<E>>(itor, tmp));
				}
				return tmp;
			}else{// return to parent
				itorStack.pop();
				if(itorStack.empty()){
					return root;
				}else{
					Map.Entry<Iterator<Node<E>>, Node<E>> superiorStage = itorStack.peek();
					return superiorStage.getValue();
				}
			}
		}
		
	}
	
}