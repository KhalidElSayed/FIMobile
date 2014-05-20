package com.deploy.fi.services;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.deploy.fi.services.responses.DataListClientsResponse;
import com.deploy.fi.services.responses.DataLoginResponse;
import com.deploy.fi.services.responses.DataSynchDownload;
import com.deploy.fi.services.responses.DataSynchUpload;
import com.deploy.fi.tools.BypassSSLCerficicate;
import com.deploy.fi.tools.DBManager;
import com.deploy.fi.tools.FileManager;

/**
 * This class manages asynchronous queries to a Web Services
 **/
public class Services extends AsyncTask<Object, Object, Object> {

	private IServiceResponse responseService = null;
	private ServiceQuery queryService = null;
	private Context context = null;
	
	/**
	 * Constructor
	 **/
	public Services(Context context, ServiceQuery query, IServiceResponse response){
		queryService = query;
		this.context = context;
		responseService = response;
	}
	
	@Override
	protected Object doInBackground (Object... values) {
		Object result = null;
		if(queryService != null && responseService != null){
			try{
				Hashtable<String,String> headers = new Hashtable<String,String>();
				headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
				headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
				headers.put("Accept-Encoding", "gzip,deflate,sdch");
				headers.put("Accept-Language", "es-ES,es;q=0.8");
				headers.put("Connection", "Keep-Alive");
				
				int typeQuery = queryService.getType();
				
				boolean executsQuery = false, usesJSON = false;
				if(typeQuery == ServiceQuery.TYPE_LOGGIN) executsQuery = true;
				else if(typeQuery == ServiceQuery.TYPE_LIST_CLIENTS) executsQuery = true;
				else if(typeQuery == ServiceQuery.TYPE_SYNCH_DOWNLOAD) executsQuery = true;
				else if(typeQuery == ServiceQuery.TYPE_SYNCH_UPLOAD) {
					executsQuery = true;
					usesJSON = true;
				}
				
				if(executsQuery){
					String urlService = queryService.getUrlService();
					BypassSSLCerficicate bypass = BypassSSLCerficicate.getInstance(true);
					if(typeQuery == ServiceQuery.TYPE_SYNCH_DOWNLOAD){
						
						Hashtable<String, String> params = queryService.getParams();
						params.put("typeQuery", "headers");
						//params.put("token", "headers");
						String json = bypass.sendPOST(urlService, headers, params);
						String[] jsonHeaders = JSON.decodeArray(json);
						if(jsonHeaders != null && jsonHeaders.length>0){
							//urlService = urlService.substring(0, urlService.indexOf("?"));
							for(int h=0; h<jsonHeaders.length; h++){
								Hashtable jsonSections = JSON.decodeHashtable(jsonHeaders[h]);
								int blockSize = Integer.parseInt(jsonSections.get("blockSize").toString());
								int total = Integer.parseInt(jsonSections.get("total").toString());
								String section = jsonSections.get("nombre").toString();
								int blockRange = 0;
								if(total >0){
									int cicles = total / blockSize;
									if(cicles * blockSize != total) cicles++;
									for(int r=0; r<cicles; r++){
										String[] jsonContents = null;
										
										//if(section.equals("sku"))  
										{
 											params = queryService.getParams();
											params.put("typeQuery", section);
											params.put("indexQuery", String.valueOf(blockRange));
											json = bypass.sendPOST(urlService, headers, params);
											jsonContents = JSON.decodeArray(json);
										}
										
										DBManager db = null;
										
										if(jsonContents != null && jsonContents.length>0){
											for(int c=0; c<jsonContents.length; c++){
												Hashtable dataContents = JSON.decodeHashtable(jsonContents[c]);
												db = new DBManager(context);
												db.open();
												if(section.equals("listaprecio_sucursales") && !db.existRecordListaprecioSucursales(dataContents)) db.insertRecordListaprecioSucursales(dataContents);
												else if(section.equals("listaprecio_detalle") && !db.existRecordListaprecioDetalle(dataContents)) db.insertRecordListaprecioDetalle(dataContents);
												else if(section.equals("subcategorias") && !db.existRecordSubcategorias(dataContents)) db.insertRecordSubcategorias(dataContents);
												else if(section.equals("lista_precios") && !db.existRecordListaPrecios(dataContents)) db.insertRecordListaPrecios(dataContents);
												else if(section.equals("tipo_prendas") && !db.existRecordTipoPrendas(dataContents)) db.insertRecordTipoPrendas(dataContents);
												else if(section.equals("vendedores") && !db.existRecordVendedores(dataContents)) db.insertRecordVendedores(dataContents);
												else if(section.equals("categorias") && !db.existRecordCategorias(dataContents)) db.insertRecordCategorias(dataContents);
												else if(section.equals("sucursales") && !db.existRecordSucursales(dataContents)) db.insertRecordSucursales(dataContents);
												else if(section.equals("origenes") && !db.existRecordOrigenes(dataContents)) db.insertRecordOrigenes(dataContents);
												else if(section.equals("clientes") && !db.existRecordClientes(dataContents)) db.insertRecordClientes(dataContents);
												else if(section.equals("IndiceTalla") && !db.existIndiceTalla(dataContents)) db.insertIndiceTalla(dataContents);
												else if(section.equals("canales") && !db.existRecordCanales(dataContents)) db.insertRecordCanales(dataContents);
												else if(section.equals("colores") && !db.existRecordColores(dataContents)) db.insertRecordColores(dataContents);
												else if(section.equals("cuentas") && !db.existRecordCuentas(dataContents)) db.insertRecordCuentas(dataContents);
												else if(section.equals("marcas") && !db.existRecordMarcas(dataContents)) db.insertRecordMarcas(dataContents);
												else if(section.equals("ciudad") && !db.existRecordCiudad(dataContents)) db.insertRecordCiudad(dataContents);
												else if(section.equals("zonas") && !db.existRecordZonas(dataContents)) db.insertRecordZonas(dataContents);
												else if(section.equals("zonas") && !db.existRecordZonas(dataContents)) db.insertRecordZonas(dataContents);
												else if(section.equals("sku") && !db.existRecordSku(dataContents)) db.insertRecordSku(dataContents);
												db.close();
											}
											Log.e("End Insert", section);
										}
										blockRange += blockSize;
									}
								}
							}
							result = true;
							
							DBManager db = new DBManager(context);
							db.open();
							db.backupDatabase();
							db.close();
						}
					}
					else{
						if(!usesJSON) result = bypass.sendPOST(urlService, headers, queryService.getParams());
						else result = bypass.sendJSON(urlService, headers, queryService.getRawJSON());
					}
				}
				
			}catch(Exception e){
				e.printStackTrace();
				if(e.toString() != null) Log.e("Exception", e.toString());
				result = "Exception ->" + e.toString();
			}
		}
		return result;
	}
	
