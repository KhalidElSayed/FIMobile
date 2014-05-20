package com.deploy.fi.services.responses;

import com.deploy.fi.services.IDataServiceResponse;

/**
 * This class returns a data loggin response   
 **/
public class DataLoginResponse implements IDataServiceResponse {

	private String responseMessage = null;
	private String responseToken = null;
	private int responseCode = 0;
	
	/**
	 * Constructor
	 **/
	public DataLoginResponse(int responseCode, String responseMessage, String responseToken){
		this.responseMessage = responseMessage;
		this.responseToken = responseToken;
		this.responseCode = responseCode;
	}
	
	/**
	 * Get token response
	 **/
	public String getToken(){
		return responseToken;
	}
	
	/**
	 * Get message response
	 **/
	public String getMessage(){
		return responseMessage;
	}
	
	/**
	 * Get code response
	 **/
	public int getCode(){
		return responseCode;
	}
	
}
