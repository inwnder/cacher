package com.inwnder.util.tree.exception;

import com.inwnder.util.tree.Tree;

/**
 * There is at least one Node that be claimed as a parent of the other Node but it doesn't exists.
 * @author Inwnder
 *
 */
public class UndescribedParentNodeException extends RuntimeException{

	private static final long serialVersionUID = 2160491459120943076L;
	
	private Tree.Node<?> node;
	
	public UndescribedParentNodeException(Tree.Node<?> node){
		super(node.toString());
		this.node = node;
	}
	
	public Tree.Node<?> getNode(){
		return node;
	}
	
}