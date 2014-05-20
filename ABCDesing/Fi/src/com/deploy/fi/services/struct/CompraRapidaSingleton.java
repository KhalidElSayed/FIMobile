package com.deploy.fi.services.struct;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import com.deploy.fi.services.JSON;
import com.deploy.fi.tools.FileManager;

import android.content.Context;
import android.util.Log;

public class CompraRapidaSingleton {

	private static CompraRapidaSingleton INSTANCE = new CompraRapidaSingleton();
	 
    private CompraRapidaSingleton() {}
 
    public static CompraRapidaSingleton getInstance() {
        return INSTANCE;
    }
    
    //***************************************************************************
    
    private Vector<CompraRapidaItem> array = new Vector<CompraRapidaItem>();
    
    public Vector<CompraRapidaItem> getArray(){
    	return array;
    }
    
    public void updateItem(int index, CompraRapidaItem item){
    	if(item != null) array.set(index, item);
    }
    
    public void addItem(CompraRapidaItem item){
    	if(item != null) array.add(item);
    }
    
    public CompraRapidaItem getItem(int index){
    	return array.get(index);
    }
    
    public void removeItem(int index){
    	array.remove(index); 
    }
    
    public void removeAll(){
    	array.removeAllElements(); 
    }
    
    public int size(){
    	return array.size(); 
    }
    
    //***************************************************************************
    
    public void deleteFromJson(Context context, int idPedido){
    	String fileName = String.valueOf(idPedido)+".json";
		String congeladosPath = FileManager.fixPath("congelados");
		if(FileManager.existFile(context, congeladosPath, fileName)) FileManager.deleteFile(context, congeladosPath, fileName);
    }
    
    public void loadFromJson(Context context, int idPedido){
    	try{
    		if(array.size()>0) array.removeAllElements();
    		
    		String json = null;
    		String fileName = String.valueOf(idPedido)+".json";
    		String congeladosPath = FileManager.fixPath("congelados");
    		if(FileManager.existFile(context, congeladosPath, fileName)) json = FileManager.readFile(context, congeladosPath, fileName);
    		
    		if(json != null && json.length()>0){
    			String[] items = JSON.decodeArray(json);
    			if(items != null && items.length>0){
    				for(int i=0; i<items.length; i++){
    					Hashtable<Object, Object> item = JSON.decodeHashtable(items[i]);
    					
    					CompraRapidaItem itemSingleton = new CompraRapidaItem();
    					itemSingleton.setReferencia(item.get("Referencia").toString());
    					
    					itemSingleton.setSkuPrecio(JSON.parseIntArray(item.get("SkuPrecio").toString()));
    					itemSingleton.setSkuID(JSON.parseIntArray(item.get("SkuID").toString()));
    					itemSingleton.setColores(JSON.decodeArray(item.get("Colores").toString()));
    					itemSingleton.setCodigosColor(JSON.decodeArray(item.get("CodigosColor").toString()));
    					itemSingleton.setTallas(JSON.decodeArray(item.get("Tallas").toString()));
    					itemSingleton.setSelectCodigosColor(item.get("SelectCodigosColor").toString());
    					itemSingleton.setSelectColores(item.get("SelectColor").toString());
    					
    					String[] AviableTalla = JSON.decodeArray(item.get("AviableTalla").toString());
    					if(AviableTalla != null && AviableTalla.length>0){
    						for(int j=0; j<AviableTalla.length; j++){
    							Hashtable<Object, Object> AviableTallaItem = JSON.decodeHashtable(AviableTalla[j]);
    							Enumeration<Object> e = AviableTallaItem.keys();
    							while(e.hasMoreElements()){
    								String NombreTalla = e.nextElement().toString();
    								Hashtable<Object, Object> ItemDetails = JSON.decodeHashtable(AviableTallaItem.get(NombreTalla).toString());
    								itemSingleton.insertRawAviableTalla(NombreTalla, ItemDetails);
    							}
    						}
    					}
    					array.add(itemSingleton);
    				}
    			}
    		}
    	}catch(Exception e){}
    }
    
    public void saveToJson(Context context, int idPedido){
    	JSONArray jsonArrayItems = new JSONArray();
    	try{
    		int sizeArray = array.size();
    		if(sizeArray>0){
    			for(int indexArray=0; indexArray<sizeArray; indexArray++){
    				CompraRapidaItem item = array.get(indexArray);
    				
    				JSONObject jsonItem = new JSONObject();
    				
    				jsonItem.put("SelectCodigosColor", item.getSelectCodigosColor());
    				jsonItem.put("SelectColor", item.getSelectColor());
    				jsonItem.put("Referencia", item.getReferencia());
    				
    				JSONArray arrayTmp = JSON.parseArray(item.getCodigosColor());
    				jsonItem.put("CodigosColor", arrayTmp);
    				
    				arrayTmp = JSON.parseArray(item.getColores());
    				jsonItem.put("Colores", arrayTmp);
    				
    				arrayTmp = JSON.parseArray(item.getSkuPrecio());
    				jsonItem.put("SkuPrecio", arrayTmp);
    				
    				arrayTmp = JSON.parseArray(item.getTallas());
    				jsonItem.put("Tallas", arrayTmp);
    				
    				arrayTmp = JSON.parseArray(item.getSkuID());
    				jsonItem.put("SkuID", arrayTmp);
    				
    				arrayTmp = new JSONArray();
    				
    				Hashtable<Object, Object> AviableTalla = item.getRawAviableTalla();
    				if(AviableTalla != null && AviableTalla.size()>0){
    					Enumeration<Object> e = AviableTalla.keys();
    					while(e.hasMoreElements()){
    						String key = e.nextElement().toString();
    						Hashtable<Object, Object> Talla = (Hashtable<Object, Object>)AviableTalla.get(key);
    						Talla.put("createdField", 0);
    						JSONObject objectTmp = JSON.parseTable(key, Talla);
    						arrayTmp.put(objectTmp);
    					}
    				}
    				jsonItem.put("AviableTalla", arrayTmp);
    				AviableTalla = null;
    				arrayTmp = null;
    				
    				jsonArrayItems.put(jsonItem);
    			}
    		}
    	}catch(Exception e){}
    	
    	try{
    		String fileName = String.valueOf(idPedido)+".json";
    		String congeladosPath = FileManager.fixPath("congelados");
    		if(!FileManager.existFolder(context, congeladosPath)) FileManager.createFolder(context, congeladosPath);
    		if(FileManager.existFile(context, congeladosPath, fileName)) FileManager.deleteFile(context, congeladosPath, fileName); 
    		String json = jsonArrayItems.toString();
    		FileManager.createFile(context, congeladosPath, fileName, json);
			Log.e("json", json);
    	}catch(Exception e){}
    }
    
    /*
     * JSONObject objJSON = new JSONObject();
		
		JSONArray objPedido = new JSONArray();*/
    
    //***************************************************************************
	
}
