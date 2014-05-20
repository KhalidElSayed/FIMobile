package com.deploy.fi.services;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.me.JSONArray;
import org.json.me.JSONObject;

public class JSON {

	//********************************************************************************************
	
	public static int[] parseIntArray(String json){
		int[] array = null;
		try{
			String[] list = JSON.decodeArray(json);
			array = new int[list.length];
			for(int i=0; i<list.length; i++) array[i] = Integer.parseInt(list[i]);
		}catch(Exception e){
			array = null;
		}
		return array;
	}
	
	public static JSONObject parseTable(String root, Hashtable<Object, Object> table){
		JSONObject objectRoot = new JSONObject();
		try{
			JSONObject objectTmp = new JSONObject();
			Enumeration<Object> ee = table.keys();
			while(ee.hasMoreElements()){
				Object key = ee.nextElement();
				Object value = table.get(key);
				if(value instanceof Integer) {
					objectTmp.put(key.toString(), ((Integer)value).intValue());
				}
				else if(value instanceof Boolean) {
					objectTmp.put(key.toString(), ((Boolean)value).booleanValue());
				}
				else if(value instanceof String) {
					objectTmp.put(key.toString(), value.toString());
				}
				else{
					objectTmp.put(key.toString(), value);
				}
			}
			objectRoot.put(root, objectTmp);
		}catch(Exception e){}
		return objectRoot;
	}
	
	public static JSONArray parseArray(int[] array){
    	JSONArray jsonArray = new JSONArray();
    	if(array != null && array.length>0){
    		for(int indexArray=0; indexArray<array.length; indexArray++) jsonArray.put(array[indexArray]);
    	}
    	return jsonArray;
    }
    
	public static JSONArray parseArray(Object[] array){
    	JSONArray jsonArray = new JSONArray();
    	if(array != null && array.length>0){
    		for(int indexArray=0; indexArray<array.length; indexArray++){
    			Object obj = array[indexArray];
    			if(obj != null){
    				if(obj instanceof Integer) {
    					int value = ((Integer)obj).intValue();
    					jsonArray.put(value);
    				}
    				else if(obj instanceof Boolean) {
    					boolean value = ((Boolean)obj).booleanValue();
    					jsonArray.put(value);
    				}
    				else if(obj instanceof String) {
    					jsonArray.put(obj.toString());
    				}
    			}
    		}
		}
    	return jsonArray;
    }
    
    
	
	//********************************************************************************************
		
	public static String downloadText(String url){
    	String content = "";
    	try{
    		BufferedReader in = new BufferedReader(getStreamUlr(url));
    		//BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
    	    String str = "";
    	    while ((str = in.readLine()) != null) content += str;
    	    in.close();
    	}catch(Exception e){ content = ""; }
    	return content;
    }
	
	public static byte[] downloadBinary(String url){
    	try{
    		//InputStreamReader is = new InputStreamReader(new URL(url).openStream());
    		InputStreamReader is = getStreamUlr(url);
    		ByteArrayOutputStream  bos = new ByteArrayOutputStream();
    		int c;
    		while ((c = is.read()) != -1) bos.write(c);
    	    is.close();
    	    return bos.toByteArray();
    	}catch(Exception e){ }
    	return null;
    }
	
	//***************************************************************************************
	
	public static String[] decodeArray(String source){
        try{
        	org.json.me.JSONArray o = new org.json.me.JSONArray(source);
            int length = o.length();
            if(length>0){
                String[] array = new String[length];
                for(int i=0; i<length; i++){
                    array[i] =o.getString(i);
                }
                return array;
            }
        }catch(Exception e){}
        return null;
    }
    
    public static java.util.Hashtable<Object, Object> decodeHashtable(String source){
        java.util.Hashtable<Object, Object> table = new java.util.Hashtable<Object, Object>(0);
        try{
            org.json.me.JSONObject o = new org.json.me.JSONObject(source);
            int length = o.length();
            if(length > 0){
                Enumeration<Object> en =  o.keys();
                while(en.hasMoreElements()){
                    String key = en.nextElement()+"";
                    String value = o.get(key)+"";
                    table.put(key, value);
                }
            }
        }catch(Exception e){}
        return table;
    }
    
    public static String encodeArray(String[] source){
        try{
            org.json.me.JSONArray o = new org.json.me.JSONArray();
            for(int i=0; i<source.length; i++) o.put(source[i]);
            String s = o.toString();
            if(s != null && s.length()>0) return s;
        }catch(Exception e){}
        return null;
    }
    
    public static String encodeHashtable(java.util.Hashtable<Object, Object> source){
        try{
            org.json.me.JSONObject o = new org.json.me.JSONObject();
            if(source.size()>0){
                Enumeration<Object> en =  source.keys();
                while(en.hasMoreElements()){
                    String key = en.nextElement()+"";
                    String value = source.get(key)+"";
                    o.put(key, value);
                }
            }
            
            if(o.length() > 0){
                String s = o.toString();
                if(s != null && s.length()>0) return s;
            }            
        }catch(Exception e){}
        return null;
    }
	
  //***************************************************************************************
    
    public static InputStreamReader getStreamUlr(String urlStr){
    	try{
    		HttpParams httpParameters = new BasicHttpParams();
    	     HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
    	     HttpConnectionParams.setSoTimeout(httpParameters, 10000);
    	     
    	     HttpClient httpclient = new DefaultHttpClient(httpParameters);
    	     HttpGet httpGet = new HttpGet(urlStr);
    	     
    	     HttpResponse response = httpclient.execute(httpGet);
    	     HttpEntity entity = response.getEntity();
    	     InputStream is = entity.getContent();
    	     return new InputStreamReader(is);
    	}catch(Exception e){}
    	return null;
    }
    
}


