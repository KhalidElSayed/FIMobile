package com.deploy.fi.services.struct;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import android.util.Log;
import android.view.View;

public class CompraRapidaItem {

	public CompraRapidaItem(){}
	
	//******************************************************************************
	
	public int getPrecioTalla(String talla){
		int precio = 0;
		if(tallas != null && skuPrecio != null && tallas.length == skuPrecio.length){
			for(int i=0; i<tallas.length; i++){
				if(tallas[i].equals(talla)){
					precio = skuPrecio[i];
					break;
				}
			}
		}
		return precio;
	}
	
	private int[] skuPrecio = null;
	
	public int[] getSkuPrecio(){
		return skuPrecio;
	}
	
	public void setSkuPrecio(int[] value){
		skuPrecio = value;
	}
	
	private int[] skuID = null;
	
	public int[] getSkuID(){
		return skuID;
	}
	
	public void setSkuID(int[] value){
		if(value != null && value.length>0) skuID = value;
	}
	
	private String referencia = null;
	
	public String getReferencia(){
		return referencia;
	}
	
	public void setReferencia(String value){
		if(value != null && value.length()>0) referencia = value;
	}
	
	private String[] tallas = null;
	
	public String[] getTallas(){
		return tallas;
	}
	
	public void setTallas(String[] value){
		if(value != null && value.length>0) tallas = value;
	}
	
	public void cleanTallas(){
		tallas = null;
	}
	
	public Vector<String> getFiltredTallas(){
		Vector<String> filtred = new Vector<String>();
		if(tallas != null && codigosColor != null && tallas.length == codigosColor.length && selectCodigosColor != null){
			if(selectCodigosColor != "..."){
				for(int i=0; i<codigosColor.length; i++){
					if(codigosColor[i].trim().equals(selectCodigosColor.trim())) {
						Log.e("found", selectCodigosColor + " -> " + codigosColor[i] +" -> "+ tallas[i]);
						filtred.add(tallas[i]);
					}
					else{
						Log.e("not found", selectCodigosColor + " -> " + codigosColor[i] +" -> "+ tallas[i]);
					}
				}
			}
		}
		return filtred;
	} 
	
	//******************************************************************************
	
	private String[] codigosColor = null;
	private String selectCodigosColor = null;
	
	public String[] getCodigosColor(){
		return codigosColor;
	}
	
	public void setCodigosColor(String[] values){
		if(values != null && values.length>0) {
			codigosColor = values;
			selectCodigosColor = null;
		}
	}
	
	public String getSelectCodigosColor(){
		return selectCodigosColor;
	}
	
	public void setSelectCodigosColor(String value){
		if(value != null && value.length()>0) selectCodigosColor = value;
	}
	
	public void cleanSelectCodigosColor(){
		codigosColor = null;
		selectCodigosColor = null;
	}
	
	//******************************************************************************
	
	private String[] colores = null;
	private String selectColor = null;
	
	public String[] getColores(){
		return colores;
	}
	
	public void setColores(String[] values){
		if(values != null && values.length>0) {
			colores = values;
			selectColor = null;
		}
	}
	
	public String getSelectColor(){
		return selectColor;
	}
	
	public void setSelectColores(String value){
		if(value != null && value.length()>0) selectColor = value;
	}
	
	public void cleanSelectColores(){
		colores = null;
		selectColor = null;
	}
	
	//******************************************************************************
	
	private Hashtable<Object, Object> AVIABLE_TALLAS = new Hashtable<Object, Object>();
	
	public void removeAviableTalla(){
		if(AVIABLE_TALLAS.size()>0) AVIABLE_TALLAS.clear();
		refViewTallas = null;
	}
	
	public void removeAviableTalla(String tallaName){
		if(tallaName != null && tallaName.length()>0){
			if(AVIABLE_TALLAS.containsKey(tallaName)) AVIABLE_TALLAS.remove(tallaName);
			refViewTallas = null;
		}
	}
	
	public boolean isAviableTalla(String tallaName){
		boolean exist = false;
		if(tallaName != null && tallaName.length()>0){
			if(AVIABLE_TALLAS.containsKey(tallaName)) exist = true;
		}
		return exist;
	}
	
	public View[] refViewTallas = null;
	
	public Hashtable<Object, Object> getRawAviableTalla(){
		return AVIABLE_TALLAS;
	}
	
	public String[] getAviableTalla(){
		String[] aviable = null;
		int size = AVIABLE_TALLAS.size();
		if(size>0){
			aviable = new String[size];
			Enumeration<Object> keys = AVIABLE_TALLAS.keys();
			int i = 0;
			while(keys.hasMoreElements()){
				aviable[i] = keys.nextElement().toString();
				i++;
			}
		}
		return aviable;
	} 
	
