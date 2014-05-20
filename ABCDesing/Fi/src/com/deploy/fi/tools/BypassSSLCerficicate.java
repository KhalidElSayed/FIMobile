package com.deploy.fi.tools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.util.Log;

/*
 * This class provides connections to HTTPS service with invalid certificates and standard HTTP services, 
 * contains 3 common methods for service consumption, GET, POST and JSON.
 * 
 * Usage:
 * 
 * SendGET:
			
 * Hashtable<String,String> headers = new Hashtable<String,String>();
 * String url = "https://x.x.x.x/BypassSSLCerficicate/test.php";
 * headers.put("User-Agent", "BypassSSLCerficicate 1.1");
 * BypassSSLCerficicate bug = BypassSSLCerficicate.getInstance(true);
 * String content = bug.sendGET(url, headers);
			
 * SendPOST:
				
 * Hashtable<String,String> headers = new Hashtable<String,String>();
 * String url = "https://x.x.x.x/BypassSSLCerficicate/test.php";
 * headers.put("User-Agent", "BypassSSLCerficicate 1.1");
 * Hashtable<String,String> params = new Hashtable<String,String>();
 * params.put("name", "you");
 * BypassSSLCerficicate bug = BypassSSLCerficicate.getInstance(true);
 * String content = bug.sendPOST(url, headers, params);
			
 * SendJSON:
			
 * Hashtable<String,String> headers = new Hashtable<String,String>();
 * String url = "https://x.x.x.x/BypassSSLCerficicate/test.php";
 * headers.put("User-Agent", "BypassSSLCerficicate 1.1");
 * String json = "{\"key\":\"value\"}";
 * BypassSSLCerficicate bug = BypassSSLCerficicate.getInstance(true);
 * String content = bug.sendJSON(url, headers, json);
 * 
 * */
public class BypassSSLCerficicate {

	private boolean enableAllCertificates = false;
	
	private BypassSSLCerficicate(){
	}
	
	/*
	 * Crea una instancia de la clase
	 * 
	 * Params:
	 * enableAllCertificates: para conexiones por https true activa el bypass con certificados no validos
	 * */
	public static BypassSSLCerficicate getInstance(boolean enableAllCertificates){
		BypassSSLCerficicate cls = new BypassSSLCerficicate();
		cls.enableAllCertificates = enableAllCertificates;
		return cls;
	}
	
