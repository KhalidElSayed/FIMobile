package com.deploy.fi.services;

/**
 * This class manages the erros on response services
 **/
public final class ServiceError {

	public final static int TYPE_EXCEPTION = 0;
	public final static int TYPE_ERROR_LOGIN = 1;
	
	private int type = TYPE_EXCEPTION;
	private String message = "";
	
	/**
	 * Constructor
	 **/
	public ServiceError(int type, String message){
		this.type = type;
		this.message = message;
	}
	
	/**
	 * Returns type of error code
	 **/
	public int getType(){
		return type;
	}
	
	/**
	 * Returns message of error
	 **/
	public String getMessage(){
		return message;
	}
	
	public String toString(){
		return "Type: " + type + " Message: " + message;
	}
	
}
