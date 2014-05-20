package com.deploy.fi.services.struct;

import java.io.Serializable;
import java.util.Hashtable;

import android.util.Log;

public class SkuReferencia implements Serializable {

	private SkuReferencia(){
	}
	
	private String Referencia = "";
	private String DescSku = "";
	private int idsku = 0;
	
	public int getID(){
		return idsku;
	}
	
	public String getReferencia(){
		return Referencia;
	}
	
	public String getDescripcion(){
		return DescSku;
	}
	
	public static SkuReferencia getInstance(Hashtable<Object,Object> params){
		SkuReferencia skuReferencia = null;
		try{
			if(params != null && params.containsKey("idsku") && params.containsKey("Referencia") && params.containsKey("DescSku")){
				skuReferencia = new SkuReferencia();
				skuReferencia.Referencia = params.get("Referencia").toString();
				skuReferencia.DescSku = params.get("DescSku").toString();
				skuReferencia.idsku = Integer.parseInt(params.get("idsku").toString());
			}
		}catch(Exception e){
			Log.e("SkuReferencia.getInstance", e.getMessage());
			skuReferencia = null;
		}
		return skuReferencia;
	}
	
}
