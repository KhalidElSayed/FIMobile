package com.deploy.fi.services.struct;

public interface ICompraRapidaItem {

	public void removeFila(int indexItem);
	
	public void updateTalla(String tallaName, int value, int indexItem);
	
	public void updateReferencia(String value, int indexItem);
	
	public void updateCodigoColor(String value, int indexItem);
	
	public void updateColor(String value, int indexItem);
	
	public void focusColor(int indexItem);
	
}
