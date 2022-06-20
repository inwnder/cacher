package com.inwnder.util.tree.exception;

/**
 * There are duplicated ID set in the TreeBuilder.
 * @author Inwnder
 *
 */
public class DuplicatedIDException extends RuntimeException{
	
	private static final long serialVersionUID = 8215775658981227165L;

	private Object ID;
	
	public DuplicatedIDException(Object ID){
		super(ID.toString());
		this.ID = ID;
	}
	
	public Object getID(){
		return ID;
	}
	
	
}
