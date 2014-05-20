package com.deploy.fi.services.responses;

import com.deploy.fi.services.IDataServiceResponse;

/**
 * This class returns a data synch download response   
 **/
public class DataSynchDownload implements IDataServiceResponse{

	private String responseMessage = null;
	private int responseCode = 0;
	private String data = null;
	
	/**
	 * Constructor
	 **/
	public DataSynchDownload(String responseMessage, int responseCode, String data){
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
	public String getData(){
		return data;
	}
	
}
