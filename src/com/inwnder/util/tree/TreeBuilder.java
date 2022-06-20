package com.inwnder.util.tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.inwnder.util.tree.Tree.Node;
import com.inwnder.util.tree.exception.CircledTreeException;
import com.inwnder.util.tree.exception.DisconnectedNodesException;
import com.inwnder.util.tree.exception.DuplicatedIDException;
import com.inwnder.util.tree.exception.UndescribedParentNodeException;

/**
 * Builder of a multi-way tree<br>
 * Build the multi-way tree using relations and value
 * @author Inwnder
 * @param <V> The ID type of the Node
 * @param <E> The value type of the Node
 * 
 */
public class TreeBuilder<V, E>{
	
	//Settings
	private boolean nocover = false;//ID重复时会报错
	
	//cache
	private Tree<E> cache;//生成树的缓存
	private Map<V, Node<E>> nodeMap = new HashMap<V, Node<E>>();
	private Node<E> expectedRoot = null;
	
	TreeBuilder(){
		
	}
	
	/**
	 * Add a relation into the builder
	 * @param ID ID of the Node
	 * @param value Value of the Node
	 * @param parentID ID of the parent of the Node. Set null while it's the root.
	 */
	public void addValue(V ID, E value, V parentID){
		cache = null;//标记cache需要更新
		
		Node<E> current = prepareNode(ID, nocover);//准备这个节点
		current.setValue(value);//将值填入这个节点
		
		if(parentID != null){//如果声明这个节点存在父节点
			Node<E> parent = prepareNode(parentID, false);//准备父节点
			parent.addChild(current);//将当前节点加入到父节点的子节点列表中
			current.setParent(parent);//设置当前节点的父节点
			
			offerRoot(parent);
		}else{
			offerRoot(current);
		}
	}
	
	/**
	 * Build a tree
	 * @return the tree
	 * @throws UndescribedParentNodeException
	 * @throws CircledTreeException
	 */
	public Tree<E> build() throws UndescribedParentNodeException, CircledTreeException, DisconnectedNodesException{
		/*
		 * 论证环路的简便验证方法
		 * 1. 若树中存在环路: 
		 *   由于树中每个节点的父节点仅有一个，则环路与其余部分必然不连通
		 *   任选一环路，将树分为环路中和环路外两部分
		 *   1.1. 若根节点在环路中: 在树中选取随机节点
		 *     1.1.1. 若随机节点在环路中，则可通过traceToRoot找到这个环路
		 *     1.1.2. 若随机节点不在环路中，则从traceToRoot找到的根节点进行DFS必然不能遍历整个环路，将报错发现与根不连通的节点
		 *   1.2. 若根节点不在环路中: 则以根节点进行的DFS必然不能遍历整个环路，将报错发现与根不连通的节点
		 * 2. 若树中不存在环路: 正常
		 */
		
		if(cache == null){//如果不存在缓存
			//从树中任选节点，traceToRoot来找到root
			Tree<E> tree = Tree.newInstance(Tree.findRoot(expectedRoot));//生成一棵树并判断1.1.1的情况
			
			//检查：
			// 是否存在节点未填充
			// 是否所有节点都在同一个树中
			Set<Node<?>> untouchedNode = new HashSet<Node<?>>();//未被遍历过的节点集合
			for(Node<E> node:nodeMap.values()){
				untouchedNode.add(node);
				if(node.getValue() == null){//存在节点未填充
					throw new UndescribedParentNodeException(node);
				}
			}
			
			for(Node<E> node:tree){
				untouchedNode.remove(node);//标记这个节点已被遍历
			}
			
			if(untouchedNode.size() > 0){//DFS存在未遍历的节点，则必有节点与根不连通
				throw new DisconnectedNodesException(untouchedNode);
			}
			
			cache = tree;//建立缓存
		}
		
		return cache;
	}
	
	/**
	 * Prepare a Node<br>
	 * Return the Node if exists<br>
	 * Create and return the Node if not exists<br>
	 * @param ID ID of the Node
	 * @param nocover Set true if you want to throw an exception while ID is duplicated
	 * @return 节点
	 */
	private Node<E> prepareNode(V ID, boolean nocover) throws DuplicatedIDException{
		Node<E> node = nodeMap.get(ID);//尝试获取当前的节点
		if(node == null){//如果不存在这个节点
			node = new Node<E>();//创建一个新的节点
			nodeMap.put(ID, node);//将这个节点放入map
		}else if(nocover){//存在这个节点
			if(node.getValue() != null){
				throw new DuplicatedIDException(ID);
			}
		}
		return node;
	}
	
	/**
	 * Tell the builder that this node might be the root
	 * @param node
	 */
	private void offerRoot(Node<E> node){
		if(expectedRoot == null){
			expectedRoot = node;
		}else{
			if(node == expectedRoot.getParent()){
				expectedRoot = node;
			}
		}
	}
	
	/**
	 * Get the Node by ID
	 * @param ID 节点ID
	 * @return
	 */
	public Node<E> getNode(V ID){
		return nodeMap.get(ID);
	}
	
	/**
	 * Clear all the Nodes
	 */
	public void clear(){
		cache = null;
		nodeMap = new HashMap<V, Node<E>>();
		expectedRoot = null;
	}
	
	/**
	 * Replace the ID of a Node
	 * @param originID
	 * @param newID
	 * @param nocover Set true if you want to throw an exception while ID is duplicated
	 */
	public void changeID(V originID, V newID) throws DuplicatedIDException{
		if(nocover){
			if(nodeMap.get(newID) != null){
				throw new DuplicatedIDException(newID);
			}
		}
		nodeMap.put(newID, nodeMap.get(originID));
		nodeMap.remove(originID);
	}
	
	/**
	 * Set nocover
	 * @param nocover Set true if you want to throw an exception while ID is duplicated
	 */
	public void setNoCover(boolean nocover){
		this.nocover = nocover;
	}
	
	/**
	 * Get nocover
	 * @return true if you want to throw an exception while ID is duplicated
	 */
	public boolean getNoCover(){
		return nocover;
	}
}
