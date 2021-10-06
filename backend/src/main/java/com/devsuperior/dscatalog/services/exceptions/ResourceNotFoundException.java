package com.devsuperior.dscatalog.services.exceptions;

public class ResourceNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public ResourceNotFoundException(String msg) {
		
		/** Passando o argumento  "msg" para o super, invocante o método da classe RuntimeException **/
		super(msg);
	}
	
	

}
