package com.deploy.fi.services.queries;

import java.util.Hashtable;

import com.deploy.fi.services.ServiceQuery;

/**
 * This class is a query timeLine main type
 **/
public class QueryLogin extends ServiceQuery {

	/**
	 * Constructor
	 **/
	public QueryLogin(int type) {
		super(type);
	}

	/**
	 * Returns a service url 
	 **/
	@Override
	public String getUrlService() {
		String urlService = URL_BASE + "login.php";
		return urlService;
	}
	
	@Override
	public void setParams(Hashtable<String, String> params) {
		this.params = new Hashtable<String, String>();
		if(params != null && params.containsKey("user") && params.containsKey("pass")){
			String user = params.get("user");
			String pass = params.get("pass");
			this.params.put("user", user);
			this.params.put("pass", pass);
		}
	}
	
}