	protected void onPostExecute(Object result){
		super.onPostExecute(result);//{"responseCode":"1","responseMessage":"Success Login","token":"4dc57f1997eb15ae07c7622c6950e28809097b73ede71ed8399793f372bf28e6"}
		if(queryService != null && responseService != null){
			if(result == null || result.toString().startsWith("Exception ->")) {
				ServiceError se = new ServiceError(ServiceError.TYPE_EXCEPTION, result.toString());
				responseService.errorResponse(se);
			}
			else{
				int typeQuery = queryService.getType();
				if(typeQuery == ServiceQuery.TYPE_LOGGIN) parseServiceLoggin(result);
				else if(typeQuery == ServiceQuery.TYPE_LIST_CLIENTS) parseServiceListClients(result);
				else if(typeQuery == ServiceQuery.TYPE_SYNCH_DOWNLOAD) parseServiceSynchDownlod(result);
				else if(typeQuery == ServiceQuery.TYPE_SYNCH_UPLOAD) parseServiceSynchUpload(result);
			}
		}		
	}
		@Override
	protected void onProgressUpdate(Object... values) {
		if(queryService != null && responseService != null){
		}
	}
	
	/**
	 * This parsed the loggin response 
	 **/
	private void parseServiceSynchUpload(Object result){
		try{
			if(result == null || result.toString().length() == 0) throw new IOException("Null Connection");
			else{
				//Log.e("JSON", result.toString());
				Hashtable<?, ?> responseJSON = JSON.decodeHashtable(result.toString());
				int responseCode = Integer.parseInt(responseJSON.get("responseCode").toString());
				String responseMessage = responseJSON.get("responseMessage").toString();
				DataSynchUpload dResponse = new DataSynchUpload(responseMessage, responseCode);
				responseService.successResponse(dResponse);
			}
		}catch(Exception e){
			ServiceError error = new ServiceError(
					ServiceError.TYPE_EXCEPTION,
					e.getMessage()
			);
			responseService.errorResponse(error);
		}
	}
		
