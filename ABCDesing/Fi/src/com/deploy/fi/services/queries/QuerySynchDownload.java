package com.deploy.fi.services.queries;

import java.util.Hashtable;

import com.deploy.fi.services.ServiceQuery;

/**
 * This class is a query timeLine main type
 **/
public class QuerySynchDownload extends ServiceQuery {

	/**
	 * Constructor
	 **/
	public QuerySynchDownload(int type) {
		super(type);
	}

	/**
	 * Returns a service url 
	 **/
	@Override
	public String getUrlService() {
		//String urlService = URL_BASE + "EstructDB.php";
		//String urlService = URL_BASE + "Estructura3.php";
		String urlService = URL_BASE + "nuevaEstructura.php";
		return urlService;
	}
	
	@Override
	public void setParams(Hashtable<String, String> params) {
		this.params = new Hashtable<String, String>();
		if(params != null && params.containsKey("token")){
			String token = params.get("token");
			this.params.put("token", token);
		}
	}
	
}