	public void insertRawAviableTalla(String tallaName, Hashtable<Object, Object> props){
		if(tallaName != null && tallaName.length()>0 && props != null && props.size()>0) AVIABLE_TALLAS.put(tallaName, props);
	}
	
	public boolean addAviableTalla(String tallaName){
		boolean adding = false;
		if(tallaName != null && tallaName.length()>0){
			Hashtable<Object, Object> talla = null;
			if(!AVIABLE_TALLAS.containsKey(tallaName)){
				talla = new Hashtable<Object, Object>();
				talla.put("createdField", 0);
				talla.put("talla", tallaName);
				talla.put("visible", 1);
				talla.put("count", 0);
				AVIABLE_TALLAS.put(tallaName, talla);
				adding = true;
			}
			else{
				talla = (Hashtable<Object, Object>)AVIABLE_TALLAS.get(tallaName);
				talla.put("talla", tallaName);
				talla.put("visible", 1);
				talla.put("count", 0);
				AVIABLE_TALLAS.put(tallaName, talla);
				adding = true;
			}
		}
		return adding;
	}
	
	public boolean isCreatedFieldTalla(String tallaName){
		boolean visible = false;
		if(tallaName != null && tallaName.length()>0){
			if(AVIABLE_TALLAS.containsKey(tallaName)){
				Hashtable<Object, Object> talla = (Hashtable<Object, Object>)AVIABLE_TALLAS.get(tallaName);
				int v = Integer.parseInt(talla.get("createdField").toString());
				if(v == 1) visible = true;
			}
		}
		return visible;
	}
	
	public void setCreatedFieldTalla(String tallaName, boolean created){
		if(tallaName != null && tallaName.length()>0){
			if(AVIABLE_TALLAS.containsKey(tallaName)){
				Hashtable<Object, Object> talla = (Hashtable<Object, Object>)AVIABLE_TALLAS.get(tallaName);
				if(created) talla.put("createdField", 1);
				else talla.put("createdField", 0);
				AVIABLE_TALLAS.put(tallaName, talla);
			}
		}
	}
	
	public boolean isVisbleTalla(String tallaName){
		boolean visible = false;
		if(tallaName != null && tallaName.length()>0){
			if(AVIABLE_TALLAS.containsKey(tallaName)){
				Hashtable<Object, Object> talla = (Hashtable<Object, Object>)AVIABLE_TALLAS.get(tallaName);
				int v = Integer.parseInt(talla.get("visible").toString());
				if(v == 1) visible = true;
			}
		}
		return visible;
	}
	
	public void setVisbleTalla(String tallaName, boolean visible){
		if(tallaName != null && tallaName.length()>0){
			if(AVIABLE_TALLAS.containsKey(tallaName)){
				Hashtable<Object, Object> talla = (Hashtable<Object, Object>)AVIABLE_TALLAS.get(tallaName);
				if(visible) talla.put("visible", 1);
				else talla.put("visible", 0);
				AVIABLE_TALLAS.put(tallaName, talla);
			}
		}
	}
	
	public void setCountTalla(String tallaName, int count){
		if(count <= 0) count = 0;
		if(tallaName != null && tallaName.length()>0){
			if(AVIABLE_TALLAS.containsKey(tallaName)){
				Hashtable<Object, Object> talla = (Hashtable<Object, Object>)AVIABLE_TALLAS.get(tallaName);
				talla.put("count", count);
				AVIABLE_TALLAS.put(tallaName, talla);
			}
		}
	}
	
	public int getCountTalla(String tallaName){
		int count = 0;
		if(tallaName != null && tallaName.length()>0){
			if(AVIABLE_TALLAS.containsKey(tallaName)){
				Hashtable<Object, Object> talla = (Hashtable<Object, Object>)AVIABLE_TALLAS.get(tallaName);
				count = Integer.parseInt(talla.get("count").toString());
			}
		}
		return count;
	}
	
	
	//******************************************************************************
	
