package com.deploy.fi.services.struct;

import java.io.Serializable;
import java.util.Hashtable;

import android.util.Log;

public class SkuColor implements Serializable {

	private SkuColor(){
	}
	
	private String Referencia = "";
	private String Talla = "";
	private String idColor = "";
	private String DescColor = "";
	private int idsku = 0;
	
	public int getIDSku(){
		return idsku;
	}
	
	public String getIDColor(){
		return idColor;
	}
	
	public String getReferencia(){
		return Referencia;
	}
	
	public String getTalla(){
		return Talla;
	}
	
	public String getDescripcionColor(){
		return DescColor;
	}
	
	public static SkuColor getInstance(Hashtable<Object,Object> params){
		SkuColor skuColor = null;
		try{
			if(params != null && params.containsKey("Referencia") && params.containsKey("idColor") && 
					params.containsKey("Talla") && params.containsKey("DescColor") && params.containsKey("idsku")){
				skuColor = new SkuColor();
				skuColor.Referencia = params.get("Referencia").toString();
				skuColor.idColor = params.get("idColor").toString();
				skuColor.Talla = params.get("Talla").toString();
				skuColor.DescColor = params.get("DescColor").toString();
				skuColor.idsku = Integer.parseInt(params.get("idsku").toString());
			}
		}catch(Exception e){
			Log.e("SkuReferencia.getInstance", e.getMessage());
			skuColor = null;
		}
		return skuColor;
	}

}
