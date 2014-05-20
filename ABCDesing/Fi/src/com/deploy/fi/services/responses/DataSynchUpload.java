package com.deploy.fi.services.responses;

import com.deploy.fi.services.IDataServiceResponse;

/**
 * This class returns a data synch download response   
 **/
public class DataSynchUpload implements IDataServiceResponse{

	private String responseMessage = null;
	private int responseCode = 0;
	
	/**
	 * Constructor
	 **/
	public DataSynchUpload(String responseMessage, int responseCode){
		this.responseMessage = responseMessage;
		this.responseCode = responseCode;
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
