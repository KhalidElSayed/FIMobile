package com.deploy.fi.services.responses;

import com.deploy.fi.services.IDataServiceResponse;

/**
 * This class returns a data loggin response   
 **/
public class DataListClientsResponse implements IDataServiceResponse {

	private String responseMessage = null;
	private int responseCode = 0;
	private String[] data = null;
	
	/**
	 * Constructor
	 **/
	public DataListClientsResponse(int responseCode, String responseMessage, String[] data){
		this.responseMessage = responseMessage;
		this.responseCode = responseCode;
		this.data = data;
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
	
	/**
	 * Get data response
	 **/
	public String[] getData(){
		return data;
	}
	
}
