package com.deploy.fi.services;

/**
 * This a interface to manage a service responses
 **/
public interface IServiceResponse {

	/**
	 * Occurs when a service responds an success 
	 **/
	public void successResponse(IDataServiceResponse response);
	
	/**
	 * Occurs when a service responds an error 
	 **/
	public void errorResponse(ServiceError error);
	
}
