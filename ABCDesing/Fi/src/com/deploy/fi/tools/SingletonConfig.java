package com.deploy.fi.tools;

public class SingletonConfig {
    
	//*********************************************************************
	
	private static SingletonConfig INSTANCE = new SingletonConfig();
 
    private SingletonConfig() {}
 
    public static SingletonConfig getInstance() {
        return INSTANCE;
    }
    
    //*********************************************************************
    
    private String token = "";
    
    public String getToken(){
    	return token;
    }
    
    public void setToken(String token){
    	if(token != null && token.length()>0) this.token = token;
    }
}
