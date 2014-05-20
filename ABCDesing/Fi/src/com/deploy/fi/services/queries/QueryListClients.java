package com.deploy.fi.services.queries;

import java.util.Hashtable;

import com.deploy.fi.services.ServiceQuery;

/**
 * This class is a query timeLine main type
 **/
public class QueryListClients extends ServiceQuery {

	/**
	 * Constructor
	 **/
	public QueryListClients(int type) {
		super(type);
	}

	/**
	 * Returns a service url 
	 **/
	@Override
	public String getUrlService() {
		String urlService = URL_BASE + "consultaCliente.php";
		return urlService;
	}
	
	@Override
	public void setParams(Hashtable<String, String> params) {
		this.params = new Hashtable<String, String>();
		if(params != null && params.containsKey("NIT")){
			String NIT = params.get("NIT");
			this.params.put("NIT", NIT);
		}
	}
	
}
