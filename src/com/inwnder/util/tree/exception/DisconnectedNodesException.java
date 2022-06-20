package com.inwnder.util.tree.exception;

import java.util.Set;

import com.inwnder.util.tree.Tree;

/**
 * It's not a tree. At least not ONE. \n
 * There's at least one Node disconnect from the others.
 * @author Inwnder
 *
 */
public class DisconnectedNodesException extends RuntimeException{
	
	private static final long serialVersionUID = -4741375561537319999L;

	private Set<Tree.Node<?>> disconnectedNodes;
	
	public DisconnectedNodesException(Set<Tree.Node<?>> disconnectedNodes){
		super();
		this.disconnectedNodes = disconnectedNodes;
	}
	
	/**
	 * Get the Nodes disconnected with the other nodes.
	 * @return
	 */
	public Set<Tree.Node<?>> getDisconnectedNodes(){
		return disconnectedNodes;
	}
	
}