	private void enableAllCertificates(){
		try{
			X509TrustManager trustManager = new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}
				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			TrustManager[] trustAllCerts = new TrustManager[] {trustManager};
	
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
						
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private HttpURLConnection getURLConnection(String urlStr, boolean isGET) throws MalformedURLException, IOException {
		if(enableAllCertificates) enableAllCertificates();
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(1000 * 60);
		conn.setReadTimeout(1000 * 60);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		if(isGET) conn.setRequestMethod("GET");
		else conn.setRequestMethod("POST");
		conn.connect();
		return conn;
	}
	
	private HttpURLConnection addHeaders(HttpURLConnection conn, Hashtable<String,String> params){
		if(conn != null && params != null && params.size()>0){
			try{
				Enumeration keys = params.keys();
				while(keys.hasMoreElements()){
					String key = keys.nextElement().toString();
					String value = params.get(key).toString();
					if(key != null && value != null) conn.setRequestProperty(key, value);
				}
			}catch(Exception e){}
		}
		return conn;
	}
	
	private void addPostVars(HttpURLConnection conn, Hashtable<String,String> params){
		try{
			String method = conn.getRequestMethod();
			if(method.equals("POST")){
				String urlParameters = "";
				Enumeration keys = params.keys();
				while(keys.hasMoreElements()){
					String key = URLEncoder.encode(keys.nextElement().toString(), "UTF-8");
					String value = URLEncoder.encode(params.get(key).toString(), "UTF-8");
					if(key != null && value != null) {
						if(urlParameters.length() == 0) urlParameters += key + "=" + value;
						else urlParameters += "&" + key + "=" + value;
					}
				}
				if(urlParameters.length() > 0){
					DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
					wr.writeBytes(urlParameters);
					wr.flush();
					wr.close();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void addPostJson(HttpURLConnection conn, String json, boolean useUrlEncode){
		try{
			String method = conn.getRequestMethod();
			if(method.equals("POST")){
				DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
				if(useUrlEncode) wr.writeBytes(URLEncoder.encode(json,"UTF-8"));
				else wr.writeBytes(json);
				wr.flush();
				wr.close();
			}
		}catch(Exception e){}
	}
	
	private String getTextContent(HttpURLConnection conn) throws MalformedURLException, IOException {
		String content = "";
		Reader reader = new InputStreamReader(conn.getInputStream());
		while (true) {
			int ch = reader.read();
			if (ch==-1) break;
			content += (char)ch;
		}
		conn.disconnect();
		return content;
	}
	
	private boolean getTextContentToFile(HttpURLConnection conn, File file) throws MalformedURLException, IOException {
		boolean content = false;
		try {
			OutputStream out = new FileOutputStream(file);
			InputStream in = conn.getInputStream();
			byte[] buffer = new byte[1024];
		    int read;
		    long total = 0;
		    while((read = in.read(buffer)) != -1) {
		    	out.write(buffer, 0, read);
		    	total += read;
		    }
		    Log.e("total", String.valueOf(total));
		    out.close();
		    
		    conn.disconnect();
		    content = true;
        } catch(Exception e) {
        	e.printStackTrace();//java.net.SocketTimeoutException
        	content = false;
        }
		return content;
	}
	
	/*
	 * This method lets you create a url with parameters such GET
	 * 
	 * Params: 
	 * Ulr: url of service
	 * Params: headers with the request will be made
	 * */
	public static String encodeUrl(String url, Hashtable<String,String> params){
		String outUrl = url;
		try{
			String urlParameters = "";
			Enumeration<String> keys = params.keys();
			while(keys.hasMoreElements()){
				String key = URLEncoder.encode(keys.nextElement(), "UTF-8");
				String value = URLEncoder.encode(params.get(key), "UTF-8");
				if(key != null && value != null) {
					if(urlParameters.length() == 0) urlParameters += "?" + key + "=" + value;
					else urlParameters += "&" + key + "=" + value;
				}
			}
			if(outUrl != null && urlParameters.length() > 0) outUrl += urlParameters;
		}catch(Exception e){}
		return outUrl;
	}
	
	/*
	 * This method allows to send a request type JSON
	 * 
	 * Params: 
	 * Ulr: url of service
	 * Headers: headers with the request will be made
	 * Json: body json for send
	 * */
	public String sendJSON(String url, Hashtable<String,String> headers, String json){
		String content = "";
		try{
			if(json != null && json.length()>0){
				HttpURLConnection conn = getURLConnection(url, false);
				if(headers != null && headers.size()>0) addHeaders(conn, headers);
				addPostJson(conn, json, false);
				content = getTextContent(conn);
			}
			else{
				HttpURLConnection conn = getURLConnection(url, false);
				if(headers != null && headers.size()>0) addHeaders(conn, headers);
				content = getTextContent(conn);
			}
		}catch(Exception e){
			content = "";
		}
		return content;
	}
	
	public boolean sendJSON_toFile(String url, Hashtable<String,String> headers, String json, File file){
		boolean content = false;
		try{
			if(json != null && json.length()>0){
				HttpURLConnection conn = getURLConnection(url, false);
				if(headers != null && headers.size()>0) addHeaders(conn, headers);
				addPostJson(conn, json, false);
				content = getTextContentToFile(conn, file);
			}
			else{
				HttpURLConnection conn = getURLConnection(url, false);
				if(headers != null && headers.size()>0) addHeaders(conn, headers);
				content = getTextContentToFile(conn, file);
			}
		}catch(Exception e){
			e.printStackTrace();
			content = false;
		}
		return content;
	}
	
	public boolean sendPOSTtoFile(String url, Hashtable<String,String> headers, Hashtable<String,String> params, File file){
		boolean content = false;
		try{
			if(params != null && params.size()>0){
				HttpURLConnection conn = getURLConnection(url, false);
				if(headers != null && headers.size()>0) addHeaders(conn, headers);
				addPostVars(conn, params);
				content = getTextContentToFile(conn, file);
			}
			else{
				HttpURLConnection conn = getURLConnection(url, false);
				if(headers != null && headers.size()>0) addHeaders(conn, headers);
				content = getTextContentToFile(conn, file);
			}
		}catch(Exception e){
			content = false;
			Log.e("Exception", e.getMessage());
		}
		return content;
	}
	
	/*
	 * This method allows to send a request type POST
	 * 
	 * Params: 
	 * Ulr: url of service
	 * Headers: headers with the request will be made
	 * params: parameters that will be sent by POST 
	 * */
	public String sendPOST(String url, Hashtable<String,String> headers, Hashtable<String,String> params){
		String content = "";
		try{
			if(params != null && params.size()>0){
				HttpURLConnection conn = getURLConnection(url, false);
				if(headers != null && headers.size()>0) addHeaders(conn, headers);
				addPostVars(conn, params);
				content = getTextContent(conn);
			}
			else{
				HttpURLConnection conn = getURLConnection(url, false);
				if(headers != null && headers.size()>0) addHeaders(conn, headers);
				content = getTextContent(conn);
			}
		}catch(Exception e){
			content = "";
			Log.e("Exception", e.getMessage());
		}
		return content;
	}
	
	/*
	 * This method allows to send a request type GET
	 * 
	 * Params: 
	 * Ulr: url of service
	 * Headers: headers with the request will be made
	 * */
	public String sendGET(String url, Hashtable<String,String> headers){
		String content = "";
		try{
			HttpURLConnection conn = getURLConnection(url, true);
			if(headers != null && headers.size()>0) addHeaders(conn, headers);
			content = getTextContent(conn);
		}catch(Exception e){
			content = "";
		}
		return content;
	}
	
}