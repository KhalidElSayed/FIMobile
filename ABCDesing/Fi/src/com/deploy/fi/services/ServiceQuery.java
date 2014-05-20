package com.deploy.fi.services;

import java.util.Hashtable;

/**
 * This abstract class are for create more service query types
 **/
public abstract class ServiceQuery {
	
	public final static String URL_BASE = "http://crmfi.com/api/";
	//public final static String URL_BASE = "http://192.168.0.13/api/";
	
	public final static int TYPE_NONE = 0;
	public final static int TYPE_LOGGIN = 1;
	public final static int TYPE_LIST_CLIENTS = 2;
	public final static int TYPE_SYNCH_UPLOAD = 3;
	public final static int TYPE_SYNCH_DOWNLOAD = 4;
	
	protected int type = TYPE_NONE; 
	
	/**
	 * Constructor
	 **/
	public ServiceQuery(int type){
		this.type = type;
	}

	/**
	 * Returns a service query type
	 **/
	public final int getType(){
		return type;
	}
	
	protected Hashtable<String, String> params = new Hashtable<String, String>();
	
	/**
	 * Returns a params sets on the query
	 **/
	public final Hashtable<String, String> getParams(){
		return params;
	}
	
	/**
	 * Sets a aparams to Query
	 **/
	public abstract void setParams(Hashtable<String, String> params);
	
	/**
	 * Returns a service url 
	 **/
	public abstract String getUrlService(); 
	
	private String rawJSON = "";
	
	public String getRawJSON(){
		return rawJSON;
	}
	
	public void setRawJSON(String rawJSON){
		if(rawJSON != null && rawJSON.length()>0) this.rawJSON = rawJSON;
	}
	
}
