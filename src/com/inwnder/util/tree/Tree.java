package com.inwnder.util.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inwnder.util.tree.trace.FindToRootTrace;

/**
 * Multi-way tree
 * @author Inwnder
 *
 * @param <T> Value type of the tree node
 */
public class Tree<T> implements Iterable<Tree.Node<T>>{
	
	private Node<T> root;
	
	/**
	 * Create a tree by a pre-built root node
	 * @param root Root node
	 * @param comparator Comparator of the node value
	 * @return
	 */
	public static <T> Tree<T> newInstance(Node<T> root){
		return new Tree<T>(root);
	}
	
	private Tree(Node<T> root){
		this.root = root;
	}
	
	/**
	 * Get a TreeBuilder to build a tree from relations
	 * @return
	 */
	public static <V, E> TreeBuilder<V, E> getBuilder(Class<V> IDType, Class<E> valueType){
		return new TreeBuilder<V, E>();
	}
	
	/**
	 * Find the root of a certain node
	 * @param node The node
	 * @return
	 */
	public static <T> Node<T> findRoot(Node<T> node){
		CircleTracer<T> tracer = new CircleTracer<T>();
		tracer.trace(node);
		
		if(node == null) return null;
		while(node.getParent() != null){
			node = node.getParent();
			tracer.trace(node);
		}
		return node;
	}
	
	/**
	 * Get a new tree from the origin tree that correlative with the node
	 * @param node The node
	 * @return
	 */
	public Tree<T> getCorrelativeTree(Node<T> node){
		Node<T> current = node;
		Node<T> copyer = node;
		
		while(current.getParent() != null){
			current = current.getParent();
			
			//copy the structure
			Node<T> origin = copyer;
			copyer = new Node<T>(current.getValue());
			copyer.addChild(origin);
		}
		
		//find root
		if(current != root){
			throw new RuntimeException("The tree doesn't contain this node:" + node);
		}else{
			return Tree.newInstance(copyer);
		}
	}
	
	/**
	 * Find the first Node whose value equals to the parameter
	 * @param value The value of a Node
	 * @return
	 */
	public Node<T> find(T value){
		return find(root, value);
	}
	
	/**
	 * Get the root of the tree
	 * @return
	 */
	public Node<T> getRoot(){
		return root;
	}
	
	/**
	 * Get a Trace that from the start Node to the root
	 * @param start The start Node
	 * @return
	 */
	public FindToRootTrace<T> findToRoot(Node<T> start){
		return new FindToRootTrace<T>(start);
	}
	
	/**
	 * Find the first Node whose value equals to the parameter
	 * @param node Use this node as root to search
	 * @param value The value
	 * @return
	 */
	private Node<T> find(Node<T> node, T value){
		Node<T> target;
		if(node.value.equals(value)){
			return node;
		}else{
			for(Node<T> child:node.children){
				if((target = find(child, value)) != null){
					return target;
				}
			}
			return null;
		}
	}
	
	/**
	 * Node of a Tree
	 * @author Inwnder
	 *
	 * @param <E> Value type of the tree node
	 */
	public static class Node<E>{
		private Node<E> parent = null;
		private List<Node<E>> children = new ArrayList<Node<E>>();
		private E value;
		
		public Node(){
		}
		
		public Node(E value){
			this.value = value;
		}
		
		public Node(Node<E> parent, E value){
			this.parent = parent;
			this.value = value;
		}
		
		public void setValue(E value){
			this.value = value;
		}
		
		public E getValue(){
			return value;
		}
		
		public Node<E> getParent(){
			return parent;
		}
		
		public void setParent(Node<E> parent){
			this.parent = parent;
		}
		
		public List<Node<E>> getChildren(){
			return children;
		}
		
		public void addChild(Node<E> child){
			this.children.add(child);
		}
		
		public void removeChild(Node<E> child){
			this.children.remove(child);
		}
		
		public void clearChild(){
			this.children.clear();
		}
		
		public boolean isRoot(){
			return parent == null;
		}
		
		public boolean isLeaf(){
			return children.size() == 0;
		}
		
		public List<Node<E>> sublings(){
			if(parent != null){
				List<Node<E>> sublings = parent.getChildren();
				if(sublings != null){
					return sublings;
				}else{
					return new ArrayList<Node<E>>(0);
				}
			}else{
				return new ArrayList<Node<E>>(0);
			}
		}
		
		/**
		 * Get a Trace that from this Node to the root
		 * @return traceToRoot
		 */
		public FindToRootTrace<E> findToRoot(){
			return new FindToRootTrace<E>(this);
		}
		
		public String toString(){
			if(value == null){
				return "";
			}else{
				return value.toString();
			}
		}
		
		/**
		 * Find if this node has a child whose value equals to the given value
		 * @param value The given value
		 * @return
		 */
		public boolean hasChild(E value) {
			for(Node<E> child:children) {
				if(child.getValue().equals(value)) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * Find a child whose value equals to the given value
		 * @param value The given value
		 * @return
		 */
		public Node<E> getChild(E value) {
			for(Node<E> child:children) {
				if(child.getValue().equals(value)) {
					return child;
				}
			}
			return null;
		}
	}

	@Override
	public Iterator<Tree.Node<T>> iterator() {
		return new DeepFirstIterator<T>(this);
	}
	
	public Iterator<Tree.Node<T>> bottomFirstIterator(){
		return new BottomFirstIterator<T>(this);
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		String indent = "-";
		
		DeepFirstIterator<T> itor = new DeepFirstIterator<T>(this);
		
		
		while(itor.hasNext()) {
			Tree.Node<T> node = itor.next();
			for(int i = 0; i < itor.getLevel(); ++i) {
				builder.append(indent);
			}
			builder.append(node);
			builder.append("\n");
		}
		
		return builder.toString();
	}

	public static void main(String[] args) {
		TreeBuilder<Integer, Integer> builder = Tree.getBuilder(Integer.class, Integer.class);
		builder.addValue(1, 1, null);
		builder.addValue(2, 2, 1);
		builder.addValue(3, 3, 1);
		builder.addValue(4, 4, 2);
		builder.addValue(5, 5, 2);
		builder.addValue(6, 6, 3);
		Tree<Integer> tree = builder.build();
		System.out.println(tree);
	}
	
}
