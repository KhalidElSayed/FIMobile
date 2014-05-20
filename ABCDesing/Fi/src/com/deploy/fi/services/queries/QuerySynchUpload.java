package com.deploy.fi.services.queries;

import java.util.Hashtable;

import com.deploy.fi.services.ServiceQuery;

/**
 * This class is a query timeLine main type
 **/
public class QuerySynchUpload extends ServiceQuery {

	/**
	 * Constructor
	 **/
	public QuerySynchUpload(int type) {
		super(type);
	}

	/**
	 * Returns a service url 
	 **/
	@Override
	public String getUrlService() {
		String urlService = URL_BASE + "uploadPedido.php";
		return urlService;
	}

	@Override
	public void setParams(Hashtable<String, String> params) {
	}
	
}