	/*public static int parseTalla(String value){
		int talla = -1;
		if(value != null && value.length()>0){
			value = value.trim().toUpperCase();
			if(value.equals("XS") || value == "XS") talla = TYPE_XS;
			else if(value.equals("S") || value == "S") talla = TYPE_S;
			else if(value.equals("M") || value == "M") talla = TYPE_M;
			else if(value.equals("L") || value == "L") talla = TYPE_L;
			else if(value.equals("XL") || value == "XL") talla = TYPE_XL;
			else if(value.equals("XXL") || value == "XXL") talla = TYPE_XXL;
			else if(value.equals("XXS") || value == "XXS") talla = TYPE_XXS;
			else if(value.equals("UNICA") || value == "UNICA") talla = TYPE_UNICA;
		}
		return talla;
	}
	
	private int tallaXS = 0;
	private boolean visibleTallaXS = false;
	
	private int tallaS = 0;
	private boolean visibleTallaS = false;
	
	private int tallaM = 0;
	private boolean visibleTallaM = false;
	
	private int tallaL = 0;
	private boolean visibleTallaL = false;
	
	private int tallaXL = 0;
	private boolean visibleTallaXL = false;
	
	private int tallaXXL = 0;
	private boolean visibleTallaXXL = false;
	
	private int tallaXXS = 0;
	private boolean visibleTallaXXS = false;
	
	private int tallaUNICA = 0;
	private boolean visibleTallaUNICA = false;
	
	public final static int TYPE_XS = 0;
	public final static int TYPE_S = 1;
	public final static int TYPE_M = 2;
	public final static int TYPE_L = 3;
	public final static int TYPE_XL = 4;
	public final static int TYPE_XXL = 5;
	public final static int TYPE_XXS = 6;
	public final static int TYPE_UNICA = 7;
	
	public void setVisibleTalla(boolean visible, int type){
		switch(type){
			case TYPE_XS:{
				visibleTallaXS = visible;
				tallaXS = 0;
				break;
			}
			case TYPE_S:{
				visibleTallaS = visible;
				tallaS = 0;
				break;
			}
			case TYPE_M:{
				visibleTallaM = visible;
				tallaM = 0;
				break;
			}
			case TYPE_L:{
				visibleTallaL = visible;
				tallaL = 0;
				break;
			}
			case TYPE_XL:{
				visibleTallaXL = visible;
				tallaXL = 0;
				break;
			}
			case TYPE_XXL:{
				visibleTallaXXL = visible;
				tallaXXL = 0;
				break;
			}
			case TYPE_XXS:{
				visibleTallaXXS = visible;
				tallaXXS = 0;
				break;
			}
			case TYPE_UNICA:{
				visibleTallaUNICA = visible;
				tallaUNICA = 0;
				break;
			}
		}
	}
	
	public boolean getVisibleTalla(int type){
		boolean value = false;
		switch(type){
			case TYPE_XS:{
				value = visibleTallaXS;
				break;
			}
			case TYPE_S:{
				value = visibleTallaS;
				break;
			}
			case TYPE_M:{
				value = visibleTallaM;
				break;
			}
			case TYPE_L:{
				value = visibleTallaL;
				break;
			}
			case TYPE_XL:{
				value = visibleTallaXL;
				break;
			}
			case TYPE_XXL:{
				value = visibleTallaXXL;
				break;
			}
			case TYPE_XXS:{
				value = visibleTallaXXS;
				break;
			}
			case TYPE_UNICA:{
				value = visibleTallaUNICA;
				break;
			}
		}
		return value;
	}
	
	public void setTalla(int type, int value){
		if(value<0) value = 0;
		switch(type){
			case TYPE_XS:{
				visibleTallaXS = true;
				tallaXS = value;
				break;
			}
			case TYPE_S:{
				visibleTallaS = true;
				tallaS = value;
				break;
			}
			case TYPE_M:{
				visibleTallaM = true;
				tallaM = value;
				break;
			}
			case TYPE_L:{
				visibleTallaL = true;
				tallaL = value;
				break;
			}
			case TYPE_XL:{
				visibleTallaXL = true;
				tallaXL = value;
				break;
			}
			case TYPE_XXL:{
				visibleTallaXXL = true;
				tallaXXL = value;
				break;
			}
			case TYPE_XXS:{
				visibleTallaXXS = true;
				tallaXXS = value;
				break;
			}
			case TYPE_UNICA:{
				visibleTallaUNICA = true;
				tallaUNICA = value;
				break;
			}
		}
	}
	
	public int getTalla(int type){
		int value = 0;
		switch(type){
			case TYPE_XS:{
				value = tallaXS;
				break;
			}
			case TYPE_S:{
				value = tallaS;
				break;
			}
			case TYPE_M:{
				value = tallaM;
				break;
			}
			case TYPE_L:{
				value = tallaL;
				break;
			}
			case TYPE_XL:{
				value = tallaXL;
				break;
			}
			case TYPE_XXL:{
				value = tallaXXL;
				break;
			}
			case TYPE_XXS:{
				value = tallaXXS;
				break;
			}
			case TYPE_UNICA:{
				value = tallaUNICA;
				break;
			}
		}
		return value;
	}*/
	
	//******************************************************************************
	
}