	/**
	 * This parsed the loggin response 
	 **/
	private void parseServiceSynchDownlod(Object result){
		try{
			if(result == null || result.toString().length() == 0) throw new IOException("Null Connection");
			else{
				boolean rsp = ((Boolean)result).booleanValue();
				if(rsp){
					DataSynchDownload dResponse = new DataSynchDownload("Success Download", 1, "OK");
					responseService.successResponse(dResponse);
				}
				else throw new IOException("Error Data");
				
				//Log.e("JSON", result.toString());
				
				/*Hashtable<?, ?> responseJSON = JSON.decodeHashtable(result.toString());
				int responseCode = Integer.parseInt(responseJSON.get("responseCode").toString());
				String responseMessage = responseJSON.get("responseMessage").toString();
				String data = responseJSON.get("data").toString();
				DataSynchDownload dResponse = new DataSynchDownload(responseMessage, responseCode, result.toString());
				responseService.successResponse(dResponse);*/
			}
		}catch(Exception e){
			ServiceError error = new ServiceError(
					ServiceError.TYPE_EXCEPTION,
					e.getMessage()
			);
			responseService.errorResponse(error);
		}
	}
	
	/**
	 * This parsed the loggin response 
	 **/
	private void parseServiceLoggin(Object result){
		try{
			if(result == null || result.toString().length() == 0) throw new IOException("Null Connection");
			else{
				Hashtable<?, ?> responseJSON = JSON.decodeHashtable(result.toString());
				int responseCode = Integer.parseInt(responseJSON.get("responseCode").toString());
				String responseMessage = responseJSON.get("responseMessage").toString();
				String responseToken = responseJSON.get("token").toString();
				DataLoginResponse dResponse = new DataLoginResponse(responseCode, responseMessage, responseToken);
				responseService.successResponse(dResponse);
			}
		}catch(Exception e){
			ServiceError error = new ServiceError(
					ServiceError.TYPE_EXCEPTION,
					e.getMessage()
			);
			responseService.errorResponse(error);
		}
	}
	
	/**
	 * This parsed the list clients response 
	 **/
	private void parseServiceListClients(Object result){
		try{
			if(result == null || result.toString().length() == 0) throw new IOException("Null Connection");
			else{
				Hashtable<?, ?> responseJSON = JSON.decodeHashtable(result.toString());
				int responseCode = Integer.parseInt(responseJSON.get("responseCode").toString());
				String responseMessage = responseJSON.get("responseMessage").toString();
				String[] data = JSON.decodeArray(responseJSON.get("data").toString());
				DataListClientsResponse dResponse = new DataListClientsResponse(responseCode, responseMessage, data);
				responseService.successResponse(dResponse);
			}
		}catch(Exception e){
			ServiceError error = new ServiceError(
					ServiceError.TYPE_EXCEPTION,
					e.getMessage()
			);
			responseService.errorResponse(error);
		}
	}
	
}
