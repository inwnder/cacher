package com.inwnder.util.tree.exception;

import java.util.List;

import com.inwnder.util.tree.Tree;

/**
 * It's not a Tree. \n
 * There's at least on circle in the 'tree'.
 * @author Inwnder
 *
 */
public class CircledTreeException extends RuntimeException{

	private static final long serialVersionUID = 1278234185645455896L;
	
	List<Tree.Node<?>> circle;
	
	public CircledTreeException(List<Tree.Node<?>> circle){
		super(circle.toString());
		this.circle = circle;
	}
	
	/**
	 * Get the circle found in the 'tree'.
	 * @return
	 */
	public List<Tree.Node<?>> getCircle(){
		return circle;
	}
	
}